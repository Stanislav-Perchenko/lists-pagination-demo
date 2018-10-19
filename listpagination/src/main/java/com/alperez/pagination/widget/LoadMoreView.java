package com.alperez.pagination.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.alperez.pagination.R;

/**
 * Created by stanislav.perchenko on 4/13/2018.
 */

public class LoadMoreView extends FrameLayout {

    public static final int LOAD_MORE_STATE_READY = 0;
    public static final int LOAD_MORE_STATE_LOADING = 1;
    public static final int LOAD_MORE_STATE_TAP_RELOAD = 2;
    public static final int LOAD_MORE_STATE_END_FEED = 3;
    public static final int LOAD_MORE_STATE_HIDE = 4;

    public LoadMoreView(Context context) {
        super(context);
        throw new RuntimeException("This View must be instantiated from XML with proper custom attributes");
    }

    public LoadMoreView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0, 0);
    }

    public LoadMoreView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LoadMoreView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr, defStyleRes);
    }

    private int mState = -1;
    private View vProgress,
            vReload,
            vEndOfFeed;

    private int progressResId, reloadResId, eofResId;
    private OnClickListener reloadClickListener;

    private boolean isInflated;
    private int pendingInitialState = -1;

    public int getState() {
        return mState;
    }

    public void setState(int newState) {
        if (!isInflated) {
            pendingInitialState = newState;
            return;
        }
        if (this.mState != newState) {
            this.mState = newState;
            switch (newState) {
                case LOAD_MORE_STATE_READY:
                case LOAD_MORE_STATE_HIDE:
                    if (vProgress.getVisibility() == View.VISIBLE) vProgress.setVisibility(View.INVISIBLE);
                    if (vReload.getVisibility() == View.VISIBLE) vReload.setVisibility(View.INVISIBLE);
                    if (vEndOfFeed.getVisibility() == View.VISIBLE) vEndOfFeed.setVisibility(View.INVISIBLE);
                    return;
                case LOAD_MORE_STATE_END_FEED:
                    if (vProgress.getVisibility() == View.VISIBLE) vProgress.setVisibility(View.INVISIBLE);
                    if (vReload.getVisibility() == View.VISIBLE) vReload.setVisibility(View.INVISIBLE);
                    if (vEndOfFeed.getVisibility() != View.VISIBLE) vEndOfFeed.setVisibility(View.VISIBLE);
                    return;
                case LOAD_MORE_STATE_LOADING:
                    if (vProgress.getVisibility() != View.VISIBLE) vProgress.setVisibility(View.VISIBLE);
                    if (vReload.getVisibility() == View.VISIBLE) vReload.setVisibility(View.INVISIBLE);
                    if (vEndOfFeed.getVisibility() == View.VISIBLE) vEndOfFeed.setVisibility(View.INVISIBLE);
                    return;
                case LOAD_MORE_STATE_TAP_RELOAD:
                    if (vProgress.getVisibility() == View.VISIBLE) vProgress.setVisibility(View.INVISIBLE);
                    if (vReload.getVisibility() != View.VISIBLE) vReload.setVisibility(View.VISIBLE);
                    if (vEndOfFeed.getVisibility() == View.VISIBLE) vEndOfFeed.setVisibility(View.INVISIBLE);
                    return;
                default:
                    throw new IllegalArgumentException("Wrong LoadMoreView state - "+newState);
            }
        }
    }

    public void setReloadClickListener(OnClickListener reloadClickListener) {
        this.reloadClickListener = reloadClickListener;
    }




    private void init(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.LoadMoreView, defStyleAttr, defStyleRes);
        try {
            progressResId = a.getResourceId(R.styleable.LoadMoreView_progressResId, -1);
            reloadResId = a.getResourceId(R.styleable.LoadMoreView_endmsgResId, -1);
            eofResId = a.getResourceId(R.styleable.LoadMoreView_reloadResId, -1);
        } finally {
            a.recycle();
        }
        pendingInitialState = LOAD_MORE_STATE_READY;
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        isInflated = true;
        vProgress = findViewById(progressResId);
        if (vProgress == null) throw new IllegalStateException("No Progress view found");

        vEndOfFeed = findViewById(reloadResId);
        if (vEndOfFeed == null) throw new IllegalStateException("No End Message view found");

        vReload = findViewById(eofResId);
        if (vReload == null) throw new IllegalStateException("No Reload view found");
        vReload.setOnClickListener(v -> {
            if (mState == LOAD_MORE_STATE_TAP_RELOAD && reloadClickListener != null) reloadClickListener.onClick(v);
        });

        if (pendingInitialState >= 0) {
            setState(pendingInitialState);
        }
    }
}

