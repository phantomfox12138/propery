package com.junjingit.propery.circle;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.FindCallback;
import com.junjingit.propery.R;
import com.junjingit.propery.fragment.ActiveFragment;
import com.junjingit.propery.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/13 0013.
 */

public class MyFollowActivity extends AppCompatActivity {
    private static final String TAG = "MyFollowActivity";
    private Context mContext;
    private Toolbar title_bar;
    private TabLayout sliding_tabs;
    private ViewPager follow_viewPage;
    private List<Fragment> mFragments;
    MyFollowPagerAdapter myFollowPagerAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);
        mContext = MyFollowActivity.this;
        initView();
        initData();
    }

    /**
     * 初始化控件和数据
     */
    private void initView() {
        title_bar = (Toolbar) findViewById(R.id.title_bar);
        sliding_tabs = (TabLayout) findViewById(R.id.sliding_tabs);
        follow_viewPage = (ViewPager) findViewById(R.id.follow_viewPage);
    }

    private void initData() {
        setSupportActionBar(title_bar);
        getSupportActionBar().setTitle(getString(R.string.my_follow));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sliding_tabs.setTabMode(TabLayout.MODE_FIXED);
        sliding_tabs.addTab(sliding_tabs.newTab().setText(getString(R.string.haved_follow_user)));
        sliding_tabs.addTab(sliding_tabs.newTab().setText(getString(R.string.haved_follow_circle)));

        mFragments = new ArrayList<>();
        mFragments.add(new FollowUserFragment());//我所关注的人
        mFragments.add(new FollowCircleFragment());//我所关注的圈子

        // 实例化ViewPage的适配器
        myFollowPagerAdapter = new MyFollowPagerAdapter(getSupportFragmentManager(), mFragments);
        follow_viewPage.setAdapter(myFollowPagerAdapter);



        sliding_tabs.setupWithViewPager(follow_viewPage, true);
        sliding_tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                follow_viewPage.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public class MyFollowPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragmentList;
        private String tabTitles[] = new String[]{getString(R.string.haved_follow_user),getString(R.string.haved_follow_circle)};

        public MyFollowPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
            super(fm);
            this.fragmentList = fragmentList;
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Toolbar上的左上角的返回箭头的键值为Android.R.id.home  不是R.id.home
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
