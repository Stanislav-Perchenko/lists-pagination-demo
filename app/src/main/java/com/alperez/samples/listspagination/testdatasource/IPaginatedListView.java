package com.alperez.samples.listspagination.testdatasource;

import com.alperez.samples.listspagination.utils.AppError;

import java.util.List;

/**
 * Created by stanislav.perchenko on 4/9/2018.
 */

public interface IPaginatedListView<T> {
    void onLoadedFirst(List<T> data, boolean isFinished);
    void onLoadedMore(List<T> data, boolean isFinished);
    void onLoadFirstError(AppError reason);
    void onLoadMoreError(int nPage, AppError reason);
}
