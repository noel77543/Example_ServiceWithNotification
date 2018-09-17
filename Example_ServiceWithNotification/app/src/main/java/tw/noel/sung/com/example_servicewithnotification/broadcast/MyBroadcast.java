package tw.noel.sung.com.example_servicewithnotification.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

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
//    public static final int ACTION_PLAY = 66;
//////    public static final int ACTION_PAUSE = 67;

    public static final int ACTION_STATUS_CHANGE = 66;
    public static final int ACTION_NEXT = 68;
    public static final int ACTION_PREVIOUS = 69;
    public static final int ACTION_CLOSE = 70;

    public static final int ERROR = -1;

    public static String BUNDLE_KEY = "BundleKey";
    public static String BUNDLE_KEY_PLAYER_STATUS = "BundleKeyStatus";
    public static String PLATFORM = "TestPlatform";

    private CustomNotification customNotification;
    private OnActionCommandListener onActionCommandListener;

    @Override
    public void onReceive(Context context, Intent intent) {

            //接收傳值
            int action = intent.getIntExtra(BUNDLE_KEY, ERROR);
            Log.e("action", "" + action);

            if(action != ACTION_STATUS_CHANGE ){
                switch (action) {
                    //發送notification 控制台 並且開始撥放音樂
                    case ACTION_SHOW:
                        customNotification = new CustomNotification(context, MainActivity.class, null);
                        customNotification.displayNotificationToLaunchActivity(R.mipmap.ic_launcher, R.mipmap.ic_launcher_round, "Something Just Like This.");
                        onActionCommandListener.onActionShowed();
                        break;
                    //下一首
                    case ACTION_NEXT:
                        onActionCommandListener.onActionNext();
                        break;
                    //前一首
                    case ACTION_PREVIOUS:
                        onActionCommandListener.onActionPrevious();
                        break;
                    //結束
                    case ACTION_CLOSE:
                        Intent intentPlayerStatus = new Intent(context, MyService.class);
                        intentPlayerStatus.putExtra(BUNDLE_KEY,ACTION_CLOSE);
                        context.startService(intentPlayerStatus);

                        onActionCommandListener.onActionClosed();
                        break;
                }
            }else {
                Intent intentPlayerStatus = new Intent(context, MyService.class);
                boolean isPlaying = intent.getBooleanExtra(BUNDLE_KEY_PLAYER_STATUS, false);
                intentPlayerStatus.putExtra(BUNDLE_KEY,ACTION_STATUS_CHANGE);
                context.startService(intentPlayerStatus);
                onActionCommandListener.onPlayerStatusChanged(isPlaying);
            }


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
