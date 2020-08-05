package es.uji.geotec.activityrecorder.persistence;

import java.util.List;

import es.uji.geotec.activityrecorder.model.AccelerometerSensorRecord;
import es.uji.geotec.activityrecorder.model.ActivityEnum;

public class SensorRecordPersister {


    private ActivityEnum activity;
    private LocalFilePersister filePersister;
    private FirebaseStoragePersister firebaseStoragePersister;

    public SensorRecordPersister(ActivityEnum activity) {
        this.activity = activity;
        filePersister = new LocalFilePersister(activity);
        firebaseStoragePersister = new FirebaseStoragePersister(activity);
    }

    public void saveSensorRecords(List<AccelerometerSensorRecord> records) {
        String fileToUpload = filePersister.saveSensorRecords(records);
        firebaseStoragePersister.uploadRecordsFile(fileToUpload);
    }
}
