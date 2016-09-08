package com.cms.simuvibration;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cms.util.SersorService;
import com.cms.util.SocketService;

import java.text.DecimalFormat;

/**
 * 应用程序的用户界面， 主要功能就是按照XML布局文件的内容显示界面， 并与用户进行交互 负责前台界面展示
 * 在android中Activity负责前台界面展示，service负责后台的需要长期运行的任务。
 * Activity和Service之间的通信主要由Intent负责
 */
@SuppressLint("HandlerLeak")
public class ControlActivity extends Activity {
    public static String TAG = "ControlActivity";

    private Button btn_start;// 开始按钮
    private Button btn_stop;// 停止按钮

    private boolean isAnotB = false;
    private boolean isRun = true;

    private TextView mServerIPTextView;
    private TextView mMsgTextView;
    private ReceiveBroadCast receiveBroadCast; // 广播实例
    private MyHandler handler;
    Intent mSensorservice, mSocketService;
    /***
     *
     */

    // 当创建一个新的Handler实例时, 它会绑定到当前线程和消息的队列中,开始分发数据
    // Handler有两个作用, (1) : 定时执行Message和Runnalbe 对象
    // (2): 让一个动作,在不同的线程中执行.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.control); // 设置当前屏幕

        if (SettingsActivity.sharedPreferences == null) {
            SettingsActivity.sharedPreferences = this.getSharedPreferences(
                    SettingsActivity.SETP_SHARED_PREFERENCES,
                    Context.MODE_PRIVATE);
        }

        Bundle extras = getIntent().getExtras();
        isAnotB = extras.getBoolean("run");

        handler = new MyHandler();
        receiveBroadCast = new ReceiveBroadCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.cms.util.SocketService");
        registerReceiver(receiveBroadCast, filter);
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        Log.d(TAG, "on resuame.");
        // 获取界面控件
        addView();
        // 初始化控件
        init();
        // this.mEditText.setText("18:DC:56:F8:26:3F"); //设置MAC

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(receiveBroadCast);
    }

    // The local server socket

    /**
     * 获取Activity相关控件
     */
    private void addView() {
        Log.i(TAG, "addView");
        btn_start = (Button) this.findViewById(R.id.start);
        btn_stop = (Button) this.findViewById(R.id.stop);

        mMsgTextView = (TextView) findViewById(R.id.msg_textview);
        mServerIPTextView = (EditText) findViewById(R.id.serverip_textview);

        Intent tmpSersorservice = new Intent(this, SersorService.class);
        stopService(tmpSersorservice);

        Intent tmpTCPservice = new Intent(this, SocketService.class);
        stopService(tmpTCPservice);
    }

    /**
     * 初始化界面
     */
    private void init() {
        btn_start.setEnabled(!SersorService.FLAG);
        btn_stop.setEnabled(SersorService.FLAG);
        if (SersorService.FLAG) {
            btn_stop.setText(getString(R.string.pause));
        }

        mSensorservice = new Intent(this, SersorService.class);
        mSocketService = new Intent(this, SocketService.class);

    }



    /**
     * 计算并格式化doubles数值，保留两位有效数字
     *
     * @param doubles
     * @return 返回当前路程
     */
    private String formatDouble(Double doubles) {
        DecimalFormat format = new DecimalFormat("####.##");
        String distanceStr = format.format(doubles);
        return distanceStr.equals(getString(R.string.zero)) ? getString(R.string.double_zero)
                : distanceStr;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start:
                startService(mSensorservice);
                SersorService.FLAG = true;
                btn_start.setEnabled(false);
                btn_stop.setEnabled(true);
                btn_stop.setText(getString(R.string.pause));
                break;
            case R.id.stop:
                // isRun = false;
                stopService(mSensorservice);
                SersorService.FLAG = false;
                // gifView.showCover();
                if (SersorService.FLAG) {
                    btn_stop.setText(getString(R.string.cancel));
                } else {
                  btn_stop.setText(getString(R.string.pause));
                    btn_stop.setEnabled(false);
                }
                btn_start.setEnabled(true);
                break;
            case R.id.connect_btn:
                this.mServerIPTextView.setText("192.168.1.104");
                SocketService.serverIP = mServerIPTextView.getText().toString();
                startService(mSocketService);
                Log.d(TAG, "btnConnect");
                break;
        }
    }

    /**
     * 得到一个格式化的时间
     *
     * @param time 时间 毫秒
     * @return 时：分：秒：毫秒
     */
    private String getFormatTime(long time) {
        time = time / 1000;
        long second = time % 60;
        long minute = (time % 3600) / 60;
        long hour = time / 3600;

        // 毫秒秒显示两位
        // String strMillisecond = "" + (millisecond / 10);
        // 秒显示两位
        String strSecond = ("00" + second)
                .substring(("00" + second).length() - 2);
        // 分显示两位
        String strMinute = ("00" + minute)
                .substring(("00" + minute).length() - 2);
        // 时显示两位
        String strHour = ("00" + hour).substring(("00" + hour).length() - 2);

        return strHour + ":" + strMinute + ":" + strSecond;
        // + strMillisecond;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_step, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;

            case R.id.ment_information:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        finish();
    }

    /**************
     * service 命令
     *********/
    static final int CMD_STOP_SERVICE = 0x01;
    static final int CMD_SEND_DATA = 0x02;
    static final int CMD_SYSTEM_EXIT = 0x03;
    static final int CMD_SHOW_TOAST = 0x04;

    public void showToast(String str) {// 显示提示信息
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
    }



    public void sendCmd(byte command, String value) {
        Intent intent = new Intent();// 创建Intent对象
        intent.setAction("android.intent.action.cmd");
        intent.putExtra("cmd", CMD_SEND_DATA);
        intent.putExtra("command", command);
        intent.putExtra("value", value);
        sendBroadcast(intent);// 发送广播
    }

    class MyHandler extends Handler {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "Handler");

            switch (msg.what) {
                case 1:
                    mMsgTextView.setText((String) msg.obj);

                    Log.d(TAG, "mTextView");

                    break;
            }
        }

    }

    public class ReceiveBroadCast extends BroadcastReceiver {
        public static final String TAG = "ReceiveBroadCast";

        @Override
        public void onReceive(Context context, Intent intent) {
            // 得到广播中得到的数据，并显示出来

            String strMsg = intent.getStringExtra("value");
            Log.d(TAG, "onReceive数据"+strMsg);


            try{
            if ("begin".equalsIgnoreCase(strMsg)) {
                startService(mSensorservice);
                btn_start.setEnabled(false);
                btn_stop.setEnabled(true);
                btn_stop.setText(getString(R.string.pause));
                // startTimer = System.currentTimeMillis();
                // tempTime = timer;
                SersorService.FLAG = true;
            } else if ("end".equalsIgnoreCase(strMsg)) {
                stopService(mSensorservice);
                SersorService.FLAG = false;

                // gifView.showCover();
                if (SersorService.FLAG) {
                    btn_stop.setText(getString(R.string.cancel));
                } else {
                    // VibraDetector.CURRENT_SETP = 0;
                    // tempTime = timer = 0;

                    btn_stop.setText(getString(R.string.pause));
                    btn_stop.setEnabled(false);
                }
                btn_start.setEnabled(true);
            }

            }
            catch (Exception e)
            {
                Log.e(TAG,e.getLocalizedMessage());
            }


            handler.sendMessage(Message.obtain(handler, 1, 0, 0, strMsg));
        }
    }
}
