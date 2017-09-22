package com.mundos_virtuales.sanbotmv;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import com.qihancloud.opensdk.base.TopBaseActivity;
import com.qihancloud.opensdk.beans.FuncConstant;
import com.qihancloud.opensdk.function.unit.SystemManager;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class OptionsActivity extends TopBaseActivity {

    String mRut;

    @OnClick(R.id.btnOptionCancel)
    public void onCancel(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ((SystemManager) getUnitManager(FuncConstant.SYSTEM_MANAGER)).switchFloatBar(false, MainActivity.class.getName());
        startActivity(intent);
    }

    @OnClick(R.id.btnOptionReturn)
    public void onReturn(){
        super.finish();
    }

    private void goOption(int option){
        Intent intent = new Intent(this, GetNumberActivity.class);
        intent.putExtra("option", option);
        intent.putExtra("rut", mRut);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ((SystemManager) getUnitManager(FuncConstant.SYSTEM_MANAGER)).switchFloatBar(false, GetNumberActivity.class.getName());
        startActivity(intent);
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

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        ((SystemManager) getUnitManager(FuncConstant.SYSTEM_MANAGER)).switchFloatBar(false, OptionsActivity.class.getName());

        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();

        mRut = bundle.getString("rut");
    }

    @Override
    protected void onMainServiceConnected() {

    }
}
