package com.example.timer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class MainActivity extends Activity {
    Chronometer chronometer;
    Button start, pause, stop;
    TextView reminder;
    EditText editBox;
    long pauseOff;
    boolean running = false;
    String GET_TIME, GET_ACTION, REMINDER_TEXT, time;
    SharedPreferences sharedPreferences;
    long current = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chronometer = findViewById(R.id.chronometer);
        start = findViewById(R.id.startButton);
        pause = findViewById(R.id.pauseButton);
        stop = findViewById(R.id.stopButton);
        reminder = findViewById(R.id.reminder);
        editBox = findViewById(R.id.enterBox);
        sharedPreferences = getSharedPreferences("com.example.timer", MODE_PRIVATE);
        //输出保存的reminder
        reminder.setText(sharedPreferences.getString(REMINDER_TEXT, ""));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        pauseOff = SystemClock.elapsedRealtime() - chronometer.getBase();
        outState.putLong("pauseOff", pauseOff);
        outState.putBoolean("running", running);
        outState.putString("chronometer", chronometer.getText().toString());
        //Log.e("time", "onSaveInstanceState " + (SystemClock.elapsedRealtime() - pauseOff) + " chronometer.getBase() " + chronometer.getBase());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        running = savedInstanceState.getBoolean("running");
        if (running == true) {
            Long pauseOff = savedInstanceState.getLong("pauseOff");
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOff);
            chronometer.start();
            this.running = true;
           //Log.e("time", "在运行  " + (SystemClock.elapsedRealtime() - pauseOff) + "");
        } else {
            String chString = savedInstanceState.getString("chronometer");
            this.chronometer.setBase(convertStrTimeToLong(chString));
            //Log.e("time", "不在运行 " + current + "");
            this.running = false;
        }


    }

    //开始键
    public void startClick(View v) {
        String check_null = editBox.getText().toString();
        if (Objects.equals(check_null,""))
        {
            reminder.setText("Please enter work out type first!");
            Toast.makeText(this,"Please enter work out type first!",Toast.LENGTH_SHORT).show();
            return;
        }
        //Log.e("time", "点击开始 " + running + "");
        if (running == false) {
            if (current != 0) {
                //Log.e("time", "点击开始 " + running + "" + current);

                //将时间设置为暂停时的时间
                chronometer.setBase(convertStrTimeToLong(chronometer.getText().toString()));
            } else {
                chronometer.setBase(convertStrTimeToLong(chronometer.getText().toString()));
//                chronometer.setBase(SystemClock.elapsedRealtime() - pauseOff);
                //Log.e("time", "点击开始 " + running + "" + (SystemClock.elapsedRealtime() - pauseOff));
            }
            chronometer.start();
            running = true;
        }
    }

    //暂停键
    public void pauseClick(View v) {
        Log.e("running", running + "");
        if (running == true) {
            chronometer.stop();
            pauseOff = SystemClock.elapsedRealtime() - chronometer.getBase();
            running = false;
            //Log.e("time", "暂停  " + pauseOff + " chronometer   " + chronometer.getBase());
        }
    }

    //停止键
    public void stopClick(View v) {
        chronometer.stop();
        GET_TIME = String.valueOf(chronometer.getText());//取时间
        GET_ACTION = editBox.getText().toString();//取动作
        reminder.setText("Your spent " + GET_TIME + " on " + GET_ACTION + " last time.");

        //保存reminder这句话，以便下次显示
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(REMINDER_TEXT, reminder.getText().toString());
        editor.apply();

        //计时器清零
        chronometer.setBase(SystemClock.elapsedRealtime());
        pauseOff = 0;
        running = false;
    }

    //记录时间并转换成Long格式
    protected long convertStrTimeToLong(String strTime) {
        String[] timeArry = strTime.split(":");
        long longTime = 0;
        if (timeArry.length == 2) {//如果时间是MM:SS格式
            longTime = Integer.parseInt(timeArry[0]) * 1000 * 60 + Integer.parseInt(timeArry[1]) * 1000;
        }
        return SystemClock.elapsedRealtime() - longTime;
    }

    //    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//        }
//    }
}

