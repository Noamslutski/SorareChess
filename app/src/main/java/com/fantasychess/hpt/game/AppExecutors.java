package com.fantasychess.hpt.game;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Tiny helper to run DB/network work off the main thread and post results back. */
public final class AppExecutors {

    private static final ExecutorService IO = Executors.newFixedThreadPool(3);
    private static final Handler MAIN = new Handler(Looper.getMainLooper());

    private AppExecutors() { }

    public static void io(Runnable r) {
        IO.execute(r);
    }

    public static void main(Runnable r) {
        MAIN.post(r);
    }
}
