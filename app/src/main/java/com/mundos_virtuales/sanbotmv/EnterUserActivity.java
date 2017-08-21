package com.mundos_virtuales.sanbotmv;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import com.mundos_virtuales.sanbotmv.utils.Messages;
import com.qihancloud.opensdk.base.TopBaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EnterUserActivity  extends TopBaseActivity {

    @Bind(R.id.etRut)
    EditText etRut;

    @OnClick(R.id.btnOkRut)
    public void onOkRut() {
        String rut = etRut.getText().toString();

        if(validateRut(rut)) {
            Intent intent = new Intent(this, OptionsActivity.class);
            intent.putExtra("rut", rut);
            startActivity(intent);
        } else {
            Messages.showToast("Rut Incorrecto", getApplicationContext());
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_user);

        ButterKnife.bind(this);
    }

    @Override
    protected void onMainServiceConnected() {

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
