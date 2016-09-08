package com.cms.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class BlueteethService extends Service {
	public static String TAG = "BlueteethService";

	public boolean threadFlag = true;
	MyThread myThread;
	CommandReceiver cmdReceiver;// 继承自BroadcastReceiver对象，用于得到Activity发送过来的命令

	/************** service 命令 *********/
	static final int CMD_STOP_SERVICE = 0x01;
	static final int CMD_SEND_DATA = 0x02;
	static final int CMD_SYSTEM_EXIT = 0x03;
	static final int CMD_SHOW_TOAST = 0x04;

	private BluetoothAdapter mBluetoothAdapter = null;
	private BluetoothChat mChatService = null;
	private BluetoothSocket btSocket = null;
	private OutputStream outStream = null;
	private InputStream inStream = null;
	public boolean bluetoothFlag = true;
	private static final UUID MY_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private static String address = "18:DC:56:F8:26:3F"; // <==要连接的蓝牙设备MAC地址

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.i(TAG, "onCreate");

	}

	// 前台Activity调用startService时，该方法自动执行
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onStartCommand");

		cmdReceiver = new CommandReceiver();
		mChatService = new BluetoothChat(this, null);

		IntentFilter filter = new IntentFilter();// 创建IntentFilter对象
		// 注册一个广播，用于接收Activity传送过来的命令，控制Service的行为，如：发送数据，停止服务等
		filter.addAction("android.intent.action.cmd");
		// 注册Broadcast Receiver
		registerReceiver(cmdReceiver, filter);
		doJob();// 调用方法启动线程
		//	return super.onStartCommand(intent, flags, startId);

		return START_REDELIVER_INTENT;


	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.i(TAG, "onDestroy");
		this.unregisterReceiver(cmdReceiver);// 取消注册的CommandReceiver
		threadFlag = false;
		boolean retry = true;
		while (retry) {
			try {
				myThread.join();
				retry = false;
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	public class MyThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			connectDevice();// 连接蓝牙设备
			while (threadFlag) {
				/*int value = readByte();
				if (value != -1) {
					DisplayToast(value + "");
				}*/

				try {
					Thread.sleep(50);
				} catch (Exception e) {
					e.printStackTrace();
					Log.e(TAG, e.getMessage());

				}
			}
		}
	}

	public void doJob() {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		Log.i(TAG, "doJob");

		if (mBluetoothAdapter == null) {
			DisplayToast("蓝牙设备不可用，请打开蓝牙！");
			Log.e(TAG, "蓝牙设备不可用，请打开蓝牙！");
			bluetoothFlag = false;
			return;
		}

		if (!mBluetoothAdapter.isEnabled()) {
			DisplayToast("请打开蓝牙并重新运行程序！");
			bluetoothFlag = false;
			stopService();
			showToast("请打开蓝牙并重新运行程序！");
			Log.e(TAG, "请打开蓝牙并重新运行程序！");

			return;
		}
		showToast("搜索到蓝牙设备!");
		Log.i(TAG, "搜索到蓝牙设备!");

		threadFlag = true;

		connectDevice();
		//myThread = new MyThread();
		//myThread.start();

	}

	public void connectDevice() {
		DisplayToast("正在尝试连接蓝牙设备，请稍后····");
		Log.i(TAG, "connectDevice");

		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		if (mChatService.getState() != BluetoothChat.STATE_CONNECTED) {
			mChatService.connect(device, false);
		}
		try {
			btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
		} catch (IOException e) {
			DisplayToast("套接字创建失败！");
			Log.e(TAG, "套接字创建失败！");

			bluetoothFlag = false;
		}
		DisplayToast("成功连接蓝牙设备！");
		mBluetoothAdapter.cancelDiscovery();
		try {
			btSocket.connect();
			DisplayToast("连接成功建立，可以开始操控了!");
			showToast("连接成功建立，可以开始操控了!");
			bluetoothFlag = true;
		} catch (IOException e) {
			try {
				btSocket.close();
				bluetoothFlag = false;
				Log.e(TAG, e.getMessage());

			} catch (IOException e2) {
				DisplayToast("连接没有建立，无法关闭套接字！");
				Log.e(TAG, e2.getMessage());
			}
		}

		/*
		 * if (bluetoothFlag) { try { inStream = btSocket.getInputStream(); }
		 * catch (IOException e) { e.printStackTrace(); } // 绑定读接口
		 *
		 * try { outStream = btSocket.getOutputStream(); } catch (IOException e)
		 * { e.printStackTrace(); } // 绑定写接口
		 *
		 * }
		 */
	}

	public void sendCmd(byte cmd, int value)// 串口发送数据
	{
		if (!bluetoothFlag) {
			return;
		}
		byte[] msgBuffer = new byte[5];
		msgBuffer[0] = cmd;
		msgBuffer[1] = (byte) (value >> 0 & 0xff);
		msgBuffer[2] = (byte) (value >> 8 & 0xff);
		msgBuffer[3] = (byte) (value >> 16 & 0xff);
		msgBuffer[4] = (byte) (value >> 24 & 0xff);

		try {
			outStream.write(msgBuffer, 0, 5);
			outStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, e.getMessage());
		}
	}

	public int readByte() {// return -1 if no data
		int ret = -1;
		if (!bluetoothFlag) {
			return ret;
		}
		try {
			ret = inStream.read();
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, e.getMessage());

		}
		return ret;
	}

	public void stopService() {// 停止服务
		threadFlag = false;// 停止线程
		stopSelf();// 停止服务
	}

	public void showToast(String str) {// 显示提示信息
		Intent intent = new Intent();
		intent.putExtra("cmd", CMD_SHOW_TOAST);
		intent.putExtra("str", str);
		intent.setAction("android.intent.action.lxx");
		sendBroadcast(intent);
	}

	public void DisplayToast(String str) {
		Log.d(TAG, str);
	}

	// 接收Activity传送过来的命令
	private class CommandReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals("android.intent.action.cmd")) {
				int cmd = intent.getIntExtra("cmd", -1);// 获取Extra信息
				if (cmd == CMD_STOP_SERVICE) {
					stopService();
				}

				if (cmd == CMD_SEND_DATA) {
					byte command = intent.getByteExtra("command", (byte) 0);
					String value = intent.getStringExtra("value");
					if (value.length() > 0) {
						// Get the message bytes and tell the BluetoothChatService to write
						byte[] send = value.getBytes();
						mChatService.write(send);
					}
					//sendCmd(command, value);发送
				}

			}
		}
	}

}
