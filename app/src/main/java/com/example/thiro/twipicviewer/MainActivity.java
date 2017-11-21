package com.example.thiro.twipicviewer;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import jp.co.recruit_mp.android.headerfootergridview.HeaderFooterGridView;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class MainActivity extends Activity implements ListView.OnItemClickListener {

    private Twitter mTwitter;
    private TweetAdapter mAdapter;
    private HeaderFooterGridView gridView;
    private int pageCount;
    private AsyncTask<Void, Void, List<Status>> addTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pageCount = 1;

        gridView = (HeaderFooterGridView) findViewById(R.id.gridView);
        View footer = LayoutInflater.from(this).inflate(R.layout.grid_header, null, false);
        gridView.addFooterView(footer, null, true);
        gridView.setOnItemClickListener(this);
        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (addTask == null || addTask.getStatus() != AsyncTask.Status.RUNNING) {
                    if (totalItemCount != 0 && totalItemCount == firstVisibleItem + visibleItemCount) {
                        additionalTimeLine();
                    }
                }
            }
        });

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
        AsyncTask<Void, Void, List<Status>> loadTask = new AsyncTask<Void, Void, List<twitter4j.Status>>() {
            @Override
            protected List<twitter4j.Status> doInBackground(Void... params) {
                try {
                    return mTwitter.getHomeTimeline(new Paging(1, 200));
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<twitter4j.Status> result) {
                if (result != null) {
                    mAdapter = new TweetAdapter(getApplicationContext(), result);
                    gridView.setAdapter(mAdapter);
                    showToast("成功");
                    pageCount++;
                } else {
                    showToast("タイムラインの取得に失敗しました。。。");
                }
            }
        };
        loadTask.execute();
    }

    private void additionalTimeLine() {
        addTask = new AsyncTask<Void, Void, List<twitter4j.Status>>() {
            @Override
            protected List<twitter4j.Status> doInBackground(Void... params) {
                try {
                    return mTwitter.getHomeTimeline(new Paging(pageCount, 200));
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<twitter4j.Status> result) {
                if (result != null) {
                    mAdapter.addTimeLine(result);
                    //mAdapter.notifyDataSetChanged();
                    restoreListPosition();
                    gridView.setAdapter(mAdapter);

                    showToast(pageCount + "ページ目を読み込んだ");
                    pageCount++;
                } else {
                    showToast("タイムラインの取得に失敗しました。。。");
                }
            }
        };
        addTask.execute();
    }

    private void restoreListPosition() {
        int position = gridView.getFirstVisiblePosition();
        gridView.setSelection(position);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
        intent.putExtra("url", mAdapter.getItemUrl(position));
        intent.putExtra("id", mAdapter.getItemId(position));
        startActivity(intent);
    }


    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

}