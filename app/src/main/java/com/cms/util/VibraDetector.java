package com.cms.util;

import com.cms.simuvibration.SettingsActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

//振动检测器，用于检测振动并存储
/**
 *
 *
 */
public class VibraDetector implements SensorEventListener {
	public final static String TAG = "VibraDetector";

	public static float CURRENT_AX = 0;
	public static float CURRENT_AY = 0;
	public static float CURRENT_AZ = 0;

	public static float TIME = 0; // Time间隔

	/**
	 * 传入上下文的构造函数
	 *
	 * @param context
	 */
	public VibraDetector(Context context) {
		// TODO Auto-generated constructor stub
		super();
		if (SettingsActivity.sharedPreferences == null) {
			SettingsActivity.sharedPreferences = context.getSharedPreferences(
					SettingsActivity.SETP_SHARED_PREFERENCES,
					Context.MODE_PRIVATE);
		}
		TIME = SettingsActivity.sharedPreferences.getInt(
				SettingsActivity.TIME_VALUE, 3);
	}

	// public void setSensitivity(float sensitivity) {
	// SENSITIVITY = sensitivity; // 1.97 2.96 4.44 6.66 10.00 15.00 22.50
	// // 33.75
	// // 50.62
	// }

	// public void onSensorChanged(int sensor, float[] values) {
	@Override
	public void onSensorChanged(SensorEvent event) {
		Sensor sensor = event.sensor;
		synchronized (this) {
			int type = sensor.getType();
			switch (type) {
				case Sensor.TYPE_ACCELEROMETER:// 加速度
					CURRENT_AX = event.values[0];
					CURRENT_AY = event.values[1];
					CURRENT_AZ = event.values[2];
					break;
				case Sensor.TYPE_GYROSCOPE:// 陀螺仪
					CURRENT_AX = event.values[0];
					CURRENT_AY = event.values[1];
					CURRENT_AZ = event.values[2];
					break;
				default:
					break;
			}
			Log.i(TAG, "VibraDetector onSensorChanged()" + this);
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	}

}
