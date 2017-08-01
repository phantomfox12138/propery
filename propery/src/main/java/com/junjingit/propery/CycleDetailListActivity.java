package com.junjingit.propery;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.junjingit.propery.common.FusionAction;

public class CycleDetailListActivity extends AppCompatActivity
{
    
    private static final String TAG = "FocusListActivity";
    
    private ImageView mCycleIcon;
    
    private TextView mCycleName;
    
    private Toolbar mToolbar;
    
    private CollapsingToolbarLayout mCollToolbar;
    
    private RecyclerView mFocusList;
    
    private List<AVObject> mCycleActiveList = new ArrayList<>();
    
    private HomeListAdapter mCycleAdapter;
    
    private FloatingActionButton mActionBtn;
    
    private String mObjectId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cycle_detail_list);
        
        initView();
        
        initData();
        
    }
    
    private void initView()
    {
        mCycleIcon = (ImageView) findViewById(R.id.cycle_icon);
        mCycleName = (TextView) findViewById(R.id.cycle_name);
        mFocusList = (RecyclerView) findViewById(R.id.focus_list);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mActionBtn = (FloatingActionButton) findViewById(R.id.fab_add);
        mCollToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolBar);
        
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        mFocusList.setLayoutManager(new LinearLayoutManager(this));
        
        mCycleAdapter = new HomeListAdapter(this);
        mCycleAdapter.setListData(mCycleActiveList);
        mCycleAdapter.setFrom("community");
        
        mFocusList.setAdapter(mCycleAdapter);
        
        mActionBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent("com.junjing.propery.QUOTE_EDITOR"));
            }
        });
        
    }
    
    private void initData()
    {
        mObjectId = getIntent().getStringExtra(FusionAction.FocusListExtra.OBJECT_ID);
        
        AVQuery<AVObject> query = new AVQuery<>("cycle");
        
        query.getInBackground(mObjectId, new GetCallback<AVObject>()
        {
            @Override
            public void done(AVObject avObject, AVException e)
            {
                if (null == e)
                {
                    
                    mCollToolbar.setTitle(avObject.getString("cycle_name"));
                    //                    mCycleName.setText(avObject.getString("cycle_name"));
                }
            }
        });
        
        AVQuery<AVObject> statusQuery = new AVQuery<>("Public_Status");
        statusQuery.whereEqualTo("cycle_id", mObjectId);
        
        statusQuery.findInBackground(new FindCallback<AVObject>()
        {
            @Override
            public void done(List<AVObject> list, AVException e)
            {
                if (null == e)
                {
                    mCycleActiveList.clear();
                    mCycleActiveList.addAll(list);
                    
                    mCycleAdapter.notifyDataSetChanged();
                }
            }
        });
        
        //        AVRelation<AVObject> relation = AVUser.getCurrentUser()
        //                .getRelation("cycle");
        //        AVQuery<AVObject> avQuery = relation.getQuery();
        //        avQuery.whereEqualTo("objectId", mObjectId);
        //        avQuery.findInBackground(new FindCallback<AVObject>()
        //        {
        //            @Override
        //            public void done(List<AVObject> list, AVException e)
        //            {
        //                if (null == e && null != null)
        //                {
        //                    
        //                }
        //            }
        //        });
        
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
