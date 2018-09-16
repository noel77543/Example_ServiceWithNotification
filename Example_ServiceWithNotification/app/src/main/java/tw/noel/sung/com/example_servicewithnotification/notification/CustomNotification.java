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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import tw.noel.sung.com.example_servicewithnotification.R;
import tw.noel.sung.com.example_servicewithnotification.service.MyService;

import static android.content.Context.NOTIFICATION_SERVICE;


/**
 * Created by noel on 2018/4/13.
 */

public class CustomNotification extends Notification {

    public final static  int NOTIFICATION_ID = 9487;
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

    private NotificationChannel notificationChannel;
    private Notification notification;
    private NotificationManager notificationManager;
    private Uri defaultSoundUri;
    private Bitmap bigIcon;


    /***
     * @param targetClass 打算開啟的 activity
     * @param bundle  是否攜帶資訊 可null
     */
    public CustomNotification(Context context, Class targetClass, @Nullable Bundle bundle) {
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
    }
    //-----------

    /***
     * 前往 主頁面
     */
    public void displayNotificationToLaunchActivity(int smallIconRes, int largeIconRes, String name) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.view_notification_controller);
        remoteViews.setTextViewText(R.id.tv_name, name);
        remoteViews.setImageViewResource(R.id.iv_close, R.drawable.ic_close);
        remoteViews.setImageViewResource(R.id.iv_previous, R.drawable.ic_previous);
        remoteViews.setImageViewResource(R.id.iv_next, R.drawable.ic_next);
        remoteViews.setImageViewResource(R.id.iv_platy, R.drawable.ic_pause);

        pendingIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, intentNotification, flags);
        defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        bigIcon = BitmapFactory.decodeResource(context.getResources(), largeIconRes);
        //獲取通知服務
        notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        //8.0 以上處理辦法
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(MyService.CHANNEL_ID, name,  NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
            // 建立通知
            notification = new Notification.Builder(context)
                    //狀態欄的icon
                    .setSmallIcon(smallIconRes)
                    //通知欄的大icon
                    .setLargeIcon(bigIcon)
                    //使可以向下彈出
                    .setPriority(Notification.PRIORITY_HIGH)
                    //通知聲音
                    .setSound(defaultSoundUri)
                    //設置的intent
                    .setContentIntent(pendingIntent)
                    //點了之後自動消失
                    .setAutoCancel(true)
                    //指定客製化view
                    .setCustomContentView(remoteViews)
                    //頻道ID
                    .setChannelId(MyService.CHANNEL_ID)
                    .build();
        }else {
            // 建立通知
            notification = new NotificationCompat.Builder(context)
                    //狀態欄的icon
                    .setSmallIcon(smallIconRes)
                    //通知欄的大icon
                    .setLargeIcon(bigIcon)
                    //使可以向下彈出
                    .setPriority(Notification.PRIORITY_HIGH)
                    //通知聲音
                    .setSound(defaultSoundUri)
                    //設置的intent
                    .setContentIntent(pendingIntent)
                    //點了之後自動消失
                    .setAutoCancel(true)
                    //指定客製化view
                    .setCustomBigContentView(remoteViews)
                    .build();
        }



        //使無法被滑除
        notification.flags = Notification.FLAG_ONGOING_EVENT;
//        notificationManager.cancel(NOTIFICATION_ID);
        // 發送通知
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

}
