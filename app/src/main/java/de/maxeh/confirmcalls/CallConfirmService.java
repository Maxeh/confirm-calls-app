package de.maxeh.confirmcalls;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class CallConfirmService extends Service {
    private final static int NOTIFICATION_ID = 100;
    public BroadcastReceiver mOutgoingCallReceiver;
    public static boolean sAllowCall = false;

    /**
     * Service runs a BroadcastReceiver to receive outgoing calls.
     * If the outgoing call was not started from this app, it is canceled.
     */
    @Override
    public void onCreate() {
        mOutgoingCallReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)){
                    if(!sAllowCall) {
                        setResultData(null);

                        Intent confirmIntent = new Intent();
                        confirmIntent.setClassName("de.maxeh.confirmcalls", "de.maxeh.confirmcalls.ConfirmActivity");
                        confirmIntent.putExtra("phoneNumber", intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER));
                        confirmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(confirmIntent);
                    }
                    sAllowCall = false;
                }
            }
        };
        IntentFilter filter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
        registerReceiver(mOutgoingCallReceiver, filter);
    }

    /**
     * Unregister BroadcastReceiver when service is destroyed.
     */
    @Override
    public void onDestroy() {
        unregisterReceiver(mOutgoingCallReceiver);
        super.onDestroy();
    }

    /**
     * Create notification channel and notification when the service starts.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        createNotificationChannel();
        startServiceWithNotification();
        return START_STICKY;
    }

    /**
     * onBind() must be overwritten. Nothing is bound in this service.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Create a new notification channel. If there is already a notification channel, the existing
     * channel is not overwritten or recreated.
     */
    public void createNotificationChannel() {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "call_confirm_channel_id_2";
        CharSequence channelName = "Call confirmation";
        int importance = NotificationManager.IMPORTANCE_HIGH; // HIGH is necessary, otherwise the service sometimes does not intercept outgoing calls
        NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
        notificationChannel.setShowBadge(false);
        notificationManager.createNotificationChannel(notificationChannel);
    }

    /**
     * Start a notification and bring the service to the foreground with startForeground().
     */
    public void startServiceWithNotification() {
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        notificationIntent.setAction("start activity");  // A string containing the action name
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent contentPendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, "call_confirm_channel_id_2")
                .setContentText("Call confirmation service running")
                .setSmallIcon(R.drawable.statusbar_icon)
                .setContentIntent(contentPendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                .build();
        notification.flags = notification.flags | Notification.FLAG_NO_CLEAR; // NO_CLEAR makes the notification stay when the user performs a "delete all" command
        startForeground(NOTIFICATION_ID, notification);
    }
}