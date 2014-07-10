package com.howell.sensor.sensor;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class MainActivity extends ActionBarActivity implements SensorEventListener {

  private SensorManager sensorManager;
  private boolean color = false;
  private TextView textView;
  private long lastUpdate;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    textView = (TextView) findViewById(R.id.textView1);

    sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
    List<Sensor> list = sensorManager.getSensorList(Sensor.TYPE_ALL);

    StringBuilder data = new StringBuilder();
    for (Sensor sensor : list) {
      data.append(sensor.getName()).append("\n");
      data.append(sensor.getVendor()).append("\n");
      data.append(sensor.getVersion()).append("\n");

    }
    textView.setText(data);
    lastUpdate = System.currentTimeMillis();
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();
    if (id == R.id.action_settings) {
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  /**
   * Called when sensor values have changed. <p>See {@link android.hardware.SensorManager SensorManager} for details on
   * possible sensor types. <p>See also {@link android.hardware.SensorEvent SensorEvent}.
   * <p/>
   * <p><b>NOTE:</b> The application doesn't own the {@link android.hardware.SensorEvent event} object passed as a
   * parameter and therefore cannot hold on to it. The object may be part of an internal pool and may be reused by the
   * framework.
   *
   * @param event the {@link android.hardware.SensorEvent SensorEvent}.
   */
  @Override
  public void onSensorChanged(SensorEvent event) {

    if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
      getAccelerometer(event);
    }
  }

  private void getAccelerometer(SensorEvent event) {
    float[] values = event.values;
    // Movement
    float x = values[0];
    float y = values[1];
    float z = values[2];

    float accelationSquareRoot = (x * x + y * y + z * z)
            / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
    long actualTime = event.timestamp;
    if (accelationSquareRoot >= 2) //
    {
      if (actualTime - lastUpdate < 200) {
        return;
      }
      lastUpdate = actualTime;
      Toast.makeText(this, "Device was shuffed", Toast.LENGTH_SHORT)
              .show();
      if (color) {
        textView.setBackgroundColor(Color.GREEN);
      } else {
        textView.setBackgroundColor(Color.RED);
      }
      color = !color;
    }
  }

  /**
   * Called when the accuracy of the registered sensor has changed.
   * <p/>
   * <p>See the SENSOR_STATUS_* constants in {@link android.hardware.SensorManager SensorManager} for details.
   */
  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {
  }

  @Override
  protected void onResume() {
    super.onResume();
    // register this class as a listener for the orientation and
    // accelerometer sensors
    sensorManager.registerListener(this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL);
  }

  @Override
  protected void onPause() {
    // unregister listener
    super.onPause();
    sensorManager.unregisterListener(this);
  }
}
