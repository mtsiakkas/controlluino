#include "refinputdiag.h"
#include "ui_refinputdiag.h"

RefInputDiag::RefInputDiag(float* pos, float* att, QWidget *parent) :
    QDialog(parent),
    ui(new Ui::RefInputDiag)
{
    ui->setupUi(this);
    posVec = pos;
    attVec = att;

}

RefInputDiag::~RefInputDiag()
{
    delete ui;
}

void RefInputDiag::accepted(void) {
    float posTmp[3] = {0,0,0};
    float attTmp[3] = {0,0,0};
    try {
        posTmp[0] = std::stof(ui->txtPos1->text().toStdString());
        posTmp[1] = std::stof(ui->txtPos2->text().toStdString());
        posTmp[2] = std::stof(ui->txtPos3->text().toStdString());
        attTmp[0] = std::stof(ui->txtAtt1->text().toStdString());
        attTmp[1] = std::stof(ui->txtAtt2->text().toStdString());
        attTmp[2] = std::stof(ui->txtAtt3->text().toStdString());
    } catch(...) {
        std::cout << "Unable to parse reference input!" << std::endl;
        return;
    }

    for(int i=0;i<3;i++) {
        *(posVec+i) = *(posTmp+i);
        *(attVec+i) = *(attTmp+i);
    }
}

void RefInputDiag::rejected(void) {

}
