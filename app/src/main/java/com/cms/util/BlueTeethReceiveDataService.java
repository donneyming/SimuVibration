package com.cms.util;

import java.net.Socket;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class BlueTeethReceiveDataService extends Service {
	private static final String TAG = "BlueTeethReceiveDataService.java";

	private Socket socket;
	private String workStatus; // 当前工作状况，null表示正在处理 |
	// success表示处理成功，failure表示处理失败
	public static Boolean mainThreadFlag = true; // 状态
	public static final String BROADCAST_ACTION = "com.example.corn";

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	private void doListen() {
		Log.i(TAG, "doListen()");
		// 开始监听
		while (mainThreadFlag) {
			// 开始监听数据
			new Thread(new ThreadReadWriterSocketServer(
					BlueTeethReceiveDataService.this, socket));
		}

	}

	public class ThreadReadWriterSocketServer implements Runnable {
		private Socket client = null;
		private Context context = null;

		public ThreadReadWriterSocketServer(Context context, Socket client) {
			this.context = context;
			this.client = client;
		}

		@Override
		public void run() {
			Receive();
		}

		private void Receive() {
			// 处理数据

			// 广播到前台Activity
			Intent intent = new Intent();
			intent.setAction(BROADCAST_ACTION);
			intent.putExtra("msg", "qqyumidi");
			sendBroadcast(intent);
		}
	}


}
