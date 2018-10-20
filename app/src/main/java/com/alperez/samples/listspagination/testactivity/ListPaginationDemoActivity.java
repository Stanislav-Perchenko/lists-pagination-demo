package com.alperez.samples.listspagination.testactivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ListView;

import com.alperez.pagination.adapter.PaginatedListAdapter;
import com.alperez.pagination.widget.LoadMoreView;
import com.alperez.samples.listspagination.GlobalConstants;
import com.alperez.samples.listspagination.R;
import com.alperez.samples.listspagination.testadapter.MyListInnerAdapter;
import com.alperez.samples.listspagination.testdatasource.ListItemModel;
import com.alperez.samples.listspagination.testdatasource.IPaginatedListView;
import com.alperez.samples.listspagination.testdatasource.PaginatedPresenter;
import com.alperez.samples.listspagination.utils.AppError;

import java.util.List;


/**
 * Created by stanislav.perchenko on 4/9/2018.
 */

public class ListPaginationDemoActivity extends BaseDemoActivity implements IPaginatedListView<ListItemModel> {
    private SwipeRefreshLayout vRefresher;
    private ListView vList;
    private View vNoData;
    private LoadMoreView vOnScreenReload;


    private PaginatedPresenter mPresenter;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_list_pagination_demo;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        (vRefresher = (SwipeRefreshLayout) findViewById(R.id.refresher)).setOnRefreshListener(() -> {
            if (!mPresenter.isLoading()) {
                vOnScreenReload.setVisibility(View.GONE);
                vNoData.setVisibility(View.GONE);
                mPresenter.loadFirst();
            } else {
                vRefresher.setRefreshing(false);
            }
        });
        vList = (ListView) findViewById(android.R.id.list);
        vNoData = findViewById(R.id.no_data);
        (vOnScreenReload = (LoadMoreView) findViewById(R.id.tap_to_refresh)).setReloadClickListener(v -> {
            if (!mPresenter.isLoading()) startLoadOnCreation();
        });

        mPresenter = new PaginatedPresenter(this, GlobalConstants.PAGE_SIZE, 9 * GlobalConstants.PAGE_SIZE - 1, this);

        startLoadOnCreation();
    }

    private void startLoadOnCreation() {
        vRefresher.setRefreshing(false);
        vRefresher.setEnabled(false);
        vOnScreenReload.setVisibility(View.VISIBLE);
        vOnScreenReload.setState(LoadMoreView.LOAD_MORE_STATE_LOADING);
        vNoData.setVisibility(View.GONE);
        mPresenter.loadFirst();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.release();
    }



    /**********************************************************************************************/
    @Override
    public void onLoadedFirst(List<ListItemModel> data, boolean isFinished) {
        vRefresher.setEnabled(true);
        vRefresher.setRefreshing(false);
        vOnScreenReload.setVisibility(View.GONE);
        vNoData.setVisibility((data.size()  > 0) ? View.GONE : View.VISIBLE);


        PaginatedListAdapter ad = new PaginatedListAdapter(new MyListInnerAdapter(this, data));
        ad.setLoadMoreResource(R.layout.view_load_more);
        ad.setLoadMoreState(isFinished ? LoadMoreView.LOAD_MORE_STATE_END_FEED : LoadMoreView.LOAD_MORE_STATE_READY);
        ad.setLoadMoreListener((adapter) -> {
            if (!mPresenter.isLoading()) { // Need to check loading if swipe refreshing is in progress and user scrolls list down to load more
                adapter.setLoadMoreState(LoadMoreView.LOAD_MORE_STATE_LOADING);
                mPresenter.loadMore();
            }
        });
        vList.setAdapter(ad);
    }

    @Override
    public void onLoadedMore(List<ListItemModel> data, boolean isFinished) {
        PaginatedListAdapter ad = (PaginatedListAdapter) vList.getAdapter();
        ((MyListInnerAdapter) ad.getWrappedAdapter()).addAll(data);
        ad.setLoadMoreState(isFinished ? LoadMoreView.LOAD_MORE_STATE_END_FEED : LoadMoreView.LOAD_MORE_STATE_READY);
    }

    @Override
    public void onLoadFirstError(AppError reason) {
        vRefresher.setEnabled(true);
        vRefresher.setRefreshing(false);
        vNoData.setVisibility(View.GONE);

        boolean hasData = ((vList.getAdapter() != null) && ((PaginatedListAdapter) vList.getAdapter()).getDataItemCount() > 0);
        if (hasData) {
            vOnScreenReload.setVisibility(View.GONE);
            ((PaginatedListAdapter) vList.getAdapter()).setLoadMoreState(LoadMoreView.LOAD_MORE_STATE_READY);
        } else {
            vOnScreenReload.setVisibility(View.VISIBLE);
            vOnScreenReload.setState(LoadMoreView.LOAD_MORE_STATE_TAP_RELOAD);
        }
    }

    @Override
    public void onLoadMoreError(int nPage, AppError reason) {
        vRefresher.setEnabled(true);
        vRefresher.setRefreshing(false);
        vNoData.setVisibility(View.GONE);

        //----  Activate Tap-to-Reload in the list  ----
        vOnScreenReload.setVisibility(View.GONE);
        ((PaginatedListAdapter) vList.getAdapter()).setLoadMoreState(LoadMoreView.LOAD_MORE_STATE_TAP_RELOAD);
    }
}
