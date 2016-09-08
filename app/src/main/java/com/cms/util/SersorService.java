
package com.cms.util;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.util.Log;
import  java.util.ArrayList;
import  java.util.List;
import java.util.Arrays;



public class SersorService extends Service {
	public static String TAG = "SersorService";
	private 	MsgList msgList;

	public static Boolean FLAG = false;// 服务运行标志

	private SensorManager mSensorManager;// 传感器服务
	private VibraDetector mDetector;// 传感器监听对象

	private PowerManager mPowerManager;// 电源管理服务
	private WakeLock mWakeLock;// 屏幕灯

	private Vibrator mVibrator;
	private Timer mTimer;
	public SimpleRate mSimpleRate;
	private WriteFileThread  wThread;

    private static  int  msgCountList[];


    static   Integer  ITYPE_ACCELEROMETER = 0;
    static   Integer  ITYPE_MAGNETIC_FIELD =1;
    static   Integer  ITYPE_ORIENTATION=2;
    static  Integer  ITYPE_GYROSCOPE=3;
    static   Integer   ITYPE_LIGHT=4;
    static   Integer ITYPE_PRESSURE=5;
    static   Integer  ITYPE_TEMPERATURE=6;
    static  Integer  ITYPE_PROXIMITY=7;
    static   Integer   ITYPE_GRAVITY=8;
    static   Integer   ITYPE_LINEAR_ACCELERATION=9;
    static   Integer   ITYPE_ROTATION_VECTOR=10;

    public  List<String> SersonTypeName  =  new ArrayList<String>(Arrays.asList("加速度",
    "磁场","方位","陀螺仪","亮度","压力","温度","接应","重力","线性加速度","旋转矢量"));

    @Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	/*
     * Sensor.TYPE_ACCELEROMETER加速度感应检测
     * Sensor.TYPE_MAGNETIC_FIELD磁场感应检测
     * Sensor.TYPE_ORIENTATION方位感应检测
     * Sensor.TYPE_GYROSCOPE陀螺仪感应检测
     * Sensor.TYPE_LIGHT亮度感应检测
     * Sensor.TYPE_PRESSURE压力感应检测
     * Sensor.TYPE_TEMPERATURE温度感应检测
     * Sensor.TYPE_PROXIMITY接近感应检测
     * */
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		FLAG = true;// 标记为服务正在运行
		msgList = MsgList.instance();
		wThread = new WriteFileThread(msgList);
        mSimpleRate = new SimpleRate();

		// 创建监听器类，实例化监听对象
        mDetector = new VibraDetector(this);

		// 获取传感器的服务，初始化传感器
		mSensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
		// 注册传感器，注册监听器
		// 加速度传感器&&陀螺仪传感器
		mSensorManager.registerListener(sensorEventListener,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                mSimpleRate.get_SENSOR_RATE_FAST());
		mSensorManager.registerListener(sensorEventListener,
				mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                mSimpleRate.get_SENSOR_RATE_FAST());
		//磁场感应
		mSensorManager.registerListener(sensorEventListener,
				mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                mSimpleRate.get_SENSOR_RATE_FAST());
		//压力感应
		mSensorManager.registerListener(sensorEventListener,
				mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE),
                mSimpleRate.get_SENSOR_RATE_FAST());


		mSensorManager.registerListener(sensorEventListener,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION ),
                mSimpleRate.get_SENSOR_RATE_FAST());
		mSensorManager.registerListener(sensorEventListener,
				mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT ),
                mSimpleRate.get_SENSOR_RATE_FAST());

		mSensorManager.registerListener(sensorEventListener,
				mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY ),
                mSimpleRate.get_SENSOR_RATE_FAST());
		mSensorManager.registerListener(sensorEventListener,
				mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY ),
                mSimpleRate.get_SENSOR_RATE_FAST());
		mSensorManager.registerListener(sensorEventListener,
				mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION ),
                mSimpleRate.get_SENSOR_RATE_FAST());
		mSensorManager.registerListener(sensorEventListener,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
                mSimpleRate.get_SENSOR_RATE_FAST());


        mVibrator = (Vibrator) super.getApplication().getSystemService(
                Service.VIBRATOR_SERVICE);
        mTimer = new Timer("gForceUpdate");
        mTimer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				for (int i = 0; i < msgList.GetSize(); i++) {
					MsgItem str = msgList.GetMsg(i);
					wThread.writelogFile(str.FileName, str.id+","+str.msgText);
					msgList.RemoveMsg(str);
				}
			}
		}, 0, 1000);

		// 电源管理服务
		mPowerManager = (PowerManager) this
				.getSystemService(Context.POWER_SERVICE);
		mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK
				| PowerManager.ACQUIRE_CAUSES_WAKEUP, "S");
		mWakeLock.acquire();


        msgCountList =  new int[11];
		Log.d(TAG, "SersorService onCreate()" + this);


		//wThread.start();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		FLAG = false;// 服务停止
		if (mDetector != null) {
			mSensorManager.unregisterListener(mDetector);
		}
        mTimer.cancel();

		mSensorManager.unregisterListener(sensorEventListener);
		if (mWakeLock != null) {
			mWakeLock.release();
		}

        Log.d(TAG, "SersorService onDestroy()" + this);

    }

    private final SensorEventListener sensorEventListener;
    {
        sensorEventListener = new SensorEventListener() {
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
            public void onSensorChanged(SensorEvent event) {
                Sensor sensor = event.sensor;
                synchronized (this) {
                    int type = sensor.getType();
                    //String str =  event.values[0] + "," + event.values[1] + "," + event.values[2]+ ";";
                    double result = Math.sqrt((event.values[0] * event.values[0] + event.values[1] * event.values[1] + event.values[2] * event.values[2]));
                    String tmp = "";
                    MsgItem item = new MsgItem();
                    item.msgText = result + "";
                    item.createTime = System.currentTimeMillis();
                    switch (type) {
                        case Sensor.TYPE_ACCELEROMETER:
                            tmp = "加速度";
                            //WriteFile.writelogFile("加速度.txt",str+"\r\n");
                            //WriteFile.writelogFile("加速度"+":"+str+"\r\n");
                            msgCountList[ITYPE_ACCELEROMETER]++;
                            item.FileName = SersonTypeName.get(ITYPE_ACCELEROMETER);
                            item.id = msgCountList[ITYPE_ACCELEROMETER];
                            Log.d(TAG, "ITYPE_ACCELEROMETER:" + ITYPE_ACCELEROMETER);
                            System.out.print("ITYPE_ACCELEROMETER:" + ITYPE_ACCELEROMETER);
                            break;
                        case Sensor.TYPE_MAGNETIC_FIELD:
                            //WriteFile.writelogFile("磁场.txt" , str+"\r\n");
                            //WriteFile.writelogFile("磁场"+":"+str+"\r\n");
                            tmp = "磁场";
                            msgCountList[ITYPE_MAGNETIC_FIELD]++;
                            item.FileName = SersonTypeName.get(ITYPE_MAGNETIC_FIELD);
                            item.id = msgCountList[ITYPE_MAGNETIC_FIELD];
                            break;
                        case Sensor.TYPE_ORIENTATION:
                            //WriteFile.writelogFile("定位.txt" , str+"\r\n");
                            //WriteFile.writelogFile("定位"+":"+str+"\r\n");
                            //tmp ="定位";
                            msgCountList[ITYPE_ORIENTATION]++;
                            item.FileName = SersonTypeName.get(ITYPE_ORIENTATION);
                            item.id = msgCountList[ITYPE_ORIENTATION];
                            break;
                        case Sensor.TYPE_GYROSCOPE:
                            //WriteFile.writelogFile("陀螺仪.txt" , str+"\r\n");
                            //WriteFile.writelogFile("陀螺仪"+":"+str+"\r\n");
                            //tmp ="陀螺仪";
                            msgCountList[ITYPE_GYROSCOPE]++;
                            item.FileName = SersonTypeName.get(ITYPE_GYROSCOPE);
                            item.id = msgCountList[ITYPE_GYROSCOPE];
                            break;
                        case Sensor.TYPE_LIGHT:
                            //WriteFile.writelogFile("光线.txt" , str+"\r\n");
                            //WriteFile.writelogFile("光线"+":"+str+"\r\n");
                            //tmp ="光线";
                            msgCountList[ITYPE_LIGHT]++;
                            item.FileName = SersonTypeName.get(ITYPE_LIGHT);
                            item.id = msgCountList[ITYPE_LIGHT];
                            break;
                        case Sensor.TYPE_PRESSURE:
                            // WriteFile.writelogFile("压力.txt" , str+"\r\n");
                            //WriteFile.writelogFile("压力"+":"+str+"\r\n");
                            //tmp ="压力";
                            msgCountList[ITYPE_PRESSURE]++;
                            item.FileName = SersonTypeName.get(ITYPE_PRESSURE);
                            item.id = msgCountList[ITYPE_PRESSURE];
                            break;
                        case Sensor.TYPE_PROXIMITY:
                            // WriteFile.writelogFile("距离.txt" , str+"\r\n");
                            //WriteFile.writelogFile("距离"+":"+str+"\r\n");
                            //tmp ="距离";
                            msgCountList[ITYPE_PROXIMITY]++;
                            item.FileName = SersonTypeName.get(ITYPE_PROXIMITY);
                            item.id = msgCountList[ITYPE_PROXIMITY];
                            break;
                        case Sensor.TYPE_GRAVITY:
                            // WriteFile.writelogFile("重力.txt" , str+"\r\n");
                            //WriteFile.writelogFile("重力"+":"+str+"\r\n");
                            msgCountList[ITYPE_GRAVITY]++;
                            item.FileName = SersonTypeName.get(ITYPE_GRAVITY);
                            item.id = msgCountList[ITYPE_GRAVITY];
                            //tmp ="重力";
                            break;
                       case Sensor.TYPE_LINEAR_ACCELERATION:
                            // WriteFile.writelogFile("线性加速度.txt" , str+"\r\n");
                            //WriteFile.writelogFile("线性加速度"+":"+str+"\r\n");
                            //tmp ="线性加速度";
                            msgCountList[ITYPE_LINEAR_ACCELERATION]++;
                            item.FileName = SersonTypeName.get(ITYPE_LINEAR_ACCELERATION);
                            item.id = msgCountList[ITYPE_LINEAR_ACCELERATION];
                            break;
                      case Sensor.TYPE_ROTATION_VECTOR:
                            // WriteFile.writelogFile("旋转矢量.txt" , str+"\r\n");
                            //WriteFile.writelogFile("旋转矢量"+":"+str+"\r\n");
                            //tmp ="旋转矢量";
                            msgCountList[ITYPE_ROTATION_VECTOR]++;
                            item.FileName = SersonTypeName.get(ITYPE_ROTATION_VECTOR);
                            item.id = msgCountList[ITYPE_ROTATION_VECTOR];
                            break;

                        default:
                            // WriteFile.writelogFile("1NORMAL.txt" , str+"\r\n");
                            //WriteFile.writelogFile(type+":"+str+"\r\n");
                            tmp = String.valueOf(type);
                            break;
                    }
                    Log.d(TAG, "SersorService SensorEventListener()" + this);

                    msgList.AddMsg(item);
                }

            }
        };
    }

}
