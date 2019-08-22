package com.my.bielik.task2.thread;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;

public class ProcessResponseThread extends HandlerThread {

    private Handler handler;

    public ProcessResponseThread() {
        super("ProcessResponseThread", Process.THREAD_PRIORITY_BACKGROUND);
    }

    public Handler getHandler() {
        return handler;
    }

    @Override
    protected void onLooperPrepared() {
        handler = new Handler();
    }
}
