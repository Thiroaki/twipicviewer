package com.example.thiro.twipicviewer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import twitter4j.MediaEntity;
import twitter4j.Status;

/**
 * Created by tomi on 2017/11/16.
 */

public class TweetAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private ArrayList<String> imageUrls = new ArrayList<>();
    private ArrayList<Long> idList = new ArrayList<>();
    private String mediaUrl;
    Context context;

    static class ViewHolder {
        long tweetId;
        ImageView image;
    }

    public TweetAdapter(Context mcontext, List<twitter4j.Status> statuses) {
        super();
        mInflater = LayoutInflater.from(mcontext);
        context = mcontext;

        for (int i = 0; i < statuses.size(); i++) {
            twitter4j.Status status = statuses.get(i);
            MediaEntity[] mediaEntities = status.getExtendedMediaEntities();
            if (mediaEntities.length > 0) {
                for (int j = 0; j < mediaEntities.length; j++) {
                    mediaUrl = mediaEntities[j].getMediaURLHttps();
                    if (mediaUrl.matches(".*pbs\\.twimg\\.com/media/.*")) {
                        imageUrls.add(mediaUrl);
                    }
                }
                idList.add(status.getId());
            }
        }
    }

    public void addTimeLine(List<Status> statuses) {
        for (int i = 0; i < statuses.size(); i++) {
            twitter4j.Status status = statuses.get(i);
            MediaEntity[] mediaEntities = status.getExtendedMediaEntities();
            if (mediaEntities.length > 0) {
                for (int j = 0; j < mediaEntities.length; j++) {
                    mediaUrl = mediaEntities[j].getMediaURLHttps();
                    if (mediaUrl.matches(".*pbs\\.twimg\\.com/media/.*")) {
                        imageUrls.add(mediaUrl);
                    }
                }
                idList.add(status.getId());
            }
        }

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item, null);

            holder = new ViewHolder();
            holder.image = (ImageView) convertView.findViewById(R.id.imageView);
            holder.tweetId = idList.get(position);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Picasso.with(context).load(imageUrls.get(position)).into(holder.image);


        return convertView;
    }

    public String getItemUrl(int position) {
        return imageUrls.get(position);
    }

    @Override
    public int getCount() {
        return imageUrls.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return idList.get(position);
    }

}