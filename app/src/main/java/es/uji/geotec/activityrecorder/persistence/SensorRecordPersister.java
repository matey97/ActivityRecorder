package es.uji.geotec.activityrecorder.persistence;

import android.os.Environment;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import es.uji.geotec.activityrecorder.model.AccelerometerSensorRecord;
import es.uji.geotec.activityrecorder.model.ActivityEnum;

public abstract class SensorRecordPersister {

    protected final String[] HEADER = {"timestamp", "x", "y", "z"};
    protected final String PATTERN = "dd-MM-yyyy_HH:mm:ss";

    protected ActivityEnum activity;

    public SensorRecordPersister(ActivityEnum activity) {
        this.activity = activity;
    }

    public abstract void saveSensorRecords(List<AccelerometerSensorRecord> records);
}
