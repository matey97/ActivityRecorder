package es.uji.geotec.activityrecorder.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import es.uji.geotec.activityrecorder.R;
import es.uji.geotec.activityrecorder.model.ActivityEnum;
import es.uji.geotec.activityrecorder.persistence.LocalPersister;
import es.uji.geotec.activityrecorder.persistence.SensorRecordPersister;

public class SensorRecordingService extends Service {

    public static final String ACTIVITY = "Activity";

    private static final String TAG = "SensorRecordingService";

    private static final String CHANNEL_ID = "ActivityRecorder";
    private static final int NOTIFICATION_ID = 53;

    private SensorManager sensorManager;
    private SensorRecordingReceiver sensorReceiver;

    private NotificationManager notificationManager;

    private ActivityEnum activity;

    private PowerManager.WakeLock wakeLock;

    public SensorRecordingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: service created");
        
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorReceiver = new SensorRecordingReceiver();

        notificationManager = getSystemService(NotificationManager.class);

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "activityrecorder:mywakelock");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int start_flag = super.onStartCommand(intent, flags, startId);

        activity = (ActivityEnum) intent.getExtras().getSerializable(ACTIVITY);

        Log.d(TAG, "onStartCommand: going to start gathering data for " + activity);

        wakeLock.acquire();

        registerListener();
        runInForegroundWithNotification();

        return start_flag;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterListener();
        saveRecords();
        stopForeground(true);
        Log.d(TAG, "onDestroy: service destroyed");
    }

    private void registerListener() {
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(sensorReceiver, sensor, SensorManager.SENSOR_DELAY_GAME);

        Log.d(TAG, "registerListener: gathering data for " + activity);
    }

    private void unregisterListener() {
        sensorManager.unregisterListener(sensorReceiver);

        Log.d(TAG, "unregisterListener: stop gathering data for " + activity);

        wakeLock.release();
    }

    private void saveRecords() {
        SensorRecordPersister localPersister = new LocalPersister(activity);

        localPersister.saveSensorRecords(sensorReceiver.getSensorRecords());

        Log.d(TAG, "saveRecords: gathered records for " + activity + " saved");
    }

    private void runInForegroundWithNotification() {
        createNotificationChannel();
        startForeground(NOTIFICATION_ID, createNotification());
    }

    private Notification createNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("ActivityRecorder is working")
                .setContentText("Accelerometer data is being gathered")
                .build();
    }

    private void createNotificationChannel() {
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                "ActivityRecorder Foreground Notification",
                NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.setDescription("Notification channel for foreground notification");

        notificationManager.createNotificationChannel(notificationChannel);
    }
}
