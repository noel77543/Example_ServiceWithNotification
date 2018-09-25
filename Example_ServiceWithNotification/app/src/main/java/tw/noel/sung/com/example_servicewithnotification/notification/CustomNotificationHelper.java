package tw.noel.sung.com.example_servicewithnotification.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import tw.noel.sung.com.example_servicewithnotification.R;
import tw.noel.sung.com.example_servicewithnotification.broadcast.MyBroadcast;
import tw.noel.sung.com.example_servicewithnotification.service.MyService;

import static android.content.Context.NOTIFICATION_SERVICE;


/**
 * Created by noel on 2018/4/13.
 */

public class CustomNotificationHelper extends Notification {

    public final static int NOTIFICATION_ID = 9487;
    //一般通知  單行字串 點選後開啟App 之 LaunchActivity
    public final static int NOTIFICATION_TYPE_NORMAL = 77;
    //大字串風格  點選後開啟App 之 LaunchActivity
    public final static int NOTIFICATION_TYPE_BIG_TEXT = 78;
    //客製化view  點選後開啟指定Activity
    public final static int NOTIFICATION_TYPE_CUSTOM = 79;


    @IntDef({NOTIFICATION_TYPE_NORMAL, NOTIFICATION_TYPE_BIG_TEXT, NOTIFICATION_TYPE_CUSTOM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface NotificationType {
    }


    private Class targetClass;
    private Context context;
    // 開啟另一個Activity的Intent
    private Intent intentNotification;
    private PendingIntent pendingIntent;
    private Bundle bundle;
    private int flags;

    private Uri defaultSoundUri;


    private PendingIntent pendingIntentClose;
    private PendingIntent pendingIntentPrevious;
    private PendingIntent pendingIntentNext;
    private PendingIntent pendingIntentPlay;


    //8.0以下適用
    private NotificationCompat.Builder notificationCompatBuilder;
    //8.0以上適用
    private Notification.Builder notificationBuilder;
    private Notification notification;


    private NotificationManager notificationManager;
    private NotificationChannel notificationChannel;


    /***
     * @param targetClass 打算開啟的 activity
     * @param bundle  是否攜帶資訊 可null
     */
    public CustomNotificationHelper(Context context, Class targetClass, @Nullable Bundle bundle) {
        this.targetClass = targetClass;
        this.context = context;
        this.bundle = bundle;
        init();
    }


    //--------

    /***
     * init..
     */
    private void init() {
        intentNotification = new Intent(context.getApplicationContext(), targetClass);

        if (bundle != null) {
            intentNotification.putExtras(bundle);
        }
        intentNotification.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intentNotification.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        flags = PendingIntent.FLAG_CANCEL_CURRENT;


        notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        pendingIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, intentNotification, flags);
        defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

    }


    //-----------

    /***
     * 建立8.0 以上推播
     */
    public void displayNotification(int smallIconRes, String name) {
        RemoteViews remoteViews = getRemoteViews(name);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(MyService.CHANNEL_ID, MyService.CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription(MyService.CHANNEL_DESCRIPTION);
            notificationManager.createNotificationChannel(notificationChannel);

            notificationBuilder = new Notification.Builder(context, MyService.CHANNEL_ID)
                    //狀態欄的icon
                    .setSmallIcon(smallIconRes)
                    //使可以向下彈出
                    .setPriority(Notification.PRIORITY_HIGH)
                    //通知聲音
                    .setSound(defaultSoundUri)
                    //設置的intent
                    .setContentIntent(pendingIntent)
                    //點了之後自動消失
                    .setAutoCancel(true)
                    //指定客製化view
                    .setCustomContentView(remoteViews);

            notification = notificationBuilder.build();
        }
        //建立8.0 以下推播
        else {
            notificationCompatBuilder = new NotificationCompat.Builder(context)
                    //狀態欄的icon
                    .setSmallIcon(smallIconRes)
                    //使可以向下彈出
                    .setPriority(Notification.PRIORITY_HIGH)
                    //通知聲音
                    .setSound(defaultSoundUri)
                    //設置的intent
                    .setContentIntent(pendingIntent)
                    //點了之後自動消失
                    .setAutoCancel(true)
                    //指定客製化view
                    .setCustomContentView(remoteViews);

            notification = notificationCompatBuilder.build();
        }

        //使無法被滑除
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        // 發送通知
        notificationManager.notify(NOTIFICATION_ID, notification);
    }


    //-------------

    /***
     * 移除通知
     */
    public void removeNotification() {
        notificationManager.cancel(NOTIFICATION_ID);
    }


    //-------------

    /***
     *  定義通知中的按鈕行為
     * @param name
     * @return
     */
    private RemoteViews getRemoteViews(String name) {

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.view_notification_controller);
        remoteViews.setTextViewText(R.id.tv_name, name);
        remoteViews.setImageViewResource(R.id.iv_close, R.drawable.ic_close);
        remoteViews.setImageViewResource(R.id.iv_previous, R.drawable.ic_previous);
        remoteViews.setImageViewResource(R.id.iv_next, R.drawable.ic_next);
        remoteViews.setImageViewResource(R.id.iv_play, R.drawable.ic_pause);


        Intent intentClose = new Intent(context, MyService.class);
        intentClose.putExtra(MyService.BUNDLE_KEY, MyService.ACTION_CLOSE);
        pendingIntentClose = PendingIntent.getService(context, 0, intentClose, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.iv_close, pendingIntentClose);

        Intent intentPrevious = new Intent(context, MyService.class);
        intentPrevious.putExtra(MyService.BUNDLE_KEY, MyService.ACTION_PREVIOUS);
        pendingIntentPrevious = PendingIntent.getService(context, 1, intentPrevious, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.iv_previous, pendingIntentPrevious);

        Intent intentNext = new Intent(context, MyService.class);
        intentNext.putExtra(MyService.BUNDLE_KEY, MyService.ACTION_NEXT);
        pendingIntentNext = PendingIntent.getService(context, 2, intentNext, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.iv_next, pendingIntentNext);

        Intent intentPlay = new Intent(context, MyService.class);
        intentPlay.putExtra(MyService.BUNDLE_KEY, MyService.ACTION_STATUS_CHANGE);
        pendingIntentPlay = PendingIntent.getService(context, 3, intentPlay, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.iv_play, pendingIntentPlay);

        return remoteViews;
    }
}
