package tw.noel.sung.com.example_servicewithnotification.service;

import android.app.Notification;

import android.app.Service;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import tw.noel.sung.com.example_servicewithnotification.MainActivity;
import tw.noel.sung.com.example_servicewithnotification.R;
import tw.noel.sung.com.example_servicewithnotification.broadcast.MyBroadcast;
import tw.noel.sung.com.example_servicewithnotification.notification.CustomNotificationHelper;

import static tw.noel.sung.com.example_servicewithnotification.notification.CustomNotificationHelper.NOTIFICATION_ID;

/**
 * Created by noel on 2018/6/23.
 */

public class MyService extends Service {

    public static final int ACTION_SHOW = 65;
    public static final int ACTION_STATUS_CHANGE = 66;
    public static final int ACTION_NEXT = 68;
    public static final int ACTION_PREVIOUS = 69;
    public static final int ACTION_CLOSE = 70;

    @IntDef({ACTION_SHOW, ACTION_STATUS_CHANGE, ACTION_NEXT, ACTION_PREVIOUS, ACTION_CLOSE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface NotificationAction {

    }

    public static final String CHANNEL_ID = "TEST_ID";
    public static final String BACKGROUND_CHANNEL_ID = "BACKGROUND_TEST_ID";
    public static final String CHANNEL_NAME = "TEST_NAME";
    public static final String CHANNEL_DESCRIPTION = "TEST_DESCRIPTION";
    public static final int ERROR = -1;
    public static String BUNDLE_KEY = "BundleKey";
    public static String BUNDLE_KEY_PLAYER_STATUS = "BundleKeyStatus";
    private CustomNotificationHelper customNotificationHelper;
    private MediaPlayer mediaPlayer;

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
        //在前景建立背景服務
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(NOTIFICATION_ID, new Notification.Builder(this)
                    .setShowWhen(false)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setChannelId(MyService.BACKGROUND_CHANNEL_ID)
                    .build());
        }
    }


    //-------

    /***
     *  在service啟動後 每次 call startService都會執行一次
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {

            int action = intent.getIntExtra(BUNDLE_KEY, ERROR);

            if (action == ACTION_STATUS_CHANGE || action == ACTION_CLOSE) {
                switch (action) {
                    //播放狀態改變
                    case ACTION_STATUS_CHANGE:
                        if (mediaPlayer.isPlaying()) {
                            pause();
                        } else {
                            play();
                        }
                        break;
                    //結束
                    case ACTION_CLOSE:
                        Log.e("onStartCommand", "ACTION_CLOSE");
                        customNotificationHelper.removeNotification();
                        stop();
                        stopSelf();
                        break;
                }

            } else {
                //8.0 以上版本
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    switch (action) {
                        case ACTION_SHOW:
                            customNotificationHelper = new CustomNotificationHelper(this, MainActivity.class, null);
                            customNotificationHelper.displayNewNotification(R.mipmap.ic_launcher, "Something Just Like This.");
                            openAssetMusics();
                            break;
                        //下一首
                        case ACTION_NEXT:
                            Log.e("onStartCommand", "ACTION_NEXT");
                            break;
                        //前一首
                        case ACTION_PREVIOUS:
                            Log.e("onStartCommand", "ACTION_PREVIOUS");
                            break;
                    }
                }
                //8.0 以下版本
                else {
                    switch (action) {
                        //發送notification 控制台 並且開始撥放音樂
                        case ACTION_SHOW:
                            customNotificationHelper = new CustomNotificationHelper(this, MainActivity.class, null);
                            customNotificationHelper.displayOldNotification(R.mipmap.ic_launcher, "Something Just Like This.");
                            openAssetMusics();
                            break;
                        //下一首
                        case ACTION_NEXT:
                            Log.e("onStartCommand", "ACTION_NEXT");
                            break;
                        //前一首
                        case ACTION_PREVIOUS:
                            Log.e("onStartCommand", "ACTION_PREVIOUS");
                            break;
                    }
                }
            }
        }
        else {
            stopSelf();
        }
//        return START_STICKY;

        return super.onStartCommand(intent, flags, startId);
    }


    //-----------


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

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
