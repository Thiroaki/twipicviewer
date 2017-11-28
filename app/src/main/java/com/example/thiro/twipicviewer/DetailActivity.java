package com.example.thiro.twipicviewer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class DetailActivity extends AppCompatActivity {
    private Twitter mTwitter;
    private String itemUrl;
    private long tweetId;
    private Status retweetItem;
    private ImageView itemImage;
    private ImageView iconImage;
    private TextView tweetText;
    Button openButton;
    private ToggleButton rtButton;
    private ToggleButton favButton;
    private Context context;
    private boolean favFlag;
    private boolean rtFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mTwitter = TwitterUtils.getTwitterInstance(this);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        // インテントで画像URLとツイートIDをもらう
        Intent intent = getIntent();
        itemUrl = intent.getStringExtra("url");
        tweetId = intent.getLongExtra("id", 0);

        openButton = (Button) findViewById(R.id.openButton);
        rtButton = (ToggleButton) findViewById(R.id.button);
        favButton = (ToggleButton) findViewById(R.id.button2);
        iconImage = (ImageView) findViewById(R.id.iconImage);
        tweetText = (TextView) findViewById(R.id.textView);
        itemImage = (ImageView) findViewById(R.id.imageView);


        // 画像リソースをセット
        int width = getDisplayWidth();
        int height = getDisplayHeight();
        Picasso.with(context).load(itemUrl + ":large").resize(width, height).centerInside().into(itemImage);


        // ボタンの状態セット
        if (isFavorited() == true){
            favButton.setChecked(true);
        }
        if (isRetweeted() == true){
            rtButton.setChecked(true);
        }

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


        // ツイ内容とアイコンセット
        getStatus(tweetId);

    }


    public boolean isFavorited() {
        AsyncTask<Void, Void, Void> getTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    favFlag = mTwitter.showStatus(tweetId).isFavorited();
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        getTask.execute();
        return favFlag;
    }

    public boolean isRetweeted() {
        AsyncTask<Void, Void, Void> getTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    rtFlag = mTwitter.showStatus(tweetId).isRetweeted();
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        getTask.execute();
        return rtFlag;
    }


    // Twitterで開く
    public void openApp(View v) {
        String url = "twitter://status?id=" + String.valueOf(tweetId);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    // いいねとRT
    public void enFav() {
        async(0);
        showToast("ふぁぼった");
    }

    public void deFav() {
        async(1);
        showToast("あんふぁぼ");
    }

    public void enRetweet() {
        async(2);
        showToast("RTした");
    }

    public void deRetweet() {
        async(3);
        showToast("RT消した");
    }

    // めんどいからまとめる
    public void async(final int id) {
        AsyncTask<Void, Void, Void> loadTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (id == 0) {
                        // いいねする
                        mTwitter.createFavorite(tweetId);

                    } else if (id == 1) {
                        // いいね外す
                        mTwitter.destroyFavorite(tweetId);

                    } else if (id == 2) {
                        // RTする
                        retweetItem = mTwitter.retweetStatus(tweetId);

                    } else if (id == 3) {
                        // RT解除
                        // すでにRTしてた場合ぬるぽ
                        mTwitter.destroyStatus(retweetItem.getId());

                    }
                } catch (TwitterException e) {
                    e.printStackTrace();
                    //showToast("失敗");
                }
                return null;
            }
        };
        loadTask.execute();
    }

    public void getRetweets(final long tweetId){
        AsyncTask<Void, Void, ResponseList> loadTask = new AsyncTask<Void, Void, ResponseList>() {
            @Override
            protected ResponseList doInBackground(Void... params) {
                try {
                    mTwitter.getUserTimeline(new Paging(tweetId));

                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(ResponseList result) {
                if (result != null) {
                    Object obj = result.get(1);
                    tweetText.setText(obj.toString());
                }
            }
        };
        loadTask.execute();
    }

    // ツイ詳細
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
                    // ツイ内容表示
                    tweetText.setText(result.getText());

                    // アイコン画像セット
                    if (result.isRetweet() == true) {
                        Picasso.with(context).load(result.getRetweetedStatus().getUser().getProfileImageURLHttps()).into(iconImage);
                    }else{
                        Picasso.with(context).load(result.getUser().getProfileImageURLHttps()).into(iconImage);
                    }
                } else {
                    showToast("ツイートの取得に失敗しました。。。");
                }
            }
        };
        loadTask.execute();
    }

    public int getDisplayWidth() {
        return getWindowManager().getDefaultDisplay().getWidth();
    }

    public int getDisplayHeight() {
        return getWindowManager().getDefaultDisplay().getHeight();
    }

    public void showToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

}
