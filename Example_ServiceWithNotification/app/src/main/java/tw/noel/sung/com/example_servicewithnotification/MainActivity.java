package tw.noel.sung.com.example_servicewithnotification;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import tw.noel.sung.com.example_servicewithnotification.broadcast.MyBroadcast;
import tw.noel.sung.com.example_servicewithnotification.service.MyService;

public class MainActivity extends AppCompatActivity implements MyBroadcast.OnActionCommandListener {

    private MyBroadcast broadcast;
    private Button btnStart;
    private boolean isPlaying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStart = (Button) findViewById(R.id.btn_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyService.class);
                intent.putExtra(MyService.BUNDLE_KEY, isPlaying ? MyService.ACTION_CLOSE : MyService.ACTION_SHOW);
                startService(intent);
            }
        });
    }

    //--------

    @Override
    protected void onResume() {
        super.onResume();

//        registerBroadcastReceiver();
    }
    //--------

    @Override
    protected void onPause() {
        super.onPause();
//        unregisterReceiver(broadcast);
    }

//    //--------
//
//    /***
//     *  動態註冊
//     */
//    private void registerBroadcastReceiver() {
//        // 註冊接收器
//        broadcast = new MyBroadcast();
//        broadcast.setOnActionCommandListener(this);
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(MyBroadcast.PLATFORM);
//        registerReceiver(broadcast, filter);
//    }

    @Override
    public void onActionShowed() {

    }

    @Override
    public void onActionNext() {

    }

    @Override
    public void onActionPrevious() {

    }

    @Override
    public void onActionClosed() {

    }

    @Override
    public void onPlayerStatusChanged(boolean isPlaying) {
        this.isPlaying = isPlaying;
        Log.e("isPlaying", isPlaying + "");
        btnStart.setText(isPlaying ? "pause" : "start");
    }
}
