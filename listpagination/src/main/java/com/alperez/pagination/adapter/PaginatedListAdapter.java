package com.alperez.pagination.adapter;

import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.WrapperListAdapter;

import androidx.annotation.NonNull;

import com.alperez.pagination.widget.LoadMoreView;

/**
 * Created by stanislav.perchenko on 4/9/2018.
 */

public class PaginatedListAdapter implements WrapperListAdapter, Filterable {


    public static final int VIEW_TYPE_LOAD_MORE = -548796;

    public interface OnLoadMoreListener {
        void onLoadMore(PaginatedListAdapter adapter);
    }

    private final BaseAdapter wrappedAdapter;
    private final boolean mIsFilterable;
    private OnLoadMoreListener loadMoreListener;
    private int loadMoreResource = -1;


    public PaginatedListAdapter(@NonNull BaseAdapter wrappedAdapter) {
        this.wrappedAdapter = wrappedAdapter;
        mIsFilterable = wrappedAdapter instanceof Filterable;
    }

    public void setLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

    public void setLoadMoreResource(int loadMoreResource) {
        this.loadMoreResource = loadMoreResource;
    }

    public BaseAdapter getWrappedAdapter() {
        return wrappedAdapter;
    }


    /**
     * Updates 'Load More' footer presentation
     * @param loadMoreState must be one of the LOAD_MORE_STATE_* values
     */
    public void setLoadMoreState(int loadMoreState) {

        if (vLoadMore == null) {
            pendingLoadMoreState = loadMoreState;
        } else {
            vLoadMore.setState(loadMoreState);
        }
    }


    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        wrappedAdapter.registerDataSetObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        wrappedAdapter.unregisterDataSetObserver(observer);
    }

    @Override
    public boolean hasStableIds() {
        return wrappedAdapter.hasStableIds();
    }

    @Override
    public boolean areAllItemsEnabled() {
        return wrappedAdapter.areAllItemsEnabled();
    }

    @Override
    public boolean isEnabled(int position) {
        if (position < wrappedAdapter.getCount()) {
            return wrappedAdapter.isEnabled(position);
        } else {
            return true;
        }
    }


    @Override
    public boolean isEmpty() {
        return getCount() > 0;
    }

    @Override
    public int getCount() {
        int wrCnt = wrappedAdapter.getCount();
        return (wrCnt == 0) ? 0 : wrCnt + 1;
    }

    public int getDataItemCount() {
        return wrappedAdapter.getCount();
    }

    @Override
    public Object getItem(int position) {
        return (position < wrappedAdapter.getCount()) ? wrappedAdapter.getItem(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return (position < wrappedAdapter.getCount()) ? wrappedAdapter.getItemId(position) : -1;
    }

    private boolean broadcastingLoadMore;
    private LoadMoreView vLoadMore;
    private int pendingLoadMoreState = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position < wrappedAdapter.getCount()) {
            return wrappedAdapter.getView(position, convertView, parent);
        }

        if (vLoadMore == null) {
            if (loadMoreResource <= 0) throw new IllegalStateException("Load More XML resource is not set");
            vLoadMore = (LoadMoreView) LayoutInflater.from(parent.getContext()).inflate(loadMoreResource, parent, false);
            vLoadMore.setReloadClickListener(v -> {
                if (loadMoreListener != null) loadMoreListener.onLoadMore(this);
            });
            if (pendingLoadMoreState >= 0) vLoadMore.setState(pendingLoadMoreState);
        }

        if (!broadcastingLoadMore && (loadMoreListener != null) && (vLoadMore.getState() == LoadMoreView.LOAD_MORE_STATE_READY)) {
            broadcastingLoadMore = true;
            loadMoreListener.onLoadMore(this);
            broadcastingLoadMore = false;
        }
        return vLoadMore;
    }

    @Override
    public int getItemViewType(int position) {
        return (position < wrappedAdapter.getCount()) ? wrappedAdapter.getItemViewType(position) : VIEW_TYPE_LOAD_MORE;
    }

    @Override
    public int getViewTypeCount() {
        return wrappedAdapter.getViewTypeCount();
    }

    public Filter getFilter() {
        if (mIsFilterable) {
            return ((Filterable) wrappedAdapter).getFilter();
        }
        return null;
    }
}

