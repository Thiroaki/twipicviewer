package com.example.thiro.twipicviewer;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

public class MainActivity extends Activity implements ListView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private Twitter mTwitter;
    private TweetAdapter mAdapter;
    private HeaderFooterGridView gridView;
    private int pageCount = 2;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AsyncTask<Void, Void, List<Status>> addTask;
    protected long firstTweetId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("tpv");
        toolbar.inflateMenu(R.menu.main_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.setting) {
                    showToast("setting");
                    return true;
                }
                return false;
            }
        });*/


        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        gridView = findViewById(R.id.gridView);
        View footer = LayoutInflater.from(this).inflate(R.layout.grid_footer, null, false);
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
                        additionalTimeLine(false);
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
                    restoreFirstId();
                    showToast("成功");
                } else {
                    showToast("タイムラインの取得に失敗しました。。。");
                }
            }
        };
        loadTask.execute();
    }

    private void additionalTimeLine(final boolean isFirst) {
        addTask = new AsyncTask<Void, Void, List<twitter4j.Status>>() {
            @Override
            protected List<twitter4j.Status> doInBackground(Void... params) {
                try {
                    if (isFirst == true) {
                        // 引っ張って更新した時
                        return mTwitter.getHomeTimeline(new Paging(firstTweetId));
                    } else {
                        // 遡ったとき
                        return mTwitter.getHomeTimeline(new Paging(pageCount, 200));
                    }
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<twitter4j.Status> result) {
                if (result != null) {
                    if (isFirst == true) {
                        // 引っ張って更新した時
                        mAdapter.addTimeLine(result, isFirst);
                        showToast("最新更新");
                    } else {
                        // TLを遡ったとき
                        mAdapter.addTimeLine(result, isFirst);
                        restoreListPosition();
                        showToast(pageCount + "ページ目を読み込んだ");
                        pageCount++;
                    }
                    gridView.setAdapter(mAdapter);
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

    private void restoreFirstId() {
        firstTweetId = mAdapter.idList.getFirst();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
        intent.putExtra("url", mAdapter.getItemUrl(position));
        intent.putExtra("id", mAdapter.getItemId(position));
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        additionalTimeLine(true);
        restoreFirstId();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.setting) {
            showToast("set");
            return true;
        }
        return true;
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

}