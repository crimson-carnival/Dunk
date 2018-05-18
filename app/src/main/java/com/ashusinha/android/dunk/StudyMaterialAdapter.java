package com.ashusinha.android.dunk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class StudyMaterialAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> title, artist, id;
    private LayoutInflater layoutInflater;

    public StudyMaterialAdapter(Context context, ArrayList<String> title, ArrayList<String> artist, ArrayList<String> id) {
        this.context = context;
        this.title = title;
        this.artist = artist;
        this.id = id;
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    public void updateData(ArrayList<String> title, ArrayList<String> artist, ArrayList<String> id) {
        this.title = title;
        this.artist = artist;
        this.id = id;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return title.size();
    }

    @Override
    public Object getItem(int i) {
        return this;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.list_component_radio, null);
        TextView textView = convertView.findViewById(R.id.title);
        textView.setText(title.get(position));
        textView = convertView.findViewById(R.id.artist);
        textView.setText(artist.get(position));
        return convertView;
    }


}

