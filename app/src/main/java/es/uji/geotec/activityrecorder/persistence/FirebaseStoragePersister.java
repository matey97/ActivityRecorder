package es.uji.geotec.activityrecorder.persistence;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.File;
import es.uji.geotec.activityrecorder.model.ActivityEnum;

public class FirebaseStoragePersister {

    private static final String TAG = "FirebaseStoragePersister";
    private final String STORAGE_LOCATION = "RAW_DATA";

    private ActivityEnum activity;
    private StorageReference storageReference;

    public FirebaseStoragePersister() {
        storageReference = FirebaseStorage.getInstance().getReference(STORAGE_LOCATION);
    }

    public void setActivity(ActivityEnum activityEnum) {
        this.activity = activityEnum;
    }

    public void uploadRecordsFile(String fileName) {
        Uri file = Uri.fromFile(new File(fileName));
        Log.d(TAG, "uploadRecordsFile: " + file.getPath());
        Log.d(TAG, "uploadRecordsFile: ref " + activity + "/" + file.getLastPathSegment());
        StorageReference recordRef = storageReference.child(activity + "/" + file.getLastPathSegment());

        recordRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "onSuccess: file uploaded");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: upload failed --> " + e.getMessage());
                    }
                });

    }
}
