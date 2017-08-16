package com.mundos_virtuales.sanbotmv;

import android.content.Intent;
import android.view.View;
import android.os.Bundle;
import android.widget.GridView;

import com.qihancloud.opensdk.base.TopBaseActivity;
import com.qihancloud.opensdk.beans.FuncConstant;
import com.qihancloud.opensdk.function.unit.HardWareManager;
import com.qihancloud.opensdk.function.unit.interfaces.hardware.InfrareListener;


public class SensorsViewerActivity extends TopBaseActivity {

    GridView sensorsGrid;
    HardWareManager hardWareManager;
    SensorItemAdapter sensorItemAdapter;

    Thread updateGrid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hardWareManager = (HardWareManager) getUnitManager(FuncConstant.HARDWARE_MANAGER);

        setContentView(R.layout.activity_sensors_viewer);

        sensorsGrid = (GridView) findViewById(R.id.sensorsGridView);
        sensorItemAdapter = new SensorItemAdapter(getApplicationContext());
        sensorsGrid.setAdapter(sensorItemAdapter);

        initSensorListeners();

        updateGrid = new Thread( new UpdateGridHelper());
    }

    @Override
    protected void onMainServiceConnected() {
        updateGrid.start();
    }

    public void backButton(View view){
        Intent mainActivity = new Intent(this, MainActivity.class);
        startActivity(mainActivity);
    }

    private void initSensorListeners(){
        hardWareManager.setOnHareWareListener(new InfrareListener() {
            @Override
            public void infrareDistance(int part, int distance) {
                sensorItemAdapter.setSensorValue(part,distance);
            }
        });
    }

    class UpdateGridHelper implements Runnable{
        @Override
        public void run(){
            while (true){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sensorItemAdapter.notifyDataSetChanged();
                    }
                });

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onStop(){
        super.onStop();
    }
}
