package com.mundos_virtuales.sanbotmv;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Locale;

/**
 * Created by PabloCano on 14/08/2017.
 */

public class SensorItemAdapter extends BaseAdapter {
    Context context;
    int sensors_values[];
    LayoutInflater inflater;

    public SensorItemAdapter(Context context){
        this.context = context;
        this.sensors_values = new int[20];
        inflater = (LayoutInflater.from(context));
    }

    @Override
    public int getCount(){
        return sensors_values.length;
    }

    @Override
    public Object getItem(int i){
        return null;
    }

    @Override
    public long getItemId(int i){
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup){
        view = inflater.inflate(R.layout.sensor, null);
        TextView sensorName = view.findViewById(R.id.sensor_name);
        TextView sensorValue = view.findViewById(R.id.sensor_value);
        sensorName.setText(String.format(Locale.ENGLISH, "Sensor number %d",i));
        sensorValue.setText(String.format(Locale.ENGLISH, "%d mts.",sensors_values[i]));
        return view;
    }

    void setSensorValue(int sensor, int distance){
        if (sensor >= 20)
        {
            return;
        }
        sensors_values[sensor] = distance;
    }
}
