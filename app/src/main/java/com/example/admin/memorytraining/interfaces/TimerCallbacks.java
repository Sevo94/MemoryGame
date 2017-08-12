package com.example.admin.memorytraining.interfaces;

/**
 * Created by Admin on 8/8/2017.
 */

public interface TimerCallbacks {

    void onTimerFinish();

    void onTimerStarted();

    void onTimerInterval(long timerVal);
}
