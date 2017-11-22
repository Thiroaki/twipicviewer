package com.example.thiro.twipicviewer;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class DetailActivity extends AppCompatActivity {
    private Twitter mTwitter;
    private String itemUrl;
    private long tweetId;
    private ImageView imageView;
    private ImageView iconImage;
    private TextView tweetText;
    private ToggleButton rtButton;
    private ToggleButton favButton;
    private TextView idText;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        itemUrl = intent.getStringExtra("url");
        tweetId = intent.getLongExtra("id", 0);
        mTwitter = TwitterUtils.getTwitterInstance(this);

        rtButton = (ToggleButton) findViewById(R.id.button);
        favButton = (ToggleButton) findViewById(R.id.button2);
        iconImage = (ImageView) findViewById(R.id.iconImage);
        imageView = (ImageView) findViewById(R.id.imageView);
        tweetText = (TextView) findViewById(R.id.textView);
        idText = (TextView) findViewById(R.id.textView3);

        idText.setText(String.valueOf(tweetId));
        Picasso.with(context).load(itemUrl + ":orig").into(imageView);

        rtButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked == true) {
                    enRetweet();
                } else {
                    deRetweet();
                }
            }
        });
        favButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked == true) {
                    enFav();
                } else {
                    deFav();
                }
            }
        });

        getStatus(tweetId);

    }


    public void enFav() {
        async(0);
    }

    public void deFav() {
        async(1);
    }

    public void enRetweet() {

    }

    public void deRetweet() {

    }

    public void async(final int id) {
        AsyncTask<Void, Void, Void> loadTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (id == 0) {
                        mTwitter.createFavorite(tweetId);
                    } else if (id == 1) {
                        mTwitter.destroyFavorite(tweetId);
                    }
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        loadTask.execute();
    }


    public void getStatus(final long tweetId) {
        AsyncTask<Void, Void, Status> loadTask = new AsyncTask<Void, Void, Status>() {
            @Override
            protected twitter4j.Status doInBackground(Void... params) {
                try {
                    return mTwitter.showStatus(tweetId);
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(twitter4j.Status result) {
                if (result != null) {
                    tweetText.setText(result.getText());
                    Picasso.with(context).load(result.getUser().getProfileImageURLHttps()).into(iconImage);
                } else {
                    showToast("ツイートの取得に失敗しました。。。");
                }
            }
        };
        loadTask.execute();
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

}
