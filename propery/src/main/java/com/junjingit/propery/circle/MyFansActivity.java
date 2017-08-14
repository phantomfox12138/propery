package com.junjingit.propery.circle;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.junjingit.propery.R;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.widget.GridItemDecoration;
import com.yanzhenjie.recyclerview.swipe.widget.ListItemDecoration;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jxy on 2017/8/14.
 */

public class MyFansActivity extends AppCompatActivity {
    private static final String TAG = "MyFansActivity";
    private Context mContext;
    private Toolbar title_bar;
    private SwipeRefreshLayout refresh_layout;
    private SwipeMenuRecyclerView myfans_list;
    private List<AVObject> fansDataList = new ArrayList<>();
    MyFansListAdapter myFansListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myfans_list);
        mContext = MyFansActivity.this;
        initView();
        initData();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        refresh_layout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        myfans_list = (SwipeMenuRecyclerView) findViewById(R.id.myFans_list);
        title_bar = (Toolbar) findViewById(R.id.title_bar);
        setSupportActionBar(title_bar);
        getSupportActionBar().setTitle(getString(R.string.my_fans));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        refresh_layout.setOnRefreshListener(mRefreshListener); // 刷新监听。
    }

    /**
     * 初始化数据 获取粉丝列表
     */
    private void initData(){
        myFansListAdapter = new MyFansListAdapter(this);
        myFansListAdapter.setmListData(fansDataList);
        myfans_list.setLayoutManager(new LinearLayoutManager(this));
        myfans_list.setAdapter(myFansListAdapter);
        myfans_list.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
        myfans_list.addItemDecoration(getItemDecoration());
        AVQuery<AVUser> followerQuery = null;
        try {
            followerQuery = AVUser.getCurrentUser().followerQuery(AVUser.class);
            followerQuery.findInBackground(new FindCallback<AVUser>() {
                @Override
                public void done(List<AVUser> avObjects, AVException avException) {
                    if (avException == null) {
                        fansDataList.clear();
                        fansDataList.addAll(avObjects);
                        myFansListAdapter.notifyDataSetChanged();
                    } else {
                        Log.v(TAG, "##################" + avException);
                    }
                }
            });
        } catch (AVException e) {
            e.printStackTrace();
        }
        refresh_layout.setRefreshing(false);

        // 第一次加载数据：一定要调用这个方法，否则不会触发加载更多。
        // 第一个参数：表示此次数据是否为空，假如你请求到的list为空(== null || list.size == 0)，那么这里就要true。
        // 第二个参数：表示是否还有更多数据，根据服务器返回给你的page等信息判断是否还有更多，这样可以提供性能，如果不能判断则传true。
       myfans_list.loadMoreFinish(false, true);
    }

    /**
     * 刷新。
     */
    private SwipeRefreshLayout.OnRefreshListener mRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            myfans_list.postDelayed(new Runnable() {
                @Override
                public void run() {
                    initData();
                }
            }, 1000);
        }
    };

    /**
     * 获取RecyclerView的Item分割线。
     */
    protected RecyclerView.ItemDecoration getItemDecoration() {
        RecyclerView.LayoutManager layoutManager = myfans_list.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            return new GridItemDecoration(ContextCompat.getColor(this,
                    R.color.divider_color));
        } else {
            return new ListItemDecoration(ContextCompat.getColor(this,
                    R.color.divider_color));
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        //Toolbar上的左上角的返回箭头的键值为Android.R.id.home  不是R.id.home
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
