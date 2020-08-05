package es.uji.geotec.activityrecorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import es.uji.geotec.activityrecorder.model.ActivityEnum;
import es.uji.geotec.activityrecorder.service.SensorRecordingService;

import static es.uji.geotec.activityrecorder.service.SensorRecordingService.ACTIVITY;

public class MainActivity extends AppCompatActivity {

    private Intent intent;

    private TextView statusText;
    private Spinner activitySpinner;
    private Button startButton;
    private Button stopButton;

    private ActivityEnum activitySelected;
    private PermissionsManager permissionsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusText = findViewById(R.id.status_text);
        activitySpinner = findViewById(R.id.activity_spinner);
        startButton = findViewById(R.id.start_recording);
        stopButton = findViewById(R.id.stop_recording);

        permissionsManager = new PermissionsManager(this);

        setUpSpinner();
        updateUIElements(false);
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
        String text = running ? "Activity Recorder is running" : "Activity Recorder is not running";
        statusText.setText(text);

        activitySpinner.setEnabled(!running);

        startButton.setEnabled(!running);
        stopButton.setEnabled(running);
    }
}
