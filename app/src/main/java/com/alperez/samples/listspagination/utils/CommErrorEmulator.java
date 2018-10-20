package com.alperez.samples.listspagination.utils;

import android.widget.CompoundButton;

/**
 * Created by stanislav.perchenko on 10/16/2018
 */
public class CommErrorEmulator implements CompoundButton.OnCheckedChangeListener {

    private boolean error;

    public synchronized boolean isError() {
        return error;
    }

    public synchronized void init() {
        error = false;
    }

    public synchronized void toggleError() {
        error = !error;
    }

    @Override
    public synchronized void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        error = isChecked;
    }

    /**********************************************************************************************/
    private static CommErrorEmulator instance;

    public static CommErrorEmulator getInstance() {
        if (instance == null) {
            synchronized (CommErrorEmulator.class) {
                if (instance == null) {
                    instance = new CommErrorEmulator();
                }
            }
        }
        return instance;
    }

    private CommErrorEmulator() {}
}
