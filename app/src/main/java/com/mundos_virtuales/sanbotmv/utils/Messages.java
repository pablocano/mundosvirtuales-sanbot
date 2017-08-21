package com.mundos_virtuales.sanbotmv.utils;

import android.content.Context;
import android.widget.Toast;

public class Messages {

    static public void showToast(String message, Context context) {
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }
}
