package com.alperez.pagination.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.alperez.pagination.widget.LoadMoreView;


/**
 * Created by stanislav.perchenko on 6/13/2018.
 */

public abstract class PaginatedRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int VIEW_TYPE_LOAD_MORE = -548796;
    public static final String LOG_TAG = "MY_RECYCLER_ADAPTER";

    public interface OnLoadMoreListener {
        void onLoadMore(PaginatedRecyclerAdapter adapter);
    }

    private final LayoutInflater inflater;
    private final int loadMoreResource;
    private OnLoadMoreListener loadMoreListener;

    private boolean broadcastingLoadMore;
    private int mLoadMoreState = LoadMoreView.LOAD_MORE_STATE_HIDE;
    private RecyclerView vHostRecyclerView;


    /**************************  Abstract methods  ************************************************/
    public abstract int getDataItemCount();
    public abstract long onGetDataItemId(int position);
    public abstract int onGetDataItemType(int position);
    public abstract RecyclerView.ViewHolder buildDataItemViewHolder(ViewGroup parent, int viewType);
    public abstract void onBindDataItemViewHolder(RecyclerView.ViewHolder holder, int position);
    /**********************************************************************************************/



    public PaginatedRecyclerAdapter(Context ctx, int loadMoreResource) {
        inflater = LayoutInflater.from(ctx);
        this.loadMoreResource = loadMoreResource;
    }

    protected LayoutInflater getInflater() {
        return inflater;
    }


    public void setLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }


    /**
     * Updates 'Load More' footer presentation
     * @param loadMoreState must be one of the LOAD_MORE_STATE_* values
     */
    public void setLoadMoreState(int loadMoreState) {
        Log.d(LOG_TAG, String.format("setLoadMoreState(): set Load More state from %d to %d", mLoadMoreState, loadMoreState ));

        if (mLoadMoreState != loadMoreState) {

            mLoadMoreState = loadMoreState;
            notifyDataSetChanged();
        }
    }

    public void postLoadMoreState(int loadMoreState) {
        Log.d(LOG_TAG, "Post LoadMore state - "+loadMoreState);
        if (vHostRecyclerView != null) {
            vHostRecyclerView.post(() -> setLoadMoreState(loadMoreState));
        } else {
            throw new IllegalStateException("This adapter is not attached to a RecyclerView");
        }
    }

    @Override
    public final void onAttachedToRecyclerView(RecyclerView rv) {
        vHostRecyclerView = rv;
    }

    @Override
    public final void onDetachedFromRecyclerView(RecyclerView rv) {
        if (rv == vHostRecyclerView) {
            vHostRecyclerView = null;
        }
    }


    @Override
    public int getItemCount() {
        return getDataItemCount() + 1;
    }


    @Override
    public long getItemId(int position) {
        return (position < getDataItemCount()) ? onGetDataItemId(position) : 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < getDataItemCount()) {
            int type = onGetDataItemType(position);
            if (type == VIEW_TYPE_LOAD_MORE) {
                throw new RuntimeException(String.format("The Item Type value of %d is reserved for the 'L:oad More' View"));
            }
            return type;
        } else {
            return VIEW_TYPE_LOAD_MORE;
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_LOAD_MORE) {
            VH_LoadMore vh_lm = new VH_LoadMore((LoadMoreView) inflater.inflate(loadMoreResource, parent, false));
            if (mLoadMoreState >= 0) vh_lm.vLoadMore.setState(mLoadMoreState);
            return vh_lm;
        } else {
            return buildDataItemViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof VH_LoadMore) {

            Log.e(LOG_TAG, String.format("onBindViewHolder(): change Load More state from %d to %d", ((VH_LoadMore) holder).vLoadMore.getState(), mLoadMoreState ));

            ((VH_LoadMore) holder).vLoadMore.setState(mLoadMoreState);

            if (!broadcastingLoadMore && (loadMoreListener != null) && (mLoadMoreState == LoadMoreView.LOAD_MORE_STATE_READY)) {
                broadcastingLoadMore = true;
                loadMoreListener.onLoadMore(this);
                broadcastingLoadMore = false;
            }
        } else {
            onBindDataItemViewHolder(holder, position);
        }
    }



    /**********************************************************************************************/
    class VH_LoadMore extends RecyclerView.ViewHolder {
        private final LoadMoreView vLoadMore;
        public VH_LoadMore(LoadMoreView base) {
            super(base);
            vLoadMore = base;
            vLoadMore.setReloadClickListener(v -> {
                if (loadMoreListener != null) loadMoreListener.onLoadMore(PaginatedRecyclerAdapter.this);
            });
        }
    }
}

