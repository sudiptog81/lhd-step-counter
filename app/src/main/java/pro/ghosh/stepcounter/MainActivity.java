package pro.ghosh.stepcounter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private final String TAG = "MainActivityDebug";
    private final int ACTIVITY_RECOGNITION_REQUEST_CODE = 100;

    private boolean isRunning = false;
    private float stepCount = 0.0f;
    private Sensor sensor = null;
    private SensorManager sensorManager = null;
    private TextView tvCounter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvCounter = (TextView) findViewById(R.id.tv_counter);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        if (sensorManager == null) {
            Toast.makeText(this, "SensorManager is null", Toast.LENGTH_SHORT).show();
            return;
        }

        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        if (sensor == null) {
            Toast.makeText(this, "Sensor not found", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (
                ContextCompat
                        .checkSelfPermission(
                                this,
                                Manifest.permission.ACTIVITY_RECOGNITION
                        )
                        != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, "Permission Not Granted", Toast.LENGTH_SHORT).show();

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACTIVITY_RECOGNITION
                    },
                    ACTIVITY_RECOGNITION_REQUEST_CODE
            );
        }

        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);

        isRunning = true;
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (isRunning) {
            sensorManager.unregisterListener(this);
            isRunning = false;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (isRunning) {
            if (event != null) {
                stepCount += event.values[0];

                tvCounter.setText("" + stepCount);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}