package com.alperez.samples.listspagination.utils;

import android.widget.CompoundButton;

/**
 * Created by stanislav.perchenko on 10/16/2018
 */
public class CommunicationErrorEmulator implements CompoundButton.OnCheckedChangeListener {

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
    private static CommunicationErrorEmulator instance;

    public static CommunicationErrorEmulator getInstance() {
        if (instance == null) {
            synchronized (CommunicationErrorEmulator.class) {
                if (instance == null) {
                    instance = new CommunicationErrorEmulator();
                }
            }
        }
        return instance;
    }

    private CommunicationErrorEmulator() {}
}
