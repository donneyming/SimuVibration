package com.cms.simuvibration;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import com.cms.util.VibratorUtil;

import java.util.Timer;
import java.util.TimerTask;

public class VibraActivity extends Activity {

    SensorManager sensorManager;
    TextView accelerationTextView;
    TextView maxAccelerationTextView;

    float currentAcceleration = 0;
    float maxAcceleration = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 获取两个文本显示域
        accelerationTextView = (TextView) findViewById(R.id.acceleration);
        maxAccelerationTextView = (TextView) findViewById(R.id.maxAcceleration);
        // 获取sensor服务，选择加速度感应器
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometer = sensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // 注册事件
        sensorManager.registerListener(sensorEventListener, accelerometer,
                SensorManager.SENSOR_DELAY_FASTEST);

        Timer updateTimer = new Timer("gForceUpdate");
        updateTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                updateGUI();
                VibratorUtil.Vibrate(VibraActivity.this, 1000);   //震动100ms
            }
        }, 0, 1000);


    }

    // 添加的新方法，退出activity的时候，关闭监听器
    public void onPause() {
        sensorManager.unregisterListener(sensorEventListener);
        super.onPause();
    }

    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        // 系统设置的重力加速度标准值，设备在水平静止的情况下就承受这个压力，所以默认Y轴方向的加速度值为STANDARD_GRAVITY
        double calibration = SensorManager.STANDARD_GRAVITY;

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        public void onSensorChanged(SensorEvent event) {
            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];

            // 计算三个方向的加速度
            double a = Math.round(Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)
                    + Math.pow(z, 2)));

            // 消去原有的重力引起的压力
            currentAcceleration = Math.abs((float) (a - calibration));
            if (currentAcceleration > maxAcceleration)
                maxAcceleration = currentAcceleration;
        }
    };

    private void updateGUI() {
        /*
		 * 推荐的一个刷新UI的方法 Activity.runOnUiThread（Runnable） 在新的线程中更新UI
		 * Runnable是一个接口，需要你实现run方法，上面的TimerTask就是实现了这个接口同样需要实现run方法
		 */
        runOnUiThread(new Runnable() {
            public void run() {
                String currentG = currentAcceleration
                        / SensorManager.STANDARD_GRAVITY + "Gs";
                accelerationTextView.setText(currentG);
                accelerationTextView.invalidate();
                String maxG = maxAcceleration / SensorManager.STANDARD_GRAVITY
                        + "Gs";
                maxAccelerationTextView.setText(maxG);
                maxAccelerationTextView.invalidate();
            }
        });

    }

}
