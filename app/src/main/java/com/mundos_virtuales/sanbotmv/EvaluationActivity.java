package com.mundos_virtuales.sanbotmv;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import com.qihancloud.opensdk.base.TopBaseActivity;
import com.qihancloud.opensdk.beans.FuncConstant;
import com.qihancloud.opensdk.function.unit.SystemManager;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class EvaluationActivity extends TopBaseActivity {

    @OnClick(R.id.btnGetNumberReturn)
    public void onReturn() {
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ((SystemManager) getUnitManager(FuncConstant.SYSTEM_MANAGER)).switchFloatBar(false, MainActivity.class.getName());
        startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        ((SystemManager) getUnitManager(FuncConstant.SYSTEM_MANAGER)).switchFloatBar(false, EvaluationActivity.class.getName());

        ButterKnife.bind(this);
    }

    @Override
    protected void onMainServiceConnected() {

    }
}
