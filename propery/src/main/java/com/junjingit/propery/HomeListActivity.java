package com.junjingit.propery;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.junjingit.propery.common.FusionAction;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HomeListActivity extends AppCompatActivity
{
    
    private Toolbar mToolbar;
    
    private SwipeRefreshLayout mRefresh;
    
    private SwipeMenuRecyclerView mList;
    
    private TextView mTitleText;
    
    private List<AVObject> mDataList = new ArrayList<>();
    
    private String mType;
    
    private HomeListAdapter mAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_list);
        
        initView();
        
        initData();
        
    }
    
    private void initData()
    {
        mType = getIntent().getStringExtra(FusionAction.HomeListExtra.TYPE);
        String title = getIntent().getStringExtra(FusionAction.HomeListExtra.TITLE_NAME);
        
        //        mTitleText.setText(title);
        getSupportActionBar().setTitle(title);
        
        AVQuery<AVObject> query = new AVQuery<>("Public_Status");
        
        query.whereEqualTo("cycle_id",
                (!StringUtil.isNullOrEmpty(mType) && mType.equals(FusionAction.HomeListExtra.NOTIFY)) ? "123"
                        : "456");
        
        query.findInBackground(new FindCallback<AVObject>()
        {
            @Override
            public void done(List<AVObject> list, AVException e)
            {
                if (null == e)
                {
                    mDataList.clear();
                    mDataList.addAll(list);
                    
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
        
    }
    
    private void initView()
    {
        mToolbar = (Toolbar) findViewById(R.id.title_bar);
        mRefresh = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        mList = (SwipeMenuRecyclerView) findViewById(R.id.home_title_list);
//        mTitleText = (TextView) findViewById(R.id.home_list_title);
        
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        mAdapter = new HomeListAdapter(this);
        mAdapter.setListData(mDataList);
        mAdapter.setFrom("home");
        
        mList.setLayoutManager(new LinearLayoutManager(this));
        mList.setAdapter(mAdapter);
        
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
