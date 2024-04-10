package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try{
            Bundle bundle = intent.getExtras();
            ArrayMap<String,String> arrayMap = new ArrayMap<>();
            if (bundle == null)  return;

            for (String key : bundle.keySet()) {
                String value = (bundle.get(key) != null ? (String) bundle.get(key) : "NULL");
                arrayMap.put(key,value);
                }


            String message = intent.getStringExtra("COLGAR_ACTION");
//            Log.d("onReceive",message);
            if(message != null){
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex){
            Log.e("Ex",ex.getMessage());
        }


    }
}
