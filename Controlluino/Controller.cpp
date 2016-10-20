
#include "Controller.h"
#include "Arduino.h"

float rho[6] = {0,0,0,0,0,0};
float ur[6] = {0,0,0,0,0,0};
float Pref[6] = {0,0,0,0,0,0};
float kd = 0.1;

float qe[4] = {1,0,0,0};
float qd[4] = {1,0,0,0};

float Jxx = 0.1193;
float Jyy = 0.1312;
float Jzz = 0.2313;

float input_map[6][6] = {{            0, 2.215644659e-14, 12772.32187, -9.094947018e-13,  8940.625307, 97.62092212},
    {            0, 2.215644659e-14, 12772.32187,     -7742.808642, -4470.312654, 97.62092212},
    {            0, 2.215644659e-14, 12772.32187,      7742.808642, -4470.312654, 97.62092212},
    {            0,    -25544.64374,           0,                0, -195.2418442, 4470.312654},
    {  22122.31041,     12772.32187,           0,       169.084397,  97.62092212, 4470.312654},
    { -22122.31041,     12772.32187,           0,      -169.084397,  97.62092212, 4470.312654}};

void Controller(float* u, float* quat, float* gyro, float* pos, float* vel, float* acc, float* ref, float* tfb) {
    if(true) {
        prefilter(ref, Pref);
        euler2quat(Pref, qd);
        calcQuatError(qe,qd,quat);
        
        float p = *(gyro+0);
        float q = *(gyro+1);
        float r = *(gyro+2);
        
        float ne0 = *(qe+0);
        float ne1 = *(qe+1);
        float ne2 = *(qe+2);
        float ne3 = *(qe+3);
        
        if(ne0!=0) {
            digitalWrite(13,LOW);
            
            *(tfb+0) = kd*p + (Jzz - Jyy)*q*r - (Jxx*ne1*(p*p+q*q+r*r))/(2*ne0);
            *(tfb+1) = kd*q + (Jxx - Jzz)*p*r - (Jyy*ne2*(p*p+q*q+r*r))/(2*ne0);
            *(tfb+2) = kd*r + (Jyy - Jxx)*p*q - (Jzz*ne3*(p*p+q*q+r*r))/(2*ne0);

            float cp = 6.0; // Proportional Control
            float cd = 5.0; // Derivative Control
            
            *(tfb+0) += (Jxx*(2*cp*ne1 + cd*ne0*p))/ne0;
            *(tfb+1) += (Jyy*(2*cp*ne2 + cd*ne0*q))/ne0;
            *(tfb+2) += (Jzz*(2*cp*ne3 + cd*ne0*r))/ne0;
            
            for (int i=3; i<6; i++)  *(tfb+i) = *(Pref+i);
            
            calcAW(u, tfb);
        }
    } else {
        calcAW(u, ref);
    }
}

void euler2quat(float* euler, float* quat) {
    *(quat+0) = cos(*(euler+0))*cos(*(euler+1))*cos(*(euler+2))+sin(*(euler+0))*sin(*(euler+1))*sin(*(euler+2));
    *(quat+1) = sin(*(euler+0))*cos(*(euler+1))*cos(*(euler+2))-cos(*(euler+0))*sin(*(euler+1))*sin(*(euler+2));
    *(quat+2) = cos(*(euler+0))*sin(*(euler+1))*cos(*(euler+2))+sin(*(euler+0))*cos(*(euler+1))*sin(*(euler+2));
    *(quat+3) = cos(*(euler+0))*cos(*(euler+1))*sin(*(euler+2))-sin(*(euler+0))*sin(*(euler+1))*cos(*(euler+2));
}

void calcQuatError(float* error, float* desired, float* current) {
    *(error+0)=*(desired+0)**(current+0) + *(desired+1)**(current+1) + *(desired+2)**(current+2) + *(desired+3)**(current+3);
    *(error+1)=*(desired+1)**(current+0) - *(desired+0)**(current+1) + *(desired+2)**(current+3) - *(desired+3)**(current+2);
    *(error+2)=*(desired+2)**(current+0) - *(desired+0)**(current+2) - *(desired+1)**(current+3) + *(desired+3)**(current+1);
    *(error+3)=*(desired+1)**(current+2) - *(desired+0)**(current+3) - *(desired+2)**(current+1) + *(desired+3)**(current+0);
}

void calcAW(float* aw, float* tf) {
    for (int i=0; i<6; i++) *(rho+i) = 0;
    
    for (int i=0; i<6; i++)
        for (int j=0; j<6; j++)
            *(rho+i) += input_map[i][j]**(tf+j);
    
    for (int i=0; i<3; i++) {
        *(aw+i) = sqrt(sqrt(*(rho+i)**(rho+i)+*(rho+i+3)**(rho+i+3)));
        *(aw+i+3) = atan2(*(rho+i),*(rho+i+3));
    }
//    prefilter(ur, aw);
}

void shiftArray(float* a, int s, float* n) {
    for (int i=(s-1); i>0; i--) *(a+i) = *(a+i-1);
    *(a+0) = *n;
}

/*
 2nd order prefilter
 Adjust filterDen and filterNum to tune
 
 100
 G(s) = ----------------
 s^2 + 20 s + 100
 
 0.01752 z^-1 + 0.01534 z^-2
 G(z) = ---------------------------- (h=0.02)
 0.6703 z^-2 - 1.637 z^-1 + 1
 
 y(k) - 1.637*y(k-1) + 0.6703*y(k-2) = 0.01752*u(k-1) + 0.01534*u(k-2)
 y(k) = 1.637*y(k-1) - 0.6703*y(k-2) + 0.01752*u(k-1) + 0.01534*u(k-2)
 */

float filterDen[2] = {1.637, -0.6703};
float filterNum[2] = {0.01752, 0.01534};

float prevR[6][2] = {{0,0},
    {0,0},
    {0,0},
    {0,0},
    {0,0},
    {0,0}};

float prevPr[6][2] = {{0,0},
    {0,0},
    {0,0},
    {0,0},
    {0,0},
    {0,0}};

void prefilter(float *r, float* Pr) {
    for(int i=0;i<6;i++) {
        Pr[i] = 0;
        for (int j=0; j<2; j++)
            Pr[i] += filterDen[j]*prevPr[i][j]+filterNum[j]*prevR[i][j];
        
        shiftArray(prevR[i],2,(r+i));
        shiftArray(prevPr[i],2,(Pr+i));
    }
}


