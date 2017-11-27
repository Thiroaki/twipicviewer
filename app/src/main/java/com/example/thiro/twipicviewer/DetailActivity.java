package com.example.thiro.twipicviewer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        itemUrl = intent.getStringExtra("url");
        tweetId = intent.getLongExtra("id", 0);
        mTwitter = TwitterUtils.getTwitterInstance(this);

        openButton = (Button) findViewById(R.id.openButton);
        rtButton = (ToggleButton) findViewById(R.id.button);
        favButton = (ToggleButton) findViewById(R.id.button2);
        iconImage = (ImageView) findViewById(R.id.iconImage);
        itemImage = (ImageView) findViewById(R.id.imageView);
        tweetText = (TextView) findViewById(R.id.textView);

        Picasso.with(context).load(itemUrl + ":large").into(itemImage);
        //setImage(itemUrl);

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


    public void setImage(final String itemUrl) {
        final DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);


        AsyncTask<Void, Void, Bitmap> task = new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... builder) {
                // 受け取ったbuilderでインターネット通信する
                HttpURLConnection connection = null;
                InputStream inputStream = null;
                Bitmap bitmap = null;

                try {

                    URL url = new URL(itemUrl + ":orig");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();
                    inputStream = connection.getInputStream();

                    bitmap = BitmapFactory.decodeStream(inputStream);
                } catch (MalformedURLException exception) {

                } catch (IOException exception) {

                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                    try {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                    } catch (IOException exception) {
                    }
                }
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                // インターネット通信して取得した画像をImageViewにセットする
                itemImage.setImageBitmap(result);
                float scale = dm.widthPixels / result.getWidth();
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int) (scale * result.getWidth()), (int) (scale * result.getHeight()));
                lp.gravity = Gravity.CENTER;
                itemImage.setLayoutParams(lp);
                Matrix m = itemImage.getImageMatrix();
                m.reset();
                m.postScale(scale, scale);
                itemImage.setImageMatrix(m);
            }
        };
        task.execute();


    }


    public void openApp(View v) {
        String url = "twitter://status?id=" + String.valueOf(tweetId);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

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

    public void async(final int id) {
        AsyncTask<Void, Void, Void> loadTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (id == 0) {
                        mTwitter.createFavorite(tweetId);

                    } else if (id == 1) {
                        mTwitter.destroyFavorite(tweetId);

                    } else if (id == 2) {
                        retweetItem = mTwitter.retweetStatus(tweetId);

                    } else if (id == 3) {
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
                    result.getRetweetedStatus();
                    Picasso.with(context).load(result.getUser().getProfileImageURLHttps()).into(iconImage);
                } else {
                    showToast("ツイートの取得に失敗しました。。。");
                }
            }
        };
        loadTask.execute();
    }

    public void showToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

}
