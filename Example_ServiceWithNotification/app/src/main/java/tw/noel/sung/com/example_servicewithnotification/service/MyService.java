package tw.noel.sung.com.example_servicewithnotification.service;

import android.app.Notification;

import android.app.PendingIntent;
import android.app.Service;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

import tw.noel.sung.com.example_servicewithnotification.MainActivity;
import tw.noel.sung.com.example_servicewithnotification.broadcast.MyBroadcast;

import static tw.noel.sung.com.example_servicewithnotification.notification.CustomNotification.NOTIFICATION_ID;

/**
 * Created by noel on 2018/6/23.
 */

public class MyService extends Service {

    private MediaPlayer mediaPlayer;


    public static final String CHANNEL_ID = "TEST_ID";
    public static final String BACKGROUND_CHANNEL_ID = "BACKGROUND_TEST_ID";
    private final String CHANNEL_NAME = "TEST_NAME";
    private final String CHANNEL_DESCRIPTION = "TEST_DESCRIPTION";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //-------

    /***
     *  倘若service未啟動 call startService將會執行一次
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("onCreate", "onCreate");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            startForeground(NOTIFICATION_ID, getNotification());
        }
    }

    //-------

    /***
     * 建立背景服務
     * @return
     */
    private Notification getNotification() {
        Intent intentNotification = new Intent(getApplicationContext(), MainActivity.class);

        intentNotification.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intentNotification.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID, intentNotification, PendingIntent.FLAG_CANCEL_CURRENT);


        Notification.Builder builder = new Notification.Builder(this)
                //通知聲音
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                //設置的intent
                .setContentIntent(pendingIntent)
                //點了之後自動消失
                .setAutoCancel(true)
                .setChannelId(MyService.BACKGROUND_CHANNEL_ID);

        return builder.getNotification();
    }


    //-------

    /***
     *  在service啟動後 每次 call startService都會執行一次
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("onStartCommand", "onStartCommand");
        if (intent != null) {
            int action = intent.getIntExtra(MyBroadcast.BUNDLE_KEY, MyBroadcast.ERROR);
            if (action != MyBroadcast.ERROR) {
                switch (action) {
                    case MyBroadcast.ACTION_SHOW:
                        if (mediaPlayer == null) {
                            openAssetMusics();
                        }
                        break;
                    //繼續撥放音樂
                    case MyBroadcast.ACTION_PLAY:
                        break;
                    //暫停
                    case MyBroadcast.ACTION_PAUSE:
                        break;
                    //下一首
                    case MyBroadcast.ACTION_NEXT:
                        break;
                    //前一首
                    case MyBroadcast.ACTION_PREVIOUS:
                        break;
                    //結束
                    case MyBroadcast.ACTION_CLOSE:
                        stop();
                        stopSelf();
                        break;
                }
                Log.e("onStartCommand", "AAAAAAA");
                Intent broadcastIntent = new Intent(MyBroadcast.PLATFORM);
                broadcastIntent.putExtra(MyBroadcast.BUNDLE_KEY, action);
                sendBroadcast(broadcastIntent);
            }
        }

        return START_STICKY;
    }


    //-----------

    /**
     * 打開assets的MP3
     */
    private void openAssetMusics() {
        Log.e("openAssetMusics", "openAssetMusics");
        mediaPlayer = new MediaPlayer();
        try {
            AssetFileDescriptor assetFileDescriptor = getAssets().openFd("something_like_this.mp3");
            mediaPlayer.reset();
            //設置媒體撥放器的數據資源
            mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            Log.e("openAssetMusics", "IOException");

            e.printStackTrace();
        }
    }

    //------------
    public void play() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }
    //------------

    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }
    //------------

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            try {
                mediaPlayer.prepare();  // 在调用stop后如果需要再次通过start进行播放,需要之前调用prepare函数
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
