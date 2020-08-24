package es.uji.geotec.activityrecorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import es.uji.geotec.activityrecorder.model.ActivityEnum;
import es.uji.geotec.activityrecorder.persistence.SensorRecordPersister;
import es.uji.geotec.activityrecorder.service.SensorRecordingService;

import static es.uji.geotec.activityrecorder.service.SensorRecordingService.ACTIVITY;

public class MainActivity extends AppCompatActivity {

    private static final String RUNNING_KEY = "RUNNING";

    private Intent intent;

    private TextView statusText;
    private Spinner activitySpinner;
    private Button startButton;
    private Button stopButton;
    private Switch firebaseSwitch;

    private ActivityEnum activitySelected;
    private PermissionsManager permissionsManager;
    private PowerManager powerManager;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusText = findViewById(R.id.status_text);
        activitySpinner = findViewById(R.id.activity_spinner);
        startButton = findViewById(R.id.start_recording);
        stopButton = findViewById(R.id.stop_recording);
        firebaseSwitch = findViewById(R.id.firebase_switch);

        permissionsManager = new PermissionsManager(this);
        powerManager = (PowerManager) getSystemService(POWER_SERVICE);

        requestBatteryOptmizationsIfNeeded();
        setUpSpinner();

        preferences = getPreferences(Context.MODE_PRIVATE);
        boolean running = preferences.getBoolean(RUNNING_KEY, false);
        updateUIElements(running);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean granted = true;
        if (grantResults.length > 0) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    granted = false;
                }
            }
        }

        if (granted) {
            startRecording();
        }
    }

    public void onStartClicked(View v) {
        if (permissionsManager.checkIfPermissionsNeeded()) {
            permissionsManager.requestPermissions();
            return;
        }

        startRecording();
    }

    public void onStopClicked(View v) {
        stopService(intent);
        updateUIElements(false);
    }

    public void onSwitchClicked(View v) {
        boolean firebaseChecked = firebaseSwitch.isChecked();
        SensorRecordPersister.getInstance().setFirebaseEnabled(firebaseChecked);
    }

    private void requestBatteryOptmizationsIfNeeded() {
        if (powerManager.isIgnoringBatteryOptimizations(getPackageName()))
            return;

        Intent intent = new Intent(
                Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                Uri.parse("package:"+getPackageName())
        );
        startActivity(intent);
    }

    private void startRecording() {
        intent = new Intent(this, SensorRecordingService.class);
        intent.putExtra(ACTIVITY, (ActivityEnum) activitySpinner.getSelectedItem());

        startForegroundService(intent);
        updateUIElements(true);
    }

    private void setUpSpinner(){
        ArrayAdapter<ActivityEnum> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, ActivityEnum.values());
        activitySpinner.setAdapter(adapter);
    }

    private void updateUIElements(boolean running) {
        preferences.edit().putBoolean(RUNNING_KEY, running).apply();

        String text = running ? "Activity Recorder is running" : "Activity Recorder is not running";
        statusText.setText(text);

        activitySpinner.setEnabled(!running);

        startButton.setEnabled(!running);
        stopButton.setEnabled(running);
    }
}
