package com.mundos_virtuales.sanbotmv;

import android.content.Intent;
import android.os.Bundle;

import com.qihancloud.opensdk.base.TopBaseActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class OptionsActivity extends TopBaseActivity {

    String mRut;

    @OnClick(R.id.btnOptionCancel)
    public void onCancel(){
        Intent intentMainActivity = new Intent(this, MainActivity.class);
        startActivity(intentMainActivity);
    }

    @OnClick(R.id.btnOptionReturn)
    public void onReturn(){
        super.finish();
    }

    private void goOption(int option){
        Intent intentOption = new Intent(this, GetNumberActivity.class);
        intentOption.putExtra("option", option);
        intentOption.putExtra("rut", mRut);
        startActivity(intentOption);
    }

    @OnClick(R.id.btnOption1)
    public void onOption1(){
        goOption(1);
    }

    @OnClick(R.id.btnOption2)
    public void onOption2(){
        goOption(2);
    }

    @OnClick(R.id.btnOption3)
    public void onOption3(){
        goOption(3);
    }

    @OnClick(R.id.btnOption4)
    public void onOption4(){
        goOption(4);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();

        mRut = bundle.getString("rut");
    }

    @Override
    protected void onMainServiceConnected() {

    }
}
