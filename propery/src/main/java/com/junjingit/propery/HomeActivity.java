package com.junjingit.propery;

import java.util.ArrayList;
import java.util.List;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.junjingit.propery.common.FusionAction;
import com.junjingit.propery.fragment.AroundFragment;
import com.junjingit.propery.fragment.CommunityFragment;
import com.junjingit.propery.fragment.HomeFragment;
import com.junjingit.propery.fragment.MeFragment;
import com.melnykov.fab.FloatingActionButton;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.tiancaicc.springfloatingactionmenu.MenuItemView;
import com.tiancaicc.springfloatingactionmenu.OnMenuActionListener;
import com.tiancaicc.springfloatingactionmenu.SpringFloatingActionMenu;

public class HomeActivity extends AppCompatActivity implements
        View.OnClickListener
{
    
    private BottomNavigationViewEx mBottomNavi;
    
    private ViewPager mHomePager;
    
    private SlidingUpPanelLayout mLayout;
    
    private List<Fragment> mFragmentList;
    
    private AnimationDrawable frameAnim;
    
    private HomePagerAdapter mHomePagerAdapter;
    
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
    
    public HomeActivity(SlidingUpPanelLayout mLayout)
    {
        this.mLayout = mLayout;
    }
    
    public HomeActivity()
    {
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        Utility.setTranslucent(this);
        setContentView(R.layout.activity_home);
        //        
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
        mBottomNavi.inflateMenu(R.menu.menu_bottom_navigation);
        
        final FloatingActionButton fab = new FloatingActionButton(this);
        
        mLayout.setAnchorPoint(0.7f);
        mLayout.setPanelHeight(0);
        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
        
        mHomePagerAdapter = new HomePagerAdapter(getSupportFragmentManager());
        mHomePagerAdapter.setList(mFragmentList);
        
        mHomePager.setAdapter(mHomePagerAdapter);
        //        Utility.disableShiftMode(mBottomNavi);
        
        //        mBottomNavi.enableAnimation(false);
        mBottomNavi.enableShiftingMode(false);
        mBottomNavi.enableItemShiftingMode(false);
        
        addBadgeAt(1, 10);
        
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
                    springFloatingActionMenu.setVisibility(View.GONE);
                    fab.setVisibility(View.GONE);
                }
                
                if (newState == SlidingUpPanelLayout.PanelState.HIDDEN)
                {
                    springFloatingActionMenu.setVisibility(View.VISIBLE);
                    fab.setVisibility(View.VISIBLE);
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
        else
        {
            super.onBackPressed();
        }
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
                            Toast.makeText(HomeActivity.this,
                                    "已读",
                                    Toast.LENGTH_SHORT).show();
                            
                            badge.hide(true);
                        }
                    }
                });
    }
}
