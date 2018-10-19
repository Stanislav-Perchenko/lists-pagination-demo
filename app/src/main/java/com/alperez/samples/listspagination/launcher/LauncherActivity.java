package com.alperez.samples.listspagination.launcher;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.alperez.samples.listspagination.R;
import com.alperez.samples.listspagination.databinding.ActivityLauncherBinding;
import com.alperez.samples.listspagination.utils.CommErrorEmulator;
import com.alperez.utils.UniformVerticalRecyclerItemSpace;


import java.util.Collection;

/**
 * Created by stanislav.perchenko on 10/15/2018
 */
public class LauncherActivity extends AppCompatActivity implements LauncherScreenView {

    private ActivityLauncherBinding binding;

    private LauncherActivityPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_launcher);
        binding.setSwitchListener(CommErrorEmulator.getInstance());

        binding.recycler.setLayoutManager(new LinearLayoutManager(this));
        binding.recycler.addItemDecoration(new UniformVerticalRecyclerItemSpace(getResources().getDimensionPixelSize(R.dimen.list_card_item_space)));
        binding.recycler.setOverScrollMode(View.OVER_SCROLL_NEVER);

        presenter = new LauncherActivityPresenter(this, getResources());
        presenter.initializeView();
        binding.setIsLoading(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.release();
    }

    @Override
    public void onLauncherItems(Collection<LauncherScreenItem> launcherItems) {
        binding.setIsLoading(false);
        binding.setError(null);
        if (binding.getAdapter() == null) {
            binding.setAdapter(new LauncherAdapter(this, launcherItems) {
                @Override
                public void onItemClicked(int position, LauncherScreenItem item) {
                    startActivity(new Intent(LauncherActivity.this, item.activityClass()));
                }
            });
        } else {
            ((LauncherAdapter) binding.getAdapter()).setData(launcherItems);
        }
    }

    @Override
    public void onLoadItemsError(Throwable e) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        binding.setIsLoading(false);
        binding.setError(e);
    }
}
