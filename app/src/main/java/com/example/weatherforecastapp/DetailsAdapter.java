package com.example.weatherforecastapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DetailsAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Details> details;

    public DetailsAdapter(Context context, ArrayList<Details> details) {
        this.context = context;
        this.details = details;
    }
    @Override
    public int getCount() {
        return details.size();
    }

    @Override
    public Object getItem(int i) {
        return details.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.details_line, null);

        Details deta = details.get(i);

        TextView att = view.findViewById(R.id.attri);
        TextView val = view.findViewById(R.id.value);
        ImageView ico = view.findViewById(R.id.aticon);

        att.setText(deta.attribute);
        val.setText(deta.value);
        Picasso.get().load("https://cdn-icons-png.flaticon.com/" + deta.icon + ".png").into(ico);
        return view;
    }
}
