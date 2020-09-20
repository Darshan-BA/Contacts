package com.ba.contacts.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class SearchAdapter extends ArrayAdapter {
    private List<String> dataList;
    private Context context;
    private int searchResultItemLayout;

    public SearchAdapter(@NonNull Context context, int resource, List<String>dataList) {
        super(context, resource, dataList);
        this.dataList=dataList;
        this.context=context;
        searchResultItemLayout=resource;
    }
    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public String getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {

        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(searchResultItemLayout, parent, false);
        }

        TextView resultItem = (TextView) view.findViewById(android.R.id.text1);
        resultItem.setText(getItem(position));
        return view;
    }
}
