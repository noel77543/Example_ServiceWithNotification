package tw.noel.sung.com.example_servicewithnotification.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.util.Log;

import tw.noel.sung.com.example_servicewithnotification.MainActivity;
import tw.noel.sung.com.example_servicewithnotification.R;
import tw.noel.sung.com.example_servicewithnotification.notification.CustomNotification;

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
    public static String PLATFORM = "TestPlatform";

    private CustomNotification customNotification;
    private OnActionCommandListener onActionCommandListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            //接收傳值
            int action = intent.getIntExtra(BUNDLE_KEY, ERROR);
            if (action == ERROR) {
                Log.e("ERROR", "ERROR");
            } else {
                Log.e("AAA", "AAA");
                switch (action) {
                    //發送notification 控制台 並且開始撥放音樂
                    case ACTION_SHOW:
                        customNotification = new CustomNotification(context, MainActivity.class, null);
                        customNotification.displayNotificationToLaunchActivity(R.mipmap.ic_launcher, R.mipmap.ic_launcher_round, "Something Just Like This.");
                        onActionCommandListener.onActionShowed();
                        break;
                    //繼續撥放音樂
                    case ACTION_PLAY:
                        onActionCommandListener.onActionPlayed();
                        break;
                    //暫停
                    case ACTION_PAUSE:
                        onActionCommandListener.onActionPaused();
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
                        onActionCommandListener.onActionClose();
                        break;
                }
            }
        }
    }
    //----------

    public interface OnActionCommandListener {
        void onActionShowed();

        void onActionPlayed();

        void onActionPaused();

        void onActionNext();

        void onActionPrevious();

        void onActionClose();
    }

    //-------
    public void setOnActionCommandListener(OnActionCommandListener onActionCommandListener) {
        this.onActionCommandListener = onActionCommandListener;
    }


}
