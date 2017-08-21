package com.mundos_virtuales.sanbotmv;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.qihancloud.opensdk.base.TopBaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GetNumberActivity extends TopBaseActivity {

    String mRut;
    int mOption;
    int mState = 0;
    int mNumber = 0;

    @Bind(R.id.btnGetNumber)
    Button btnGetNumber;

    @Bind(R.id.tvCaptionGetNumber)
    TextView tvCaptionGetNumber;

    @Bind(R.id.tvFooterGetNumber)
    TextView tvFooterGetNumber;

    @Bind(R.id.tvNumber)
    TextView tvNumber;

    @OnClick(R.id.btnGetNumber)
    public void onGetNumber(){
        mNumber = (int)(Math.random() * 10);
        mState = 1;
        tvNumber.setText(String.format("%d", mNumber));
        btnGetNumber.setText("Finalizar");
        tvCaptionGetNumber.setText("Retire su número");
        btnGetNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancel();
            }
        });
    }

    @OnClick(R.id.btnGetNumberMap)
    public void onMap(){
        Intent intentMainActivity = new Intent(this, MainActivity.class);
        startActivity(intentMainActivity);
    }

    @OnClick(R.id.btnGetNumberCancel)
    public void onCancel(){
        Intent intentMainActivity = new Intent(this, MainActivity.class);
        startActivity(intentMainActivity);
    }

    @OnClick(R.id.btnGetNumberReturn)
    public void onReturn(){
        super.finish();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_number);

        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();

        mRut = bundle.getString("rut");
        mOption = bundle.getInt("option");

        tvNumber.setText("---");
        tvFooterGetNumber.setText("Cliente " + mRut);
    }

    @Override
    protected void onMainServiceConnected() {

    }
}
