#ifndef REFINPUTDIAG_H
#define REFINPUTDIAG_H

#include <QDialog>
#include <iostream>

namespace Ui {
class RefInputDiag;
}

class RefInputDiag : public QDialog
{
    Q_OBJECT

public:
    explicit RefInputDiag(float* pos,float* att, QWidget *parent = 0);
    ~RefInputDiag();

private:
    float* posVec;
    float* attVec;

    Ui::RefInputDiag *ui;

public slots:
    void accepted(void);
    void rejected(void);
};

#endif // REFINPUTDIAG_H
