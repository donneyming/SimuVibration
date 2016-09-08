package com.cms.util;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class SocketService extends Service {
    public static String TAG = "SocketService";
    public static String serverIP = "192.168.1.104";
    public static int serverPort = 10010;

    MyServiceReceiver mReceiver;
    private boolean bThreadFlag = true;
    public TCPClientThread mTcpThread;
    Thread mthread = null;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        mReceiver = new MyServiceReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.cmd");
        registerReceiver(mReceiver, filter);
        Log.d(TAG, "onCreate");
    }

    // 前台Activity调用startService时，该方法自动执行
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        mTcpThread = new TCPClientThread();
        mthread = new Thread(mTcpThread);
        mthread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(mReceiver);// 取消注册的CommandReceiver
        Log.d(TAG, "onDestroy");
    }
    @Override
    public IBinder onBind(Intent arg0) {

        return null;
    }

    public void disconnectServer() {
        bThreadFlag = true;
    }

    public void connectServer() {
        Log.d(TAG, "connectServer");
        //threadFlag = false;
        try {
            mthread.join();
            mTcpThread = null;
            mTcpThread = new TCPClientThread();
            mthread = null;
            mthread = new Thread(mTcpThread);
            mthread.start();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e(TAG, e.getMessage());

        }

    }

    class TCPClientThread implements Runnable {
        @Override
        public void run() {
            try {
                Log.d(TAG, "TCPClientThread");
                startTCPClient();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG + ":TCPClientThread", e.getMessage());

            }
        }
    }

    private void startTCPClient() throws IOException {
        Log.d(TAG, "startTCPClient");
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        InetSocketAddress remote = new InetSocketAddress(serverIP, serverPort);
        channel.connect(remote);
      // if (!channel.connect(remote))
       //    Log.e(TAG + ":startTCPClient", "connect Failed");
        int i = 0;
        try {
            while (!channel.finishConnect()) {
                Thread.sleep(1000);
                i++;
                Log.d(TAG,"startTCPClient连接中...！");
                if (i > 5)
                    break;
                if (i <= 5)
                   Log.d(TAG,"startTCPClient连接成功！");
            }
        } catch (Exception e) {
            System.out.println("连接失败");
            Log.e(TAG + ":startTCPClient", e.getLocalizedMessage());
            return;
        }

        Selector selector = Selector.open();
        SelectionKey key = channel.register(selector, SelectionKey.OP_READ
                | SelectionKey.OP_WRITE);
        int index = 1;
        ByteBuffer bufferClient = ByteBuffer.allocate(100);
        while (bThreadFlag) {
            int ready = selector.select();
            if (0 == ready)
                continue;
            Set keys = selector.selectedKeys();
            Iterator it = keys.iterator();
            while (it.hasNext()) {
                SelectionKey key2 = (SelectionKey) it.next();
                if (key2.isReadable()) {
                    // 接收 server 发来的消息
                    Log.d(TAG,"client reading...");
                    SocketChannel channel2 = (SocketChannel) key2.channel();
                    ByteBuffer bytebuffer = ByteBuffer.allocateDirect(100);
                    int len = channel2.read(bytebuffer);
                    System.out.println("len = " + len);
                    bytebuffer.flip();
                    byte[] recv = new byte[64];
                    bytebuffer.get(recv, 0, len);
                    String data = new String(recv).trim();
                    Log.d(TAG,"读取到： " + data);
                    try {
                        Intent intent = new Intent();
                        intent.setAction("com.cms.util.SocketService");
                        intent.putExtra("value", data);
                        sendBroadcast(intent);
                    } catch (Exception ex) {
                        Log.e(TAG + ":startTCPClient", "读取到： " + ex.getMessage());
                    }
                } // else if (key2.isWritable()) {
                //不停的往 server 发送消息
                //System.out.println("client writing...");

                //bufferClient.put(("hello from client " + index).getBytes());
                //bufferClient.flip();
                //channel.write(bufferClient);
                //bufferClient.clear();
                //index++;
                //}
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.e(TAG + ":startTCPClient", e.getMessage());
                }
                it.remove();
            }
        }
    }
    // 接受Activity 传递过来的数据
    public class MyServiceReceiver extends BroadcastReceiver {
        public boolean mbRunFlagReceiver = false;
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "MyServiceReceiver:onReceive");

            if (intent.getAction().equals("android.intent.action.cmd")) {
                int cmd = intent.getIntExtra("cmd", -1);// 获取Extra信息
                if (cmd == 1) {
                    disconnectServer();
                }
                if (cmd == 2) {
                    connectServer();
                }
            }

        }
    }
}
