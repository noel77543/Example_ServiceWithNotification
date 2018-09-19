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
import tw.noel.sung.com.example_servicewithnotification.R;
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
        return new Notification.Builder(this)
                .setShowWhen(false)
                .setSmallIcon(R.drawable.ic_notification)
                .setChannelId(MyService.BACKGROUND_CHANNEL_ID)
                .build();
    }


    //-------

    /***
     *  在service啟動後 每次 call startService都會執行一次
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int action = intent.getIntExtra(MyBroadcast.BUNDLE_KEY, MyBroadcast.ERROR);
        if (action == MyBroadcast.ACTION_STATUS_CHANGE || action == MyBroadcast.ACTION_CLOSE) {
            switch (action) {
                //播放狀態改變
                case MyBroadcast.ACTION_STATUS_CHANGE:
                    if (mediaPlayer.isPlaying()) {
                        pause();
                    } else {
                        play();
                    }
                    break;
                //結束
                case MyBroadcast.ACTION_CLOSE:
                    Log.e("onStartCommand", "ACTION_CLOSE");

                    stop();
                    stopSelf();
                    break;
            }

        } else {
            switch (action) {
                case MyBroadcast.ACTION_SHOW:
                    openAssetMusics();
                    break;
                //下一首
                case MyBroadcast.ACTION_NEXT:
                    break;
                //前一首
                case MyBroadcast.ACTION_PREVIOUS:
                    break;

            }
            Intent broadcastIntent = new Intent(MyBroadcast.PLATFORM);
            broadcastIntent.putExtra(MyBroadcast.BUNDLE_KEY_PLAYER_STATUS, mediaPlayer.isPlaying());
            broadcastIntent.putExtra(MyBroadcast.BUNDLE_KEY, action);

            sendBroadcast(broadcastIntent);
        }

        return START_STICKY;
    }


    //-----------

    /**
     * 打開assets的MP3
     */
    private void openAssetMusics() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            try {
                AssetFileDescriptor assetFileDescriptor = getAssets().openFd("something_like_this.mp3");
                mediaPlayer.reset();
                //設置媒體撥放器的數據資源
                mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (mediaPlayer != null) {
            mediaPlayer.start();
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
                mediaPlayer.prepare();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
