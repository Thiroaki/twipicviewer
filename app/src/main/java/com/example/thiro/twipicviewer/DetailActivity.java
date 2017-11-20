package com.example.thiro.twipicviewer;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class DetailActivity extends AppCompatActivity {
    private Twitter mTwitter;
    private String itemUrl;
    private ImageView imageView;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        itemUrl = intent.getStringExtra("id");

        imageView = (ImageView) findViewById(R.id.imageView) ;
        Picasso.with(context).load(itemUrl).into(imageView);

        //loadTimeLine();

        showToast(itemUrl);

    }

    public void setImage(String url){

    }


    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

}
