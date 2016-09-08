package com.cms.simuvibration;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.cms.util.VibraDetector;

/**
 * 应用程序的设置界面
 */
public class SettingsActivity extends Activity {
    public static final String TIME_VALUE = "time_value";// 时间间隔
    public static final String AMPLITUDE_VALUE = "amplitude_value";// 振幅

    public static final String SETP_SHARED_PREFERENCES = "setp_shared_preferences";// 设置
    public static SharedPreferences sharedPreferences;
    private Editor editor;

    private TextView tv_time_value;
    private TextView tv_amplitude_value;

    private SeekBar sb_amplitude;
    private SeekBar sb_time;

    private int amplitude = 0;
    private int time = 0;
    private int weight = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.settings);
        addView();
        init();
        listener();
    }

    /**
     * SeekBar的拖动监听
     */
    private void listener() {
        sb_amplitude.setOnSeekBarChangeListener(new OnSeekBarChangeListener() { // 灵敏值动作的监听
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProgressChanged(SeekBar seekBar,
                                          int progress, boolean fromUser) {
                // TODO Auto-generated method stub
                amplitude = progress;
                tv_amplitude_value.setText(amplitude + "");
            }
        });
        sb_time.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                time = progress * 5 + 40;
                tv_time_value.setText(time + getString(R.string.cm));
            }
        });

    }

    private void init() {
        // TODO Auto-generated method stub
        if (sharedPreferences == null) { // SharedPreferences是Android平台上一个轻量级的存储类，
            // 主要是保存一些常用的配置比如窗口状态
            sharedPreferences = getSharedPreferences(SETP_SHARED_PREFERENCES,
                    MODE_PRIVATE);
        }
        editor = sharedPreferences.edit();
        amplitude = 10 - sharedPreferences.getInt(AMPLITUDE_VALUE, 7);
        time = sharedPreferences.getInt(TIME_VALUE, 70);

        sb_amplitude.setProgress(amplitude);
        sb_time.setProgress((time - 40) / 5); // 步长按钮在进度条上占得比例

        tv_amplitude_value.setText(amplitude + "");
        tv_time_value.setText(sb_time + getString(R.string.cm));
    }

    private void addView() {
        tv_time_value = (TextView) this.findViewById(R.id.time_value);
        tv_amplitude_value = (TextView) this.findViewById(R.id.amplitude_value);
        sb_amplitude = (SeekBar) this.findViewById(R.id.amplitude_spin);
        sb_time = (SeekBar) this.findViewById(R.id.time_spin);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save:
                editor.putInt(AMPLITUDE_VALUE, 10 - amplitude);
                editor.putInt(TIME_VALUE, time);

                editor.commit();
                Toast.makeText(SettingsActivity.this, "保存成功！", Toast.LENGTH_SHORT)
                        .show();
                this.finish();
                VibraDetector.TIME = 10 - time;
                break;
            case R.id.cancle:
                this.finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();

        init();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        init();
    }
}
