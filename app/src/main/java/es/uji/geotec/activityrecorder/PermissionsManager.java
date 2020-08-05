package es.uji.geotec.activityrecorder;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PermissionsManager {

    public static final int REQUEST_CODE = 53;

    private List<String> permissionsRequired = Arrays.asList(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    );

    private Context context;
    private List<String> notGranted;

    public PermissionsManager(Context context) {
        this.context = context;
        this.notGranted = new ArrayList<>();
    }

    public boolean checkIfPermissionsNeeded() {
        notGranted.clear();
        for (String permission : permissionsRequired) {
            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                notGranted.add(permission);
            }
        }

        return !notGranted.isEmpty();
    }

    public void requestPermissions() {
        if (notGranted.size() > 0) {
            ActivityCompat.requestPermissions((Activity) context, notGranted.toArray(new String[notGranted.size()]), REQUEST_CODE);
        }
    }
}

