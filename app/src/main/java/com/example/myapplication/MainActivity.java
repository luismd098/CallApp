package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Person;
import android.app.role.RoleManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.graphics.drawable.IconCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private ArrayList<String> codes;
    private String result;

    private Button btn;
    private Button btnStatus;
    private MyConnectionService myConnectionService;
    public static final String CHANNEL_ID = "MY_CHHANEL_ID";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        myConnectionService = new MyConnectionService(this);

        btn = findViewById(R.id.button_first);
        btnStatus = findViewById(R.id.button_status);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        codes = new ArrayList<>();
        binding.fab.setOnClickListener(view -> startScanner());
        btn.setOnClickListener(view -> {
            int result = myConnectionService.removeHistoryByNumber("4521902181");
            Toast.makeText(this,"Prueba: " + result,Toast.LENGTH_SHORT).show();
        });
        requestRole();
        createChannel();
        btnStatus.setOnClickListener( view -> {
            Intent intent = new Intent(this,DialerActivity.class);
            startActivity(intent);
//            createIntent();
//            requestRole();
//            if(myConnectionService.isCalling()){
//                Toast.makeText(this,"Llamando... :}",Toast.LENGTH_SHORT).show();
//            }else
//                Toast.makeText(this,"No llamando :{",Toast.LENGTH_SHORT).show();


        });
    }

    private void createChannel(){
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Incoming Calls",
                NotificationManager.IMPORTANCE_HIGH);
        // other channel setup stuff goes here.

        // We'll use the default system ringtone for our incoming call notification channel.  You can
        // use your own audio resource here.
        Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        channel.setSound(ringtoneUri, new AudioAttributes.Builder()
                // Setting the AudioAttributes is important as it identifies the purpose of your
                // notification sound.
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build());

        NotificationManager mgr = getSystemService(NotificationManager.class);
        mgr.createNotificationChannel(channel);

    }

    private void createIntent(){
        Context context = this.getApplicationContext();
        //ACTIONS
        Intent cancelAction = new Intent(context,DialerActivity.class);
//        cancelAction.putExtra("COLGAR_ACTION","Llamada cancelada.");
//        Intent acceptAction = new Intent(context,MyInCallService.class);
//        acceptAction.putExtra("CONTESTAR_ACTION","Llamada cancelada.");

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, cancelAction, PendingIntent.FLAG_MUTABLE);
//        PendingIntent aceptarIntent = PendingIntent.getBroadcast(context, 0, acceptAction, PendingIntent.FLAG_IMMUTABLE);

        Person person = new Person.Builder()
                .setImportant(true)
                .setName("Prueba")
                .build();

//        NotificationCompat.Style cs = null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
//            cs = Notification.CallStyle.forIncomingCall(
//                    person,
//                    pendingIntent,
//                    aceptarIntent
//            );
//        }

        // Build the notification as an ongoing high priority item; this ensures it will show as
        // a heads up notification which slides down over top of the current content.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,MainActivity.CHANNEL_ID);
//        builder.setOngoing(true);
        builder.setSmallIcon( R.drawable.ic_launcher_foreground);
//        builder.setContentTitle("Llamada");
//        builder.setContentText("Your notification content.");
        builder.setFullScreenIntent(pendingIntent,true);

        // Use builder.addAction(..) to add buttons to answer or reject the call.
//        builder.addAction(0,"COLGAR",pendingIntent);
        NotificationManager notificationManager = context.getSystemService(
                NotificationManager.class);
        notificationManager.notify(MainActivity.CHANNEL_ID, 1, builder.build());

    }

    public void requestRole() {

        Log.d("requestRole","Launching...");
        try {
//            Intent intent = new Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER);
//            intent.putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, getPackageName());
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            RoleManager roleManager = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                roleManager = (RoleManager) getSystemService(ROLE_SERVICE);
            }
            Intent intent = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER);
            }

            someActivityResultLauncher.launch(intent);
        }
        catch (Exception ex){
            Log.e("EX",ex.getMessage());
        }

    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Here, no request code

                    }
                }
            });

    public void startScanner(){
        myConnectionService.call("4521902181");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public interface Callback {
        void setResult(String result);

    }

    public class MyCallBack implements Callback {
        @Override
        public void setResult(String res) {
            result = res;
        }
    }

    public static abstract class MyActionListener implements WifiP2pManager.ActionListener {
        private Callback callback;

        public MyActionListener(Callback callback) {
            this.callback = callback;
        }

    }

}