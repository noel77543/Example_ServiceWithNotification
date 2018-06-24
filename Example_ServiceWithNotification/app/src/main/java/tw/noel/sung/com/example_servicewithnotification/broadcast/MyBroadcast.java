package tw.noel.sung.com.example_servicewithnotification.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import tw.noel.sung.com.example_servicewithnotification.MainActivity;
import tw.noel.sung.com.example_servicewithnotification.R;
import tw.noel.sung.com.example_servicewithnotification.notification.CustomNotification;
import tw.noel.sung.com.example_servicewithnotification.service.MyService;

/**
 * Created by noel on 2018/6/23.
 */

public class MyBroadcast extends BroadcastReceiver {
    public static final int ACTION_SHOW = 65;
    public static final int ACTION_PLAY = 66;
    public static final int ACTION_PAUSE = 67;
    public static final int ACTION_NEXT = 68;
    public static final int ACTION_PREVIOUS = 69;
    public static final int ACTION_CLOSE = 70;

    public static final int ERROR = -1;

    public static String BUNDLE_KEY = "BundleKey";
    public static String PLATFORM = "tw.noel.sung.com.example_servicewithnotification";

    private CustomNotification customNotification;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            //接收傳值
            int action = intent.getIntExtra(BUNDLE_KEY, ERROR);

            if (action == ERROR) {
                Log.e("ERROR", "ERROR");
            } else {
                Intent serviceIntent = new Intent(context, MyService.class);
                serviceIntent.putExtra(BUNDLE_KEY, action);
                switch (action) {
                    //發送notification 控制台 並且開始撥放音樂
                    case ACTION_SHOW:
                        customNotification = new CustomNotification(context, MainActivity.class, null);
                        customNotification.displayNotificationToLaunchActivity(R.mipmap.ic_launcher, R.mipmap.ic_launcher_round, "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
                        //繼續撥放音樂
                    case ACTION_PLAY:
                        //暫停
                    case ACTION_PAUSE:
                        //下一首
                    case ACTION_NEXT:
                        //前一首
                    case ACTION_PREVIOUS:
                    //結束
                    case ACTION_CLOSE:
                        context.startService(serviceIntent);
                        break;
                }
            }
        }
    }
}
