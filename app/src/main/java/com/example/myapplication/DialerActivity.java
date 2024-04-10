package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class DialerActivity extends AppCompatActivity {
    private AlphaAnimation btnAnimation = new AlphaAnimation(1F,0.8F);
    private ImageView iv = null;
    private TextView tvCaller = null;

    private MyConnectionService myConnectionService = null;
    BroadcastReceiver mBroadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialer2);
        // Get extras
        String phoneNumber = getIntent().getStringExtra("PHONE_NUMBER");

        // TextView Contacto
        this.tvCaller = findViewById(R.id.tvCaller);
        this.tvCaller.setText(phoneNumber);
        // ImageView
        this.iv = findViewById(R.id.imageView);
        this.iv.setImageResource(R.drawable.usuario);

        // MyConnectionService
        myConnectionService = new MyConnectionService(this);

        // BroadcastReceiver
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                    finish();
            }
        };

        registerReceiver(mBroadcastReceiver, new IntentFilter("com.close.activity"));

        // Funcionalidad del boton colgar
        Button btnColgar = findViewById(R.id.btnColgar);
        btnColgar.setOnClickListener( view -> {
            view.startAnimation(btnAnimation);
            myConnectionService.cancelCall();
        } );

        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException ex){

        }


    }

    private void Colgar(){

    }

    public void finishActivity(){
        this.finish();
    }

    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }
}