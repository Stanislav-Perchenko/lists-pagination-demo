package com.alperez.samples.listspagination.launcher;

import java.util.Collection;

/**
 * Created by stanislav.perchenko on 10/15/2018
 */
public interface LauncherScreenView {

    void onLauncherItems(Collection<LauncherScreenItem> launcherItems);
    void onLoadItemsError(Throwable e);
}
