package tw.noel.sung.com.example_servicewithnotification.broadcast;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.os.Build;
import android.support.annotation.IntDef;
import android.util.Log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import tw.noel.sung.com.example_servicewithnotification.MainActivity;
import tw.noel.sung.com.example_servicewithnotification.R;
import tw.noel.sung.com.example_servicewithnotification.notification.CustomNotificationHelper;
import tw.noel.sung.com.example_servicewithnotification.service.MyService;


/**
 * Created by noel on 2018/6/23.
 */

public class MyBroadcast extends BroadcastReceiver {


    public static String PLATFORM = "TestPlatform";


    private OnActionCommandListener onActionCommandListener;


    @Override
    public void onReceive(Context context, Intent intent) {


    }
    //----------

    public interface OnActionCommandListener {
        void onActionShowed();

        void onActionNext();

        void onActionPrevious();

        void onActionClosed();

        void onPlayerStatusChanged(boolean isPlaying);
    }

    //-------
    public void setOnActionCommandListener(OnActionCommandListener onActionCommandListener) {
        this.onActionCommandListener = onActionCommandListener;
    }


}
