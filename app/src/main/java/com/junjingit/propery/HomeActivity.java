package com.junjingit.propery;

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
import android.view.MenuItem;

import com.junjingit.propery.fragment.AroundFragment;
import com.junjingit.propery.fragment.CommunityFragment;
import com.junjingit.propery.fragment.HomeFragment;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity
{
    
    private BottomNavigationView mBottomNavi;
    
    private ViewPager mHomePager;
    
    private List<Fragment> mFragmentList;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        mFragmentList = new ArrayList<>();
        
        mFragmentList.add(new HomeFragment());
        mFragmentList.add(new AroundFragment());
        mFragmentList.add(new CommunityFragment());
        
        initView();
    }
    
    private void initView()
    {
        mBottomNavi = (BottomNavigationView) findViewById(R.id.bnv_menu);
        mHomePager = (ViewPager) findViewById(R.id.home_pager);
        
        mHomePager.setAdapter(new HomePagerAdapter(getSupportFragmentManager()));

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

                    case R.id.action_around:

                        mHomePager.setCurrentItem(1);
                        break;

                    case R.id.action_community:

                        mHomePager.setCurrentItem(2);
                        break;
                }
                return true;
            }
        });
    }
    
    class HomePagerAdapter extends FragmentPagerAdapter
    {
        
        public HomePagerAdapter(FragmentManager fm)
        {
            super(fm);
        }
        
        @Override
        public Fragment getItem(int position)
        {
            return mFragmentList.get(position);
        }
        
        @Override
        public int getCount()
        {
            return mFragmentList.size();
        }
    }
}
