package com.junjingit.propery;

import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.junjingit.propery.common.FusionAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserStatusActivity extends AppCompatActivity
{
    
    private AppBarLayout mAppBar;
    
    private TextView mSubscriptionTitle;
    
    private TextView mSubscribe;
    
    private ImageView mHeadImage;
    
    private RecyclerView mUserStatusList;
    
    private Toolbar mToolbar;
    
    private float mSelfHeight = 0;//用以判断是否得到正确的宽高值
    
    private float mTitleScale;
    
    private float mSubScribeScale;
    
    private float mSubScribeScaleX;
    
    private float mHeadImgScale;
    
    private String mObjectId;
    
    private List<AVObject> mCycleActiveList = new ArrayList<>();
    
    private HomeListAdapter mStatusAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        Utility.setTranslucent(this);
        
        setContentView(R.layout.activity_user_status);
        
        initView();
        
        initData();
        //        initToolbar();
    }
    
    private void initData()
    {
        mObjectId = getIntent().getStringExtra(FusionAction.FocusListExtra.USER_ID);
        
        AVQuery<AVObject> statusQuery = new AVQuery<>("Public_Status");
        statusQuery.whereEqualTo("userId", mObjectId);
        statusQuery.orderByDescending("createdAt");
        
        statusQuery.findInBackground(new FindCallback<AVObject>()
        {
            @Override
            public void done(List<AVObject> list, AVException e)
            {
                if (null == e)
                {
                    mCycleActiveList.clear();
                    mCycleActiveList.addAll(list);
                    
                    mStatusAdapter.notifyDataSetChanged();
                }
            }
        });
    }
    
    private void initView()
    {
        mAppBar = (AppBarLayout) findViewById(R.id.appbar);
        mSubscriptionTitle = (TextView) findViewById(R.id.subscription_title);
        mSubscribe = (TextView) findViewById(R.id.subscribe);
        mHeadImage = (ImageView) findViewById(R.id.iv_head);
        mUserStatusList = (RecyclerView) findViewById(R.id.user_status_list);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        mUserStatusList.setLayoutManager(new LinearLayoutManager(this));
        
        mStatusAdapter = new HomeListAdapter(this);
        mStatusAdapter.setListData(mCycleActiveList);
        mStatusAdapter.setFrom("community");
        
        mUserStatusList.setAdapter(mStatusAdapter);
        
    }
    
    private void initToolbar()
    {
        final float screenW = getResources().getDisplayMetrics().widthPixels;
        final float toolbarHeight = getResources().getDimension(R.dimen.dp_32);
        final float initHeight = getResources().getDimension(R.dimen.dp_72);
        mAppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener()
        {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout,
                    int verticalOffset)
            {
                if (mSelfHeight == 0)
                {
                    mSelfHeight = mSubscriptionTitle.getHeight();
                    
                    float distanceTitle = mSubscriptionTitle.getTop()
                            + (mSelfHeight - toolbarHeight) / 2.0f;
                    
                    float distanceSubscribe = mSubscribe.getY()
                            + (mSubscribe.getHeight() - toolbarHeight) / 2.0f;
                    
                    float distanceHeadImg = mHeadImage.getY()
                            + (mHeadImage.getHeight() - toolbarHeight) / 2.0f;
                    
                    float distanceSubscribeX = screenW
                            / 2.0f
                            - (mSubscribe.getWidth() / 2.0f + getResources().getDimension(R.dimen.dp_16));
                    
                    mTitleScale = distanceTitle / (initHeight - toolbarHeight);
                    mSubScribeScale = distanceSubscribe
                            / (initHeight - toolbarHeight);
                    mHeadImgScale = distanceHeadImg
                            / (initHeight - toolbarHeight);
                    mSubScribeScaleX = distanceSubscribeX
                            / (initHeight - toolbarHeight);
                }
                float scale = 1.0f - (-verticalOffset)
                        / (initHeight - toolbarHeight);
                mHeadImage.setScaleX(scale);
                mHeadImage.setScaleY(scale);
                mHeadImage.setTranslationY(mHeadImgScale * verticalOffset);
                mSubscriptionTitle.setTranslationY(mTitleScale * verticalOffset);
                mSubscribe.setTranslationY(mSubScribeScale * verticalOffset);
                mSubscribe.setTranslationX(-mSubScribeScaleX * verticalOffset);
            }
        });
    }
}
