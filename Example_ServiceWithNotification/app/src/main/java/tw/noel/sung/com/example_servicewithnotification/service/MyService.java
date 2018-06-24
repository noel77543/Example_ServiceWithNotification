package tw.noel.sung.com.example_servicewithnotification.service;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

import tw.noel.sung.com.example_servicewithnotification.R;
import tw.noel.sung.com.example_servicewithnotification.broadcast.MyBroadcast;

/**
 * Created by noel on 2018/6/23.
 */

public class MyService extends Service {

    private MediaPlayer mediaPlayer;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //-------
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("onCreate", "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("onStartCommand", "onStartCommand");
        if (intent != null) {
            int action = intent.getIntExtra(MyBroadcast.BUNDLE_KEY, MyBroadcast.ERROR);
            if (action != MyBroadcast.ERROR) {
                switch (action) {
                    //發送notification 控制台 並且開始撥放音樂
                    case MyBroadcast.ACTION_SHOW:
                        openAssetMusics();
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
