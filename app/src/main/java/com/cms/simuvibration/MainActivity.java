package com.cms.simuvibration;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.cms.util.WriteFileThread;

import java.util.List;

public class MainActivity extends Activity {
	public final static String TAG = "MainActivity";

	private TextView mSensorTextView, mServerTextView, mClientTextView;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);

        mSensorTextView = (TextView) findViewById(R.id.sensor_textview);
        mServerTextView = (TextView) findViewById(R.id.server_textview);
        mClientTextView = (TextView) findViewById(R.id.client_textview);
        mServerTextView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, ControlActivity.class);
				Bundle bundle = new Bundle();
				bundle.putBoolean("run", true);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

        mClientTextView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, ControlActivity.class);
				Bundle bundle = new Bundle();
				bundle.putBoolean("run", false);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		getSensor();
        mClientTextView.setMovementMethod(ScrollingMovementMethod.getInstance());

	}


	private void getSensor() {
		SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

		// 从传感器管理器中获得全部的传感器列表
		List<Sensor> allSensors = sm.getSensorList(Sensor.TYPE_ALL);

		// 显示有多少个传感器
        mSensorTextView.setText("经检测该手机有" + allSensors.size() + "个传感器，他们分别是：\n");
		Log.i(TAG, "经检测该手机有" + allSensors.size() + "个传感器" );
		// 显示每个传感器的具体信息
		String result ="";
		for (Sensor s : allSensors) {

			String tempString = "\n" + "  设备名称：" + s.getName() + "\n"
					+ "  设备版本：" + s.getVersion() + "\n" + "  供应商："
					+ s.getVendor()  +"\n";

			switch (s.getType()) {
				case Sensor.TYPE_ACCELEROMETER:
                    mSensorTextView.setText(mSensorTextView.getText().toString()
							+ s.getType() + " 加速度传感器accelerometer" + tempString);
					result +=mSensorTextView.getText().toString()
							+ s.getType() + " 加速度传感器accelerometer" + tempString;
					break;
				case Sensor.TYPE_GYROSCOPE:
                    mSensorTextView.setText(mSensorTextView.getText().toString()
							+ s.getType() + " 陀螺仪传感器gyroscope" + tempString);
					result +=mSensorTextView.getText().toString()
							+ s.getType() + " 陀螺仪传感器gyroscope" + tempString;
					break;
				case Sensor.TYPE_LIGHT:
                    mSensorTextView.setText(mSensorTextView.getText().toString()
							+ s.getType() + " 环境光线传感器light" + tempString);
					result +=mSensorTextView.getText().toString()
							+ s.getType() + " 环境光线传感器light" + tempString;
					break;
				case Sensor.TYPE_MAGNETIC_FIELD:
                    mSensorTextView.setText(mSensorTextView.getText().toString()
							+ s.getType() + " 电磁场传感器magnetic field" + tempString);
					result +=mSensorTextView.getText().toString()
							+ s.getType() + " 电磁场传感器magnetic field" + tempString;
					break;
				case Sensor.TYPE_ORIENTATION:
                    mSensorTextView.setText(mSensorTextView.getText().toString()
							+ s.getType() + " 方向传感器orientation" + tempString);
					result +=mSensorTextView.getText().toString()
							+ s.getType() + " 方向传感器orientation" + tempString;
					break;
				case Sensor.TYPE_PRESSURE:
                    mSensorTextView.setText(mSensorTextView.getText().toString()
							+ s.getType() + " 压力传感器pressure" + tempString);
					result +=mSensorTextView.getText().toString()
							+ s.getType() + " 压力传感器pressure" + tempString;
					break;
				case Sensor.TYPE_PROXIMITY:
                    mSensorTextView.setText(mSensorTextView.getText().toString()
							+ s.getType() + " 距离传感器proximity" + tempString);
					result +=mSensorTextView.getText().toString()
							+ s.getType() + " 距离传感器proximity" + tempString;
					break;
				case Sensor.TYPE_TEMPERATURE:
                    mSensorTextView.setText(mSensorTextView.getText().toString()
							+ s.getType() + " 温度传感器temperature" + tempString);
					result +=mSensorTextView.getText().toString()
							+ s.getType() + " 温度传感器temperature" + tempString;
					break;
				default:
                    mSensorTextView.setText(mSensorTextView.getText().toString()
							+ s.getType() + " 未知传感器" + tempString);
					result +=mSensorTextView.getText().toString()
							+ s.getType() + " 未知传感器" + tempString;
					break;
			}

			WriteFileThread.writelogFile("SENSOR.txt",result);

		}


	}

}
