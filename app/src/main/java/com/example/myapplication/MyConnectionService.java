package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.telecom.Connection;
import android.telecom.ConnectionRequest;
import android.telecom.ConnectionService;
import android.telecom.DisconnectCause;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;


public class MyConnectionService extends ConnectionService {
    public static final String TAG = MyConnectionService.class.getName();
    private MyConnection connection;
    private TelecomManager telecomManager;
    private Context context;
    private Bundle bundle;

    public MyConnectionService(Context context) {
        this.connection = new MyConnection();
        this.telecomManager = (TelecomManager) context.getSystemService(context.TELECOM_SERVICE);
        this.context = context;

        PhoneAccountHandle phoneAccountHandle = new PhoneAccountHandle(
                new ComponentName(context,
                        MyConnectionService.class.getName()), "myConnectionServiceId");
        this.bundle = new Bundle();
        this.bundle.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, phoneAccountHandle);
//        this.telecomManager.addNewIncomingCall(phoneAccountHandle,this.bundle);
        this.connection.setConnectionProperties(Connection.PROPERTY_SELF_MANAGED);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "On Start");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public Connection onCreateOutgoingConnection(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        Connection connection = super.onCreateOutgoingConnection(connectionManagerPhoneAccount, request);
        Log.d(TAG, connection.getDisconnectCause().getReason());
        return connection;
    }

    @Override
    public void onCreateOutgoingConnectionFailed(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        super.onCreateOutgoingConnectionFailed(connectionManagerPhoneAccount, request);
        Log.d(TAG, request.toString());
        if (request != null) {
        }
    }

    @Override
    public Connection onCreateIncomingConnection (PhoneAccountHandle connectionManagerPhoneAccount,
                                                  ConnectionRequest request){
        Connection connection = super.onCreateIncomingConnection(connectionManagerPhoneAccount, request);
        Log.d(TAG, "onCreateIncomingConnection");
        return  connection;
    }

    @Override
    public void onCreateIncomingConnectionFailed (PhoneAccountHandle connectionManagerPhoneAccount,
                                                  ConnectionRequest request){
        super.onCreateIncomingConnectionFailed(connectionManagerPhoneAccount, request);
        if(request != null){
            Toast.makeText(this.context, "Ocurrion un problema al recibir la llamada", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionServiceFocusLost(){
        super.onConnectionServiceFocusLost();
        Log.d(TAG,"onConnectionServiceFocusLost");
    }

    public boolean call(String phoneNumber) {
        if (ActivityCompat.checkSelfPermission(this.context, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return false;
        }
        this.telecomManager.placeCall(Uri.parse("tel:" + phoneNumber), this.bundle);
        return true;
    }

    public void cancelCall() {
        try {
            Log.d(TAG, "Cancelling...");
            if (ActivityCompat.checkSelfPermission(this.context, android.Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            boolean status = this.telecomManager.endCall();
            if (status) {
                DisconnectCause disconnectCause = new DisconnectCause(DisconnectCause.CANCELED);
                this.connection.setDisconnected(disconnectCause);
                this.connection.destroy();
                Log.d(TAG, "Cancelado.");
            } else
                Log.d(TAG, "No Cancelado.");

        } catch (Exception ex) {
            throw ex;
        }

    }

    public int removeHistoryByNumber(String phoneNumber) {
        String query = "NUMBER=" + phoneNumber;

        return context.getContentResolver().delete(CallLog.Calls.CONTENT_URI, query, null);
    }

    public boolean isCalling() {
        if (ActivityCompat.checkSelfPermission(this.context, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return false;
        }
        return this.telecomManager.isInCall();
    }




    public class MyConnection extends Connection{
        @Override
        public void onShowIncomingCallUi(){
            Log.d(TAG,"onShowIncomingCallUi");
        }


    }
}