package com.example.thiro.twipicviewer;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import twitter4j.MediaEntity;
import twitter4j.Status;

/**
 * Created by tomi on 2017/11/16.
 */

public class TweetAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private LinkedList<String> imageUrls = new LinkedList<>();
    protected LinkedList<Long> idList = new LinkedList<>();
    private String mediaUrl;
    private int itemHeight;
    SharedPreferences sharedPref;
    Context context;

    static class ViewHolder {
        long tweetId;
        String tweetText;
        ImageView image;
    }

    public TweetAdapter(Context mcontext, List<twitter4j.Status> statuses) {
        super();
        mInflater = LayoutInflater.from(mcontext);
        context = mcontext;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        itemHeight = Integer.valueOf(sharedPref.getString("grid_height",""));

        for (int i = 0; i < statuses.size(); i++) {
            twitter4j.Status status = statuses.get(i);
            MediaEntity[] mediaEntities = status.getExtendedMediaEntities();
            if (mediaEntities.length > 0) {
                for (int j = 0; j < mediaEntities.length; j++) {
                    mediaUrl = mediaEntities[j].getMediaURLHttps();
                    if (mediaUrl.matches(".*pbs\\.twimg\\.com/media/.*")) {
                        imageUrls.add(mediaUrl);
                        idList.add(status.getId());
                    }
                }

            }
        }
    }

    public void addTimeLine(List<Status> statuses, boolean isFirst) {
        //  ゴ    ミ   コ   ー   ド

        for (int i = 0; i < statuses.size(); i++) {
            twitter4j.Status status = statuses.get(i);
            MediaEntity[] mediaEntities = status.getExtendedMediaEntities();
            if (mediaEntities.length > 0) {
                if (isFirst == true) {
                    // 引っ張って更新した時
                    for (int j = mediaEntities.length - 1; j >=0; j--) {
                        mediaUrl = mediaEntities[j].getMediaURLHttps();
                        if (mediaUrl.matches(".*pbs\\.twimg\\.com/media/.*")) {
                            imageUrls.add(0, mediaUrl);
                            idList.add(0,status.getId());
                        }
                    }
                } else {
                    // 遡ったとき
                    for (int j = 0; j < mediaEntities.length; j++) {
                        mediaUrl = mediaEntities[j].getMediaURLHttps();
                        if (mediaUrl.matches(".*pbs\\.twimg\\.com/media/.*")) {
                            imageUrls.add(mediaUrl);
                            idList.add(status.getId());
                        }
                    }
                }
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