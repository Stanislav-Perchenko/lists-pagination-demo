package com.alperez.samples.listspagination.testactivity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alperez.pagination.widget.LoadMoreView;
import com.alperez.samples.listspagination.GlobalConstants;
import com.alperez.samples.listspagination.R;
import com.alperez.samples.listspagination.testadapter.MyPaginatedRecyclerAdapter;
import com.alperez.samples.listspagination.testdatasource.IPaginatedListView;
import com.alperez.samples.listspagination.testdatasource.ListItemModel;
import com.alperez.samples.listspagination.testdatasource.PaginatedPresenter;
import com.alperez.samples.listspagination.utils.AppError;
import com.alperez.utils.UniformVerticalRecyclerItemSpace;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stanislav.perchenko on 6/13/2018.
 */
public class RecyclerPaginationDemoActivity extends BaseDemoActivity implements IPaginatedListView<ListItemModel> {
    private SwipeRefreshLayout vRefresher;
    private View vNoData;
    private LoadMoreView vOnScreenReload;

    private MyPaginatedRecyclerAdapter mAdapter;

    private PaginatedPresenter mPresenter;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_recycler_pagination_demo;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RecyclerView vRecycler = (RecyclerView) findViewById(R.id.recycler);
        vRecycler.setLayoutManager(new LinearLayoutManager(this));
        vRecycler.addItemDecoration(new UniformVerticalRecyclerItemSpace(getResources().getDimensionPixelSize(R.dimen.list_card_item_space)));
        vRecycler.setAdapter(mAdapter = new MyPaginatedRecyclerAdapter(this, R.layout.view_load_more, new ArrayList<>()));

        mAdapter.setLoadMoreListener((adapter) -> {
            if (!mPresenter.isLoading()) { // Need to check loading if swipe refreshing is in progress and user scrolls list down to load more
                adapter.postLoadMoreState(LoadMoreView.LOAD_MORE_STATE_LOADING);
                mPresenter.loadMore();
            }
        });

        vRefresher = (SwipeRefreshLayout) findViewById(R.id.refresher);
        vRefresher.setOnRefreshListener(() -> {
            if (!mPresenter.isLoading()) {
                vOnScreenReload.setVisibility(View.GONE);
                vNoData.setVisibility(View.GONE);
                mPresenter.loadFirst();
            } else {
                vRefresher.setRefreshing(false);
            }
        });

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
    /*************************  IPaginatedListView implementation  *********************************/
    /**********************************************************************************************/

    @Override
    public void onLoadedFirst(List<ListItemModel> data, boolean isFinished) {
        vRefresher.setEnabled(true);
        vRefresher.setRefreshing(false);
        vOnScreenReload.setVisibility(View.GONE);
        vNoData.setVisibility((data.size()  > 0) ? View.GONE : View.VISIBLE);

        mAdapter.setData(data);
        mAdapter.setLoadMoreState(isFinished ? LoadMoreView.LOAD_MORE_STATE_END_FEED : LoadMoreView.LOAD_MORE_STATE_READY);
    }

    @Override
    public void onLoadFirstError(AppError reason) {
        vRefresher.setEnabled(true);
        vRefresher.setRefreshing(false);
        vNoData.setVisibility(View.GONE);

        if (mAdapter.getDataItemCount() > 0) {
            vOnScreenReload.setVisibility(View.GONE);
            mAdapter.setLoadMoreState(LoadMoreView.LOAD_MORE_STATE_READY);
        } else {
            vOnScreenReload.setVisibility(View.VISIBLE);
            vOnScreenReload.setState(LoadMoreView.LOAD_MORE_STATE_TAP_RELOAD);
        }
    }

    @Override
    public void onLoadedMore(List<ListItemModel> data, boolean isFinished) {
        mAdapter.addAll(data);
        mAdapter.setLoadMoreState(isFinished ? LoadMoreView.LOAD_MORE_STATE_END_FEED : LoadMoreView.LOAD_MORE_STATE_READY);
    }



    @Override
    public void onLoadMoreError(int nPage, AppError reason) {
        vRefresher.setEnabled(true);
        vRefresher.setRefreshing(false);
        vNoData.setVisibility(View.GONE);

        //----  Activate Tap-to-Reload in the list  ----
        vOnScreenReload.setVisibility(View.GONE);
        mAdapter.setLoadMoreState(LoadMoreView.LOAD_MORE_STATE_TAP_RELOAD);
    }
}
