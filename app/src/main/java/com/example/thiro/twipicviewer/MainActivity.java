package com.example.thiro.twipicviewer;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class MainActivity extends Activity implements ListView.OnItemClickListener {

    private Twitter mTwitter;
    private TweetAdapter mAdapter;
    private ListView listView;
    private Paging paging = new Paging(1,200);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(this);

        if (!TwitterUtils.hasAccessToken(this)) {
            Intent intent = new Intent(this, OAuthActivity.class);
            startActivity(intent);
            finish();
        } else {
            //mAdapter = new TweetAdapter(this);
            mTwitter = TwitterUtils.getTwitterInstance(this);
            loadTimeLine();
        }

    }


    private void loadTimeLine() {
        AsyncTask<Void, Void, List<Status>> task = new AsyncTask<Void, Void, List<twitter4j.Status>>() {
            @Override
            protected List<twitter4j.Status> doInBackground(Void... params) {
                try {
                    return mTwitter.getHomeTimeline(paging);
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<twitter4j.Status> result) {
                if (result != null) {
                    mAdapter = new TweetAdapter(getApplicationContext(),result);
                    listView.setAdapter(mAdapter);
                    showToast("成功");
                } else {
                    showToast("タイムラインの取得に失敗しました。。。");
                }
            }
        };
        task.execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id){
        Intent intent = new Intent(getApplicationContext(),DetailActivity.class);
        long itemId = mAdapter.getItemId(position);
        intent.putExtra("id",itemId);
        startActivity(intent);
    }


    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

}