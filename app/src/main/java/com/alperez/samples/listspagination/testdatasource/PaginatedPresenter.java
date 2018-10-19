package com.alperez.samples.listspagination.testdatasource;

import android.content.Context;
import android.os.Looper;

import com.alperez.samples.listspagination.utils.AppError;

import java.util.List;

/**
 * Created by stanislav.perchenko on 4/9/2018.
 */

public class PaginatedPresenter {

    private final int pageSize;
    private final PaginatedListView<ListItemModel> view;
    private final DataSource<ListItemModel> dataSource;



    private boolean isLoading;
    private int currentPageNumber;

    public PaginatedPresenter(Context ctx, int pageSize, int totalDataItems, PaginatedListView<ListItemModel> view) {
        this.pageSize = pageSize;
        this.view = view;
        dataSource = new DataSource<ListItemModel>(ctx, totalDataItems) {
            @Override
            public ListItemModel buildDataItem(final int nPage, final int pageSize, final int inPageIndex) {
                return new ListItemModel() {
                    @Override
                    public int nPage() {
                        return nPage;
                    }

                    @Override
                    public int inPageIndex() {
                        return inPageIndex;
                    }

                    @Override
                    public int inListIndex() {
                        return nPage*pageSize + inPageIndex;
                    }
                };
            }
        };
    }

    public void release() {
        dataSource.release();
    }

    public void loadFirst() {
        if (dataSource.isReleased()) {
            throw new IllegalStateException("Already released");
        } else if (isLoading) {
            throw new IllegalStateException("Loading now");
        }

        isLoading = true;
        dataSource.loadAsync(0, pageSize, loadListener, Looper.getMainLooper());
    }


    public void loadMore() {
        if (dataSource.isReleased())  {
            throw new IllegalStateException("Already released");
        } else if (isLoading) {
            throw new IllegalStateException("Loading now");
        }

        isLoading = true;
        dataSource.loadAsync(currentPageNumber, pageSize, loadListener, Looper.getMainLooper());
    }

    public boolean isLoading() {
        return isLoading;
    }

    /**********************************************************************************************/
    private final DataSource.OnDataLoadListener loadListener = new DataSource.OnDataLoadListener<ListItemModel>() {
        @Override
        public void onDataLoaded(int nPage, List<ListItemModel> items) {
            currentPageNumber = nPage + 1;
            isLoading = false;
            if (nPage == 0) {
                view.onLoadedFirst(items, (items.size() != pageSize));
            } else {
                view.onLoadedMore(items, (items.size() != pageSize));
            }
        }

        @Override
        public void onDataLoadError(int nPage, AppError reason) {
            isLoading = false;
            if (nPage == 0) {
                view.onLoadFirstError(reason);
            } else {
                view.onLoadMoreError(nPage, reason);
            }
        }
    };

}

