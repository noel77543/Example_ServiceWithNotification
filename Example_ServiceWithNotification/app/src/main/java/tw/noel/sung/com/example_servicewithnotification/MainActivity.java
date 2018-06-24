package tw.noel.sung.com.example_servicewithnotification;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import tw.noel.sung.com.example_servicewithnotification.broadcast.MyBroadcast;
import tw.noel.sung.com.example_servicewithnotification.notification.CustomNotification;

public class MainActivity extends AppCompatActivity {

    private MyBroadcast broadcast;

    private boolean isTest = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnStart = (Button) findViewById(R.id.btn_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMusic();
            }
        });
    }
    //---------

    /***
     *  發送 notification 並且 告知service 撥放音樂
     */
    private void startMusic() {
        Intent intent = new Intent(MyBroadcast.PLATFORM);
        intent.putExtra(MyBroadcast.BUNDLE_KEY, isTest ? MyBroadcast.ACTION_SHOW : MyBroadcast.ACTION_CLOSE);
        sendBroadcast(intent);

        isTest = !isTest;
    }


    //--------

    @Override
    protected void onResume() {
        super.onResume();
        registerBroadcastReceiver();
    }
    //--------

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcast);
    }

    //--------

    /***
     *  動態註冊
     */
    private void registerBroadcastReceiver() {
        // 註冊接收器
        broadcast = new MyBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MyBroadcast.PLATFORM);
        registerReceiver(broadcast, filter);
    }
}
