package com.junjingit.propery;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
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
import com.avos.avoscloud.AVStatusQuery;
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
    
    private String mUserId;
    
    private float mSelfHeight = 0;//用以判断是否得到正确的宽高值
    
    private float mTitleScale;
    
    private float mSubScribeScale;
    
    private float mSubScribeScaleX;
    
    private float mHeadImgScale;
    
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
        
        //        final float screenW = getResources().getDisplayMetrics().widthPixels;
        //        final float toolbarHeight = getResources().getDimension(R.dimen.toolbar_height);
        //        final float initHeight = getResources().getDimension(R.dimen.subscription_head);
        //        mAppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener()
        //        {
        //            @Override
        //            public void onOffsetChanged(AppBarLayout appBarLayout,
        //                    int verticalOffset)
        //            {
        //                if (mSelfHeight == 0)
        //                {
        //                    mSelfHeight = mSubscriptionTitle.getHeight();
        //                    float distanceTitle = mSubscriptionTitle.getTop()
        //                            + (mSelfHeight - toolbarHeight) / 2.0f;
        //                    float distanceSubscribe = mSubscribe.getY()
        //                            + (mSubscribe.getHeight() - toolbarHeight) / 2.0f;
        //                    float distanceHeadImg = mHeadImage.getY()
        //                            + (mHeadImage.getHeight() - toolbarHeight) / 2.0f;
        //                    float distanceSubscribeX = screenW
        //                            / 2.0f
        //                            - (mSubscribe.getWidth() / 2.0f + getResources().getDimension(R.dimen.normal_space));
        //                    mTitleScale = distanceTitle / (initHeight - toolbarHeight);
        //                    mSubScribeScale = distanceSubscribe
        //                            / (initHeight - toolbarHeight);
        //                    mHeadImgScale = distanceHeadImg
        //                            / (initHeight - toolbarHeight);
        //                    mSubScribeScaleX = distanceSubscribeX
        //                            / (initHeight - toolbarHeight);
        //                }
        //                float scale = 1.0f - (-verticalOffset)
        //                        / (initHeight - toolbarHeight);
        //                mHeadImage.setScaleX(scale);
        //                mHeadImage.setScaleY(scale);
        //                mHeadImage.setTranslationY(mHeadImgScale * verticalOffset);
        //                mSubscriptionTitle.setTranslationY(mTitleScale * verticalOffset);
        //                mSubscribe.setTranslationY(mSubScribeScale * verticalOffset);
        //                mSubscribe.setTranslationX(-mSubScribeScaleX * verticalOffset);
        //            }
        //        });
        
    }
    
    private void initData()
    {
        mObjectId = getIntent().getStringExtra(FusionAction.FocusListExtra.OBJECT_ID);
        
        if (!StringUtil.isNullOrEmpty(mObjectId))
        {
            AVQuery<AVObject> query = new AVQuery<>("cycle");
            
            query.getInBackground(mObjectId, new GetCallback<AVObject>()
            {
                @Override
                public void done(AVObject avObject, AVException e)
                {
                    if (null == e)
                    {
                        
                        //                    mCollToolbar.setTitle(avObject.getString("cycle_name"));
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
        }
        
        String from = getIntent().getStringExtra(FusionAction.FocusListExtra.FROM);
        mUserId = getIntent().getStringExtra(FusionAction.FocusListExtra.USER_ID);
        
        if (!StringUtil.isNullOrEmpty(from))
        {
            if (from.equals("cycle"))
            {
                
            }
        }
        
        if (!StringUtil.isNullOrEmpty(mUserId))
        {
            
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
