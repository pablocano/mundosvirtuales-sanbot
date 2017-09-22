package com.mundos_virtuales.sanbotmv;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.mundos_virtuales.sanbotmv.utils.Messages;
import com.qihancloud.opensdk.base.TopBaseActivity;
import com.qihancloud.opensdk.beans.FuncConstant;
import com.qihancloud.opensdk.function.unit.SystemManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EnterUserActivity  extends TopBaseActivity {

    @Bind(R.id.tvRut)
    TextView tvRut;

    String mRut = "";

    @OnClick(R.id.btnRut0)
    public void onBtn0(){
        appendDigit("0");
    }

    @OnClick(R.id.btnRut1)
    public void onBtn1(){
        appendDigit("1");
    }

    @OnClick(R.id.btnRut2)
    public void onBtn2(){
        appendDigit("2");
    }

    @OnClick(R.id.btnRut3)
    public void onBtn3(){
        appendDigit("3");
    }

    @OnClick(R.id.btnRut4)
    public void onBtn4(){
        appendDigit("4");
    }

    @OnClick(R.id.btnRut5)
    public void onBtn5(){
        appendDigit("5");
    }

    @OnClick(R.id.btnRut6)
    public void onBtn6(){
        appendDigit("6");
    }

    @OnClick(R.id.btnRut7)
    public void onBtn7(){
        appendDigit("7");
    }

    @OnClick(R.id.btnRut8)
    public void onBtn8(){
        appendDigit("8");
    }

    @OnClick(R.id.btnRut9)
    public void onBtn9(){
        appendDigit("9");
    }

    @OnClick(R.id.btnRutMinus)
    public void onBtnMinus(){
        appendDigit("-");
    }

    @OnClick(R.id.btnRutK)
    public void onBtnK(){
        appendDigit("K");
    }

    @OnClick(R.id.btnOkRutCancel)
    public void onCancel(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @OnClick(R.id.btnOkRut)
    public void onOkRut() {
        String rut = tvRut.getText().toString();

        if(validateRut(rut)) {
            Intent intent = new Intent(this, OptionsActivity.class);
            intent.putExtra("rut", rut);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            Messages.showToast("Rut Incorrecto", getApplicationContext());
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_user);

        ((SystemManager) getUnitManager(FuncConstant.SYSTEM_MANAGER)).switchFloatBar(false, EnterUserActivity.class.getName());

        ButterKnife.bind(this);
    }

    @Override
    protected void onMainServiceConnected() {

    }

    public void appendDigit(String d){
        if(mRut.length() < 12)
        {
            mRut += d;
            tvRut.setText(formatRut(mRut));
        }
    }

    public static String formatRut(String rut) {

        int cont = 0;
        String format;
        rut = rut.replace(".", "");
        rut = rut.replace("-", "");
        format = "-" + rut.substring(rut.length() - 1);
        for (int i = rut.length() - 2; i >= 0; i--) {
            format = rut.substring(i, i + 1) + format;
            cont++;
            if (cont == 3 && i != 0) {
                format = "." + format;
                cont = 0;
            }
        }
        return format;
    }

    public static boolean validateRut(String rut) {

        boolean validation = false;
        try {
            rut =  rut.toUpperCase();
            rut = rut.replace(".", "");
            rut = rut.replace("-", "");
            int rutAux = Integer.parseInt(rut.substring(0, rut.length() - 1));

            char dv = rut.charAt(rut.length() - 1);

            int m = 0, s = 1;
            for (; rutAux != 0; rutAux /= 10) {
                s = (s + rutAux % 10 * (9 - m++ % 6)) % 11;
            }
            if (dv == (char) (s != 0 ? s + 47 : 75)) {
                validation = true;
            }

        } catch (java.lang.NumberFormatException e) {
        } catch (Exception e) {
        }
        return validation;
    }
}
