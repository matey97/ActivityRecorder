package es.uji.geotec.activityrecorder.persistence;

import java.util.List;

import es.uji.geotec.activityrecorder.model.AccelerometerSensorRecord;
import es.uji.geotec.activityrecorder.model.ActivityEnum;

public class SensorRecordPersister {

    private LocalFilePersister filePersister;
    private FirebaseStoragePersister firebaseStoragePersister;
    private boolean isFirebaseEnabled;

    private static SensorRecordPersister instance;

    private SensorRecordPersister() {
        filePersister = new LocalFilePersister();
        firebaseStoragePersister = new FirebaseStoragePersister();
    }

    public static SensorRecordPersister getInstance() {
        if (instance == null)
            instance = new SensorRecordPersister();
        return instance;
    }

    public void setActivity(ActivityEnum activity) {
        filePersister.setActivity(activity);
        firebaseStoragePersister.setActivity(activity);
    }

    public void setFirebaseEnabled(boolean enabled){
        isFirebaseEnabled = enabled;
    }

    public void saveSensorRecords(List<AccelerometerSensorRecord> records) {
        String fileToUpload = filePersister.saveSensorRecords(records);

        if (isFirebaseEnabled)
            firebaseStoragePersister.uploadRecordsFile(fileToUpload);
    }
}
