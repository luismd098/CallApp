package com.example.myapplication;

import android.app.NotificationManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.telecom.Call;
import android.telecom.InCallService;
import android.telecom.TelecomManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;


public class MyInCallService extends InCallService {
    public static final String TAG = MyInCallService.class.getName();
    public static final int NOTIFICATION_ID = 1;

    public NotificationManager notificationManager = null;
    @Override
     public void onCallAdded(Call call){

        String phoneNumber = "";
        try{
            Call.Details details = call.getDetails();
            Bundle bundle = details.getIntentExtras();
            Uri uri = (Uri) bundle.get((TelecomManager.EXTRA_INCOMING_CALL_ADDRESS));
            if(uri != null && uri.toString().length() > 0){
                String auxUri = uri.toString();
                String end = auxUri.substring(auxUri.length()-4);
                phoneNumber = "******" + end;
            }
        } catch (Exception ex){
            phoneNumber = "DESCONOCIDO";
        }

        try{
            if(call.getState() == Call.STATE_CONNECTING){
                // Conectando llamada
            }else if(call.getState() == Call.STATE_NEW || call.getState() == Call.STATE_RINGING){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    createIntent(phoneNumber);
                }
            }
        } catch (Exception ex){}
        finally {
            openScreen(phoneNumber);
        }
    }

    @Override
    public void onCallRemoved(Call call) {
        super.onCallRemoved(call);
        Log.d("onCallRemoved","onCallRemoved");
        try{
            if(notificationManager == null){
                notificationManager = this.getSystemService(
                        NotificationManager.class);
            }
            notificationManager.cancelAll();

            sendBroadcast(new Intent("com.close.activity"));
        } catch (Exception ex){

        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void createIntent(String phoneNumber){
        Intent cancelAction = new Intent(this,MyReceiver.class);
        cancelAction.putExtra("COLGAR_ACTION","Llamada cancelada.");



        // Build the notification as an ongoing high priority item; this ensures it will show as
        // a heads up notification which slides down over top of the current content.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,MainActivity.CHANNEL_ID);
        builder.setOngoing(true);
        builder.setSmallIcon(R.drawable.call);
        builder.setContentTitle("Llamada entrante");
        builder.setContentText("Llamada entrante del número " + phoneNumber);
        // Use builder.addAction(..) to add buttons to answer or reject the call.
        if(notificationManager == null){
            notificationManager = this.getSystemService(
                    NotificationManager.class);
        }
        notificationManager.notify(MainActivity.CHANNEL_ID, NOTIFICATION_ID, builder.build());

    }

    private void openScreen(String phoneNumber){
        Intent intent = new Intent(this,DialerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("PHONE_NUMBER",phoneNumber);
        startActivity(intent);
    }
}