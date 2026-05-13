package com.example.myapplication66;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

/**
 * 后台任务服务
 * 演示Service在后台执行定时提醒任务
 */
public class BackgroundService extends Service {

    private static final String TAG = "BackgroundService";
    private static final String CHANNEL_ID = "background_service_channel";
    private static final int NOTIFICATION_ID = 2001;
    private static final int REMINDER_NOTIFICATION_ID = 2002;

    private boolean isRunning = false;
    private int checkCount = 0;
    private int reminderCount = 0;
    private Thread workerThread;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service onCreate - 后台服务创建");
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service onStartCommand - 后台服务启动");

        if (!isRunning) {
            startForeground(NOTIFICATION_ID, createStatusNotification("后台任务已启动，正在运行..."));
            startBackgroundTask();
        }

        return START_STICKY;
    }

    private void startBackgroundTask() {
        isRunning = true;
        checkCount = 0;
        reminderCount = 0;

        workerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "后台任务线程开始执行");

                while (isRunning) {
                    try {
                        Thread.sleep(3000);
                        checkCount++;

                        Log.d(TAG, "========================================");
                        Log.d(TAG, "【后台定时检查】第 " + checkCount + " 次检查");
                        Log.d(TAG, "【运行时间】" + (checkCount * 3) + " 秒");
                        Log.d(TAG, "【发送提醒】" + reminderCount + " 次");
                        Log.d(TAG, "========================================");

                        updateStatusNotification("后台任务运行中... 检查次数: " + checkCount);

                        if (checkCount % 2 == 0) {
                            reminderCount++;
                            sendReminderNotification();
                            Log.d(TAG, ">>> 发送定时提醒通知 <<<");
                        }

                    } catch (InterruptedException e) {
                        Log.d(TAG, "后台任务线程被中断");
                        break;
                    }
                }

                Log.d(TAG, "后台任务线程结束");
            }
        });
        workerThread.start();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel statusChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "后台任务状态",
                    NotificationManager.IMPORTANCE_LOW
            );
            statusChannel.setDescription("显示后台任务运行状态");

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(statusChannel);
            }
        }
    }

    private Notification createStatusNotification(String content) {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("⚙️ 后台服务")
                .setContentText(content)
                .setSmallIcon(android.R.drawable.ic_menu_manage)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    private void updateStatusNotification(String content) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(NOTIFICATION_ID, createStatusNotification(content));
        }
    }

    private void sendReminderNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("⏰ 定时提醒")
                .setContentText("后台任务已运行 " + (checkCount * 3) + " 秒，这是第 " + reminderCount + " 次提醒")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(false)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("📊 后台统计报告\n\n" +
                                "✅ 检查次数: " + checkCount + " 次\n" +
                                "⏱️ 运行时间: " + (checkCount * 3) + " 秒\n" +
                                "🔔 提醒次数: " + reminderCount + " 次\n" +
                                "📈 状态: 正常运行中"));

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(REMINDER_NOTIFICATION_ID, builder.build());
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Service onDestroy - 后台服务销毁");
        isRunning = false;

        if (workerThread != null) {
            workerThread.interrupt();
            workerThread = null;
        }

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.cancel(NOTIFICATION_ID);
            manager.cancel(REMINDER_NOTIFICATION_ID);
        }

        stopForeground(true);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
