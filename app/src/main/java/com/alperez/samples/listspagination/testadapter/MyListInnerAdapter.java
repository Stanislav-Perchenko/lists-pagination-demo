package com.alperez.samples.listspagination.testadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alperez.samples.listspagination.R;
import com.alperez.samples.listspagination.testdatasource.ListItemModel;

import java.util.List;

/**
 * Created by stanislav.perchenko on 4/9/2018.
 */

public class MyListInnerAdapter extends ArrayAdapter<ListItemModel> {
    private final LayoutInflater inflater;

    public MyListInnerAdapter(@NonNull Context context, @NonNull List<ListItemModel> objects) {
        super(context, R.layout.item_list, objects);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public long getItemId(int position) {
        return super.getItem(position).inListIndex() + 1;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        VH holder;
        if (row == null) {
            holder = new VH(row = inflater.inflate(R.layout.item_list, parent, false));
            row.setTag(holder);
        } else {
            holder = (VH) row.getTag();
        }

        ListItemModel item = getItem(position);
        holder.vText1.setText("Total list index: "+item.inListIndex());
        holder.vText2.setText("N page = "+item.nPage());
        holder.vText3.setText("In page index = "+item.inPageIndex());
        return row;
    }

    private class VH {
        final TextView vText1;
        final TextView vText2;
        final TextView vText3;

        public VH (View base) {
            vText1 = (TextView) base.findViewById(R.id.text1);
            vText2 = (TextView) base.findViewById(R.id.text2);
            vText3 = (TextView) base.findViewById(R.id.text3);
        }
    }
}