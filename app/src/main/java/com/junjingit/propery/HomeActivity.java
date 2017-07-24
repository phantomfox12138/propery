package com.junjingit.propery;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.junjingit.propery.fragment.AroundFragment;
import com.junjingit.propery.fragment.CommunityFragment;
import com.junjingit.propery.fragment.HomeFragment;
import com.junjingit.propery.fragment.MeFragment;
import com.melnykov.fab.FloatingActionButton;
import com.tiancaicc.springfloatingactionmenu.MenuItemView;
import com.tiancaicc.springfloatingactionmenu.OnMenuActionListener;
import com.tiancaicc.springfloatingactionmenu.SpringFloatingActionMenu;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements
        View.OnClickListener
{
    
    private BottomNavigationView mBottomNavi;
    
    private ViewPager mHomePager;
    
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
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        Utility.setTranslucent(this);
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
        mBottomNavi = (BottomNavigationView) findViewById(R.id.bnv_menu);
        mHomePager = (ViewPager) findViewById(R.id.home_pager);
        
        mHomePagerAdapter = new HomePagerAdapter(getSupportFragmentManager());
        mHomePagerAdapter.setList(mFragmentList);
        
        mHomePager.setAdapter(mHomePagerAdapter);
        Utility.disableShiftMode(mBottomNavi);
        
        final FloatingActionButton fab = new FloatingActionButton(this);
        
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
                if (position >= 2)
                {
                    position = position + 1;
                }
                
                if (prevMenuItem != null)
                {
                    prevMenuItem.setChecked(false);
                }
                else
                {
                    mBottomNavi.getMenu().getItem(0).setChecked(false);
                }
                mBottomNavi.getMenu().getItem(position).setChecked(true);
                prevMenuItem = mBottomNavi.getMenu().getItem(position);
                
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
}
