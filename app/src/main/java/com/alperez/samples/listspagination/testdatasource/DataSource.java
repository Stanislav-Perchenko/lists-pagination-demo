package com.alperez.samples.listspagination.testdatasource;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.alperez.samples.listspagination.utils.AppError;
import com.alperez.samples.listspagination.utils.SimpleAppError;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by stanislav.perchenko on 4/9/2018.
 */

public abstract class DataSource<T> {

    public interface OnDataLoadListener<TR> {
        void onDataLoaded(int nPage, List<TR> items);
        void onDataLoadError(int nPage, AppError reason);
    }

    private final ExecutorService executor;
    private final ConnectivityManager cManager;
    private final int totalDataItems;

    public abstract T buildDataItem(int nPage, int pageSize, int inPageIndex);

    public DataSource(Context ctx, int totalDataItems) {
        this.totalDataItems = totalDataItems;
        executor = Executors.newFixedThreadPool(3);
        cManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public synchronized void release() {
        List<Runnable> leftTasks = executor.shutdownNow();
        if ((leftTasks != null) && (leftTasks.size() > 0)) {
            for (Runnable r : leftTasks) {
                ((WorkItem) r).onReleased();
            }
        }
    }


    public synchronized void loadAsync(int nPage, int pageSize, OnDataLoadListener<T> callback, Looper notifyLooper) {
        executor.submit(new WorkItem(notifyLooper, nPage, pageSize, callback));
    }

    public synchronized boolean isReleased() {
        return executor.isShutdown();
    }




    private class WorkItem extends Handler implements Runnable {
        final int nPage,
                pageSize;
        final OnDataLoadListener<T> callback;

        public WorkItem(Looper looper, int nPage, int pageSize, OnDataLoadListener<T> callback) {
            super(looper);
            this.nPage = nPage;
            this.pageSize = pageSize;
            this.callback = callback;
        }

        @Override
        public void run() {
            NetworkInfo netInfo = cManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                try {
                    Thread.sleep(1400);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                int max_next = Math.min((nPage + 1)*pageSize, totalDataItems);
                int max_prev = nPage*pageSize;

                int thisPageSize = (max_next > max_prev) ? (max_next - max_prev) : 0;

                List<T> pageData = new ArrayList<>(thisPageSize);
                for (int i=0 ; i<thisPageSize; i++) {
                    pageData.add(buildDataItem(nPage, pageSize, i));
                }
                super.obtainMessage(0, nPage, pageSize, pageData).sendToTarget();

            } else {
                try {
                    Thread.sleep(4500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                super.obtainMessage(-1, nPage, pageSize, new SimpleAppError() {
                    @Override
                    public String userMessage() {
                        return "No Internet connection";
                    }
                }).sendToTarget();
            }


        }

        void onReleased() {
            super.obtainMessage(-1, nPage, pageSize, new SimpleAppError() {
                @Override
                public String userMessage() {
                    return "Execution was cancelled";
                }
            }).sendToTarget();
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what >= 0) {
                callback.onDataLoaded(msg.arg1, (List<T>) msg.obj);
            } else {
                callback.onDataLoadError(msg.arg1, (AppError) msg.obj);
            }
        }
    }

}

