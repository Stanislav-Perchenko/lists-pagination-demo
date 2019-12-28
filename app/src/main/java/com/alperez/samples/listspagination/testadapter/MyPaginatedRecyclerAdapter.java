package com.alperez.samples.listspagination.testadapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alperez.pagination.adapter.PaginatedRecyclerAdapter;
import com.alperez.samples.listspagination.R;
import com.alperez.samples.listspagination.testdatasource.ListItemModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by stanislav.perchenko on 6/13/2018.
 */

public class MyPaginatedRecyclerAdapter extends PaginatedRecyclerAdapter {


    private final List<ListItemModel> data = new ArrayList<>();

    public MyPaginatedRecyclerAdapter(Context ctx, int loadMoreResource, @NonNull Collection<ListItemModel> data) {
        super(ctx, loadMoreResource);
        this.data.addAll(data);
    }

    public void setData(@NonNull Collection<ListItemModel> data) {
        if (this.data.size() > 0) {
            this.data.clear();
        }
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public void addAll(@NonNull Collection<ListItemModel> data) {
        if (data.size() > 0) {
            this.data.addAll(data);
            notifyDataSetChanged();
        }
    }

    public void clear() {
        if (data.size() > 0) {
            data.clear();
            notifyDataSetChanged();
        }
    }


    @Override
    public int getDataItemCount() {
        return data.size();
    }

    @Override
    public long onGetDataItemId(int position) {
        return data.get(position).inListIndex() + 1;
    }

    @Override
    public int onGetDataItemType(int position) {
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder buildDataItemViewHolder(ViewGroup parent, int viewType) {
        return new VH(getInflater().inflate(R.layout.item_recycler, parent, false));
    }

    @Override
    public void onBindDataItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof VH) {
            ((VH) holder).bindDataItem(data.get(position));
        } else {
            throw new RuntimeException("Unknown ViewHolder implementation - "+holder.getClass().getName());
        }
    }



    /**********************************************************************************************/
    class VH extends RecyclerView.ViewHolder {

        final TextView vText1;
        final TextView vText2;
        final TextView vText3;

        public VH(View base) {
            super(base);
            vText1 = (TextView) base.findViewById(R.id.text1);
            vText2 = (TextView) base.findViewById(R.id.text2);
            vText3 = (TextView) base.findViewById(R.id.text3);
        }

        public void bindDataItem(ListItemModel item) {
            vText1.setText("Total list index: "+item.inListIndex());
            vText2.setText("N page = "+item.nPage());
            vText3.setText("In page index = "+item.inPageIndex());
        }
    }
}
