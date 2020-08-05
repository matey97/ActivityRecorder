package es.uji.geotec.activityrecorder.service;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import es.uji.geotec.activityrecorder.model.AccelerometerSensorRecord;

public class SensorRecordingReceiver implements SensorEventListener {

    private static final String TAG = "SensorRecordingReceiver";

    private List<AccelerometerSensorRecord> sensorRecords;

    public SensorRecordingReceiver() {
        sensorRecords = new LinkedList<>();
    }

    public List<AccelerometerSensorRecord> getSensorRecords() {
        return sensorRecords;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
            return;

        long timestamp = event.timestamp;
        float xValue = event.values[0];
        float yValue = event.values[1];
        float zValue = event.values[2];

        Log.d(TAG, String.format("onSensorChanged: x -> %f, y -> %f, z -> %f", xValue, yValue, zValue));

        sensorRecords.add(new AccelerometerSensorRecord(timestamp, xValue, yValue, zValue));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "onAccuracyChanged: " + accuracy);
    }
}
