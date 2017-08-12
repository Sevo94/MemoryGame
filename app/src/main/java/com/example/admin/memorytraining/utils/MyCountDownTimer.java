package com.example.admin.memorytraining.utils;


import android.os.Handler;

import com.example.admin.memorytraining.interfaces.TimerCallbacks;

/**
 * Created by Admin on 8/9/2017.
 */

public class MyCountDownTimer {

    private long millisInFuture;
    private long countDownInterval;

    private TimerCallbacks timerCallbacks;
    private Handler handler;
    private Runnable counter;

    public MyCountDownTimer(long millisInFuture, long countDownInterval, TimerCallbacks timerCallbacks) {
        this.millisInFuture = millisInFuture;
        this.countDownInterval = countDownInterval;
        this.timerCallbacks = timerCallbacks;

        handler = new Handler();
    }

    public void start() {
        counter = new Runnable() {
            @Override
            public void run() {
                if (millisInFuture <= 0) {

                    if (timerCallbacks != null) {
                        timerCallbacks.onTimerFinish();
                    }
                } else {
                    long sec = millisInFuture / 1000;

                    if (timerCallbacks != null) {
                        timerCallbacks.onTimerInterval(sec);
                    }

                    millisInFuture -= countDownInterval;
                    handler.postDelayed(this, countDownInterval);
                }
            }
        };

        handler.post(counter);
    }

    public void stop() {
        if (handler != null) {
            handler.removeCallbacks(counter);
        }
    }
}
