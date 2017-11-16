package com.example.thiro.twipicviewer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

/**
 * Created by tomi on 2017/11/16.
 */

public class TweetAdapter extends ArrayAdapter {
    private LayoutInflater mInflater;

    static class ViewHolder {
        int tweetId;
        ImageView image;
    }

    public TweetAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_1);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item, null);

            holder = new ViewHolder();
            holder.image = (ImageView) convertView.findViewById(R.id.imageView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }
}