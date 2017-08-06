package com.junjingit.propery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVStatus;
import com.avos.avoscloud.AVStatusQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.junjingit.propery.common.FusionAction;
import com.junjingit.propery.fragment.AroundFragment;
import com.junjingit.propery.fragment.CommunityFragment;
import com.junjingit.propery.fragment.HomeFragment;
import com.junjingit.propery.fragment.MeFragment;
import com.melnykov.fab.FloatingActionButton;
import com.sothree.slidinguppanel.ScrollableViewHelper;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.tiancaicc.springfloatingactionmenu.MenuItemView;
import com.tiancaicc.springfloatingactionmenu.OnMenuActionListener;
import com.tiancaicc.springfloatingactionmenu.SpringFloatingActionMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

public class HomeActivity extends AppCompatActivity implements
        View.OnClickListener, OnIconClickListener
{
    
    private static final String TAG = "HomeActivity";
    
    private BottomNavigationViewEx mBottomNavi;
    
    private ViewPager mHomePager;
    
    private SlidingUpPanelLayout mLayout;
    
    private SwipeMenuRecyclerView mUserStatusList;
    
    private AppBarLayout mAppbarLayout;
    
    private HomeListAdapter mUserStatusAdapter;
    
    private List<AVObject> mUserStatusDataList = new ArrayList<>();
    
    private List<Fragment> mFragmentList;
    
    private AnimationDrawable frameAnim;
    
    private HomePagerAdapter mHomePagerAdapter;
    
    private Badge mBadge;
    
    private String mUserId;
    
    private static int[] frameAnimRes = new int[] { R.mipmap.compose_anim_1,
            R.mipmap.compose_anim_2, R.mipmap.compose_anim_3,
            R.mipmap.compose_anim_4, R.mipmap.compose_anim_5,
            R.mipmap.compose_anim_6, R.mipmap.compose_anim_7,
            R.mipmap.compose_anim_8, R.mipmap.compose_anim_9,
            R.mipmap.compose_anim_10, R.mipmap.compose_anim_11,
            R.mipmap.compose_anim_12, R.mipmap.compose_anim_13,
            R.mipmap.compose_anim_14, R.mipmap.compose_anim_15,
            R.mipmap.compose_anim_15, R.mipmap.compose_anim_16,
            R.mipmap.compose_anim_17, R.mipmap.compose_anim_18,
            R.mipmap.compose_anim_19 };
    
    private int frameDuration = 20;
    
    private SpringFloatingActionMenu springFloatingActionMenu;
    
    private AnimationDrawable frameReverseAnim;
    
    public void setUnreadCount(int count)
    {
        mBadge.setBadgeNumber(count);
    }
    
    //    @Override
    //    public void onWindowFocusChanged(boolean hasFocus)
    //    {
    //        super.onWindowFocusChanged(hasFocus);
    //        
    //        if (hasFocus && Build.VERSION.SDK_INT >= 19)
    //        {
    //            View decorView = getWindow().getDecorView();
    //            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    //                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
    //                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    //                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    //        }
    //    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        //        Utility.setTranslucent(this);
        setContentView(R.layout.activity_home);
        
        mFragmentList = new ArrayList<>();
        
        mFragmentList.add(new HomeFragment());
        mFragmentList.add(new CommunityFragment());
        mFragmentList.add(new AroundFragment());
        mFragmentList.add(new MeFragment());
        
        createFabFrameAnim();
        createFabReverseFrameAnim();
        
        initView();
        
        initData();
        getOwnerDevice();//检查设备
        
        setSupportActionBar(((HomeFragment) mFragmentList.get(0)).getToolbar());
        
    }
    
    private void initData()
    {
        AVQuery<AVObject> query = new AVQuery<>("Public_Status");
        query.whereEqualTo("cycle_id", "123");
        
        query.findInBackground(new FindCallback<AVObject>()
        {
            @Override
            public void done(List<AVObject> list, AVException e)
            {
                if (null == e)
                {
                    ((HomeFragment) mFragmentList.get(0)).getHomeAdapter()
                            .setListData(list);
                    
                    ((HomeFragment) mFragmentList.get(0)).notifyDataSetChanged();
                    
                }
            }
        });
        
        AVStatus.getUnreadStatusesCountInBackground(AVStatus.INBOX_TYPE.TIMELINE.toString(),
                new CountCallback()
                {
                    @Override
                    public void done(int i, AVException e)
                    {
                        if (null == e)
                        {
                            mBadge = addBadgeAt(1, 0);
                        }
                    }
                });
    }
    
    @Override
    protected void onResume()
    {
        super.onResume();
        
        if (springFloatingActionMenu.isMenuOpen())
        {
            springFloatingActionMenu.hideMenu();
        }
    }
    
    private void initView()
    {
        mBottomNavi = (BottomNavigationViewEx) findViewById(R.id.bnv_menu);
        mHomePager = (ViewPager) findViewById(R.id.home_pager);
        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mUserStatusList = (SwipeMenuRecyclerView) findViewById(R.id.user_status_list);
        mAppbarLayout = (AppBarLayout) findViewById(R.id.appbar);
        
        mLayout.setPanelHeight(0);
        mLayout.setFadeOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mLayout.setPanelHeight(0);
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });
        
        mUserStatusList.setLayoutManager(new LinearLayoutManager(this));
        mUserStatusAdapter = new HomeListAdapter(this);
        mUserStatusAdapter.setFrom("community");
        
        mUserStatusAdapter.setListData(mUserStatusDataList);
        
        mUserStatusList.setAdapter(mUserStatusAdapter);
        
        mBottomNavi.inflateMenu(R.menu.menu_bottom_navigation);
        
        final FloatingActionButton fab = new FloatingActionButton(this);
        
        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        
        mHomePagerAdapter = new HomePagerAdapter(getSupportFragmentManager());
        mHomePagerAdapter.setList(mFragmentList);
        
        mHomePager.setAdapter(mHomePagerAdapter);
        //        Utility.disableShiftMode(mBottomNavi);
        
        //        mBottomNavi.enableAnimation(false);
        mBottomNavi.enableShiftingMode(false);
        mBottomNavi.enableItemShiftingMode(false);
        
        //        addBadgeAt(1, 10);
        
        //        mBottomNavi.setupWithViewPager(mHomePager, true);
        
        fab.setType(FloatingActionButton.TYPE_NORMAL);
        fab.setImageDrawable(frameAnim);
        
        springFloatingActionMenu = new SpringFloatingActionMenu.Builder(this).fab(fab)
                .addMenuItem(R.color.photo,
                        R.mipmap.ic_messaging_posttype_photo,
                        "发照片",
                        R.color.text_color,
                        this)
                .addMenuItem(R.color.chat,
                        R.mipmap.ic_messaging_posttype_chat,
                        "发消息",
                        R.color.text_color,
                        this)
                .addMenuItem(R.color.quote,
                        R.mipmap.ic_messaging_posttype_quote,
                        "发图文",
                        R.color.text_color,
                        this)
                .addMenuItem(R.color.link,
                        R.mipmap.ic_messaging_posttype_link,
                        "加圈子",
                        R.color.text_color,
                        this)
                .addMenuItem(R.color.audio,
                        R.mipmap.ic_messaging_posttype_audio,
                        "发语音",
                        R.color.text_color,
                        this)
                .addMenuItem(R.color.text,
                        R.mipmap.ic_messaging_posttype_text,
                        "发文字",
                        R.color.text_color,
                        this)
                .addMenuItem(R.color.video,
                        R.mipmap.ic_messaging_posttype_video,
                        "发视频",
                        R.color.text_color,
                        this)
                .animationType(SpringFloatingActionMenu.ANIMATION_TYPE_TUMBLR)
                .revealColor(R.color.light_white)
                .gravity(Gravity.BOTTOM | Gravity.CENTER)
                .onMenuActionListner(new OnMenuActionListener()
                {
                    @Override
                    public void onMenuOpen()
                    {
                        fab.setImageDrawable(frameAnim);
                        frameReverseAnim.stop();
                        frameAnim.start();
                    }
                    
                    @Override
                    public void onMenuClose()
                    {
                        fab.setImageDrawable(frameReverseAnim);
                        frameAnim.stop();
                        frameReverseAnim.start();
                    }
                })
                .build();
        
        mHomePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            MenuItem prevMenuItem = null;
            
            @Override
            public void onPageScrolled(int position, float positionOffset,
                    int positionOffsetPixels)
            {
                
            }
            
            @Override
            public void onPageSelected(int position)
            {
                //                if (position >= 2)
                //                {
                //                    position = position + 1;
                //                }
                //                
                //                if (prevMenuItem != null)
                //                {
                //                    prevMenuItem.setChecked(false);
                //                }
                //                else
                //                {
                //                    mBottomNavi.getMenu().getItem(0).setChecked(false);
                //                }
                //                mBottomNavi.getMenu().getItem(position).setChecked(true);
                //                prevMenuItem = mBottomNavi.getMenu().getItem(position);
                
                switch (position)
                {
                    case 0:
                        mBottomNavi.setCurrentItem(0);
                        break;
                    case 1:
                        
                        mBottomNavi.setCurrentItem(1);
                        
                        break;
                    
                    case 2:
                        
                        mBottomNavi.setCurrentItem(3);
                        break;
                    
                    case 3:
                        
                        mBottomNavi.setCurrentItem(4);
                        break;
                }
                
            }
            
            @Override
            public void onPageScrollStateChanged(int state)
            {
                
            }
        });
        
        mBottomNavi.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                switch (item.getItemId())
                {
                    case R.id.action_home:
                        
                        mHomePager.setCurrentItem(0);
                        
                        break;
                    
                    case R.id.action_community:
                        
                        mHomePager.setCurrentItem(1);
                        break;
                    
                    case R.id.action_around:
                        
                        mHomePager.setCurrentItem(2);
                        break;
                    
                    case R.id.action_me:
                        
                        mHomePager.setCurrentItem(3);
                        break;
                }
                
                return true;
            }
        });
        
        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener()
        {
            @Override
            public void onPanelSlide(View panel, float slideOffset)
            {
                
            }
            
            @Override
            public void onPanelStateChanged(View panel,
                    SlidingUpPanelLayout.PanelState previousState,
                    SlidingUpPanelLayout.PanelState newState)
            {
                if (newState == SlidingUpPanelLayout.PanelState.ANCHORED)
                {
                    
                }
                
                if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED)
                {
                    mLayout.setPanelHeight(0);
                    springFloatingActionMenu.setVisibility(View.VISIBLE);
                    fab.setVisibility(View.VISIBLE);
                    springFloatingActionMenu.hideMenu();
                    mLayout.setAnchorPoint(1.0f);
                }
                
                if (newState == SlidingUpPanelLayout.PanelState.HIDDEN)
                {
                    mLayout.setPanelHeight(0);
                    springFloatingActionMenu.hideMenu();
                    springFloatingActionMenu.setVisibility(View.VISIBLE);
                    fab.setVisibility(View.VISIBLE);
                }
                
                if (newState == SlidingUpPanelLayout.PanelState.EXPANDED)
                {
                    Log.d(TAG, "PanelState.EXPANDED");
                    springFloatingActionMenu.setVisibility(View.GONE);
                    fab.setVisibility(View.GONE);
                    
                    AVQuery<AVUser> query = AVUser.followeeQuery(mUserId,
                            AVUser.class);
                    query.include("followee");
                    
                    query.findInBackground(new FindCallback<AVUser>()
                    {
                        @Override
                        public void done(List<AVUser> list, AVException e)
                        {
                            if (null == e && null != list)
                            {
                                final List<AVObject> result = new ArrayList<>();
                                
                                final AVQuery<AVObject> pubStatusQuery = new AVQuery<AVObject>(
                                        "Public_Status");
                                
                                pubStatusQuery.whereEqualTo("userId", mUserId);
                                
                                pubStatusQuery.orderByAscending("createdAt");
                                pubStatusQuery.findInBackground(new FindCallback<AVObject>()
                                {
                                    @Override
                                    public void done(List<AVObject> list,
                                            AVException e)
                                    {
                                        
                                        if (null == e)
                                        {
                                            result.clear();
                                            result.addAll(list);
                                        }
                                        
                                    }
                                });
                                
                                if (list.isEmpty())
                                {
                                    mUserStatusDataList.clear();
                                    mUserStatusDataList.addAll(result);
                                    
                                    mUserStatusAdapter.notifyDataSetChanged();
                                    //                                    mLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                                    return;
                                }
                                
                                //                                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                                
                                AVQuery<AVUser> userQuery = new AVQuery<AVUser>(
                                        "_User");
                                userQuery.getInBackground(mUserId,
                                        new GetCallback<AVUser>()
                                        {
                                            @Override
                                            public void done(AVUser avUser,
                                                    AVException e)
                                            {
                                                if (null == e)
                                                {
                                                    try
                                                    {
                                                        AVQuery userStatusQuery = AVStatus.statusQuery(avUser);
                                                        userStatusQuery.orderByDescending("createdAt");
                                                        
                                                        userStatusQuery.findInBackground(new FindCallback<AVObject>()
                                                        {
                                                            @Override
                                                            public void done(
                                                                    List<AVObject> list,
                                                                    AVException e)
                                                            {
                                                                
                                                                if (null == e)
                                                                {
                                                                    result.addAll(list);
                                                                    
                                                                    Log.d(TAG,
                                                                            "list.size = "
                                                                                    + list);
                                                                    
                                                                    Collections.sort(result,
                                                                            new Comparator<AVObject>()
                                                                            {
                                                                                
                                                                                @Override
                                                                                public int compare(
                                                                                        AVObject avObject,
                                                                                        AVObject t1)
                                                                                {
                                                                                    
                                                                                    return avObject.getCreatedAt()
                                                                                            .compareTo(t1.getCreatedAt());
                                                                                }
                                                                            });
                                                                    
                                                                    mUserStatusDataList.clear();
                                                                    mUserStatusDataList.addAll(result);
                                                                    
                                                                    mUserStatusAdapter.notifyDataSetChanged();
                                                                    
                                                                }
                                                            }
                                                        });
                                                    }
                                                    catch (AVException e1)
                                                    {
                                                        e1.printStackTrace();
                                                    }
                                                }
                                            }
                                        });
                                
                            }
                        }
                    });
                }
                
                if (newState == SlidingUpPanelLayout.PanelState.DRAGGING)
                {
                    mAppbarLayout.setExpanded(true);
                    Log.d(TAG, "PanelState.DRAGGING");
                }
                
            }
        });
        
    }
    
    private void createFabFrameAnim()
    {
        frameAnim = new AnimationDrawable();
        frameAnim.setOneShot(true);
        Resources resources = getResources();
        for (int res : frameAnimRes)
        {
            frameAnim.addFrame(resources.getDrawable(res), frameDuration);
        }
    }
    
    private void createFabReverseFrameAnim()
    {
        frameReverseAnim = new AnimationDrawable();
        frameReverseAnim.setOneShot(true);
        Resources resources = getResources();
        for (int i = frameAnimRes.length - 1; i >= 0; i--)
        {
            frameReverseAnim.addFrame(resources.getDrawable(frameAnimRes[i]),
                    frameDuration);
        }
    }
    
    @Override
    public void onClick(View view)
    {
        MenuItemView menuItemView = (MenuItemView) view;
        
        if (menuItemView.getLabelTextView().getText().equals("发图文"))
        {
            startActivity(new Intent("com.junjing.propery.QUOTE_EDITOR"));
        }
        if (menuItemView.getLabelTextView().getText().equals("加圈子"))
        {
            startActivity(new Intent("com.junjing.propery.CYCLE_LIST"));
        }
        
        if (springFloatingActionMenu.isMenuOpen())
        {
            springFloatingActionMenu.hideMenu();
        }
        
    }
    
    @Override
    public void onBackPressed()
    {
        if (springFloatingActionMenu.isMenuOpen())
        {
            springFloatingActionMenu.hideMenu();
        }
        else if (mLayout != null
                && (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED))
        {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
        else
        {
            super.onBackPressed();
        }
    }
    
    @Override
    public void onIconClick(final String userId)
    {
        //        mLayout.setAnchorPoint(0.7f);
        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        
        mUserId = userId;
    }
    
    class HomePagerAdapter extends FragmentPagerAdapter
    {
        
        private List<Fragment> list;
        
        public void setList(List<Fragment> list)
        {
            this.list = list;
        }
        
        public HomePagerAdapter(FragmentManager fm)
        {
            super(fm);
        }
        
        @Override
        public Fragment getItem(int position)
        {
            return list.get(position);
        }
        
        @Override
        public int getCount()
        {
            return list.size();
        }
        
    }
    
    public void getOwnerDevice()
    {
        if (AVUser.getCurrentUser() == null)
        {
            //判断当前用户是否为空
            Intent intent = new Intent(FusionAction.LOGIN_ACTION);
            startActivity(intent);
        }
        else
        {
            AVQuery<AVObject> avQuery = new AVQuery<>("Device");
            String deviceNum = AVUser.getCurrentUser().getString("deviceId");
            avQuery.getInBackground(deviceNum, new GetCallback<AVObject>()
            {
                @Override
                public void done(AVObject avObject, AVException e)
                {
                    if (e == null)
                    {
                        String formDeviceId = avObject.getString("deviceId");
                        if (formDeviceId.equals(getDeviceId()))
                        {
                            //说明是同一个设备
                            Toast.makeText(HomeActivity.this,
                                    "表示当前设备",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(HomeActivity.this,
                                    "已经在另外一个设备登录",
                                    Toast.LENGTH_SHORT).show();
                            AVUser.getCurrentUser().logOut();//当前用户登出
                        }
                    }
                    else
                    {
                        // 失败的话，请检查网络环境以及 SDK 配置是否正确
                    }
                }
            });
        }
    }
    
    /**
     * 获取设置唯一Id
     *
     * @return
     */
    public String getDeviceId()
    {
        TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }
    
    private Badge addBadgeAt(int position, int number)
    {
        // add badge
        return new QBadgeView(this).setBadgeNumber(number)
                .setGravityOffset(12, 2, true)
                .bindTarget(mBottomNavi.getBottomNavigationItemView(position))
                .setOnDragStateChangedListener(new Badge.OnDragStateChangedListener()
                {
                    @Override
                    public void onDragStateChanged(int dragState, Badge badge,
                            View targetView)
                    {
                        if (Badge.OnDragStateChangedListener.STATE_SUCCEED == dragState)
                        {
                            //                            Toast.makeText(HomeActivity.this,
                            //                                    "已读",
                            //                                    Toast.LENGTH_SHORT).show();
                            
                            badge.hide(true);
                        }
                    }
                });
    }
    
}
