package com.example.rotationangletutorial;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private float mLastPitch, mLastRoll, mLastAzimuth;
    private Sensor mAccelerometer;
    private Sensor mGeoMagnetic;
    private SensorManager sManager;
    private float gravity[];
    private float magnetic[];
    private float accels[] = new float[3];
    private float mags[] = new float[3];
    private float values[] = new float[3];
    private float firstAzimuth;
    private float firstPitch;
    private float firstRoll;
    private float azimuth;
    private float pitch;
    private float roll;
    private boolean mInitialized;
    private boolean resetValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mInitialized = false;
        resetValues = false;
        sManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sManager.registerListener(this, mAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);
        mGeoMagnetic = sManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
        sManager.registerListener(this, mGeoMagnetic , SensorManager.SENSOR_DELAY_NORMAL);
    }
    protected void onResume() {
        super.onResume();
        sManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sManager.registerListener(this, mGeoMagnetic, SensorManager.SENSOR_DELAY_NORMAL);
    }
    protected void onPause() {
        super.onPause();
        sManager.unregisterListener(this);
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // can be safely ignored for this demo
    }
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_GAME_ROTATION_VECTOR:
                mags = event.values.clone();
                break;
            case Sensor.TYPE_ACCELEROMETER:
                accels = event.values.clone();
                break;
        }

        if (mags != null && accels != null) {
            gravity = new float[9];
            magnetic = new float[9];
            SensorManager.getRotationMatrix(gravity, magnetic, accels, mags);
            float[] outGravity = new float[9];
            SensorManager.remapCoordinateSystem(gravity, SensorManager.AXIS_X, SensorManager.AXIS_Z, outGravity);
            SensorManager.getOrientation(outGravity, values);
            //if first azimuth measurement has not been taken;
            if (!resetValues && values[0]*57.2957795f != -180.0) {
                firstAzimuth = values[0] * 57.2957795f;
                firstPitch = values[1] * 57.2957795f;
                firstRoll = values[2] * 57.2957795f;
                resetValues = true;
            }
            else {
                azimuth = values[0] * 57.2957795f - firstAzimuth;
                pitch = values[1] * 57.2957795f - firstPitch;
                roll = values[2] * 57.2957795f - firstRoll;
                mags = null;
                accels = null   ;
            }
        }
        TextView tvX = (TextView) findViewById(R.id.x_axis);
        TextView tvY = (TextView) findViewById(R.id.y_axis);
        TextView tvZ = (TextView) findViewById(R.id.z_axis);

        if (!mInitialized) {
            mLastPitch = pitch;
            mLastRoll = roll;
            mLastAzimuth = azimuth;
            tvX.setText("0.0");
            tvY.setText("0.0");
            tvZ.setText("0.0");
            mInitialized = true;
        } else {
            //float deltaX = Math.abs(mLastX - x);
            //float deltaY = Math.abs(mLastY - y);
            //float deltaZ = Math.abs(mLastZ - z);
            //if (deltaX < NOISE) deltaX = (float)0.0;
            //if (deltaY < NOISE) deltaY = (float)0.0;
            //if (deltaZ < NOISE) deltaZ = (float)0.0;
            mLastPitch = pitch;
            mLastRoll = roll;
            mLastAzimuth = azimuth;
            int iRoll = (int)roll;
            int iPitch = (int)pitch;
            int iAzimuth = (int)azimuth;
            tvX.setText(Float.toString(iRoll));
            tvY.setText(Float.toString(iPitch));
            tvZ.setText(Float.toString(iAzimuth));
        }
    }
}

