package com.alperez.samples.listspagination.testdatasource;

/**
 * instances of this interface will be built anonymously in the PaginatedPresenter on request of the
 * DataSource
 *
 * Created by stanislav.perchenko on 10/19/2018
 */
public interface ListItemModel {
    int nPage();
    int inPageIndex();
    int inListIndex();
}
