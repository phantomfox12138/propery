package com.junjingit.propery.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.junjingit.propery.CommunityViewPager;
import com.junjingit.propery.R;
import com.tiancaicc.springfloatingactionmenu.MenuItemView;
import com.tiancaicc.springfloatingactionmenu.SpringFloatingActionMenu;

public class CommunityFragment extends Fragment implements View.OnClickListener
{
    private static final String TAG = "CommunityFragment";
    
    private View mRootView;
    
    private TabLayout mTabLayout;
    
    private ViewPager mCommunityVp;
    
    private List<Fragment> mFragments;
    
    private CommunityAdapter mCommunityAdapter;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        if (mRootView == null)
        {
            mRootView = inflater.inflate(R.layout.fragment_community, null);
            
            initView();
        }
        
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null)
        {
            parent.removeView(mRootView);
        }
        
        return mRootView;
    }
    
    private View initTabName(String name)
    {
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.community_tab_layout, null);
        
        TextView title = v.findViewById(R.id.tab_name);
        title.setTextColor(Color.WHITE);
        title.setText(name);
        
        return v;
    }
    
    private void initData()
    {
        
        final AVQuery<AVObject> pubStatusList = new AVQuery<>("Public_Status");
        
        pubStatusList.whereNotEqualTo("cycle_id", "123");
        pubStatusList.orderByDescending("createdAt");
        
        pubStatusList.findInBackground(new FindCallback<AVObject>()
        {
            @Override
            public void done(List<AVObject> list, AVException e)
            {
                if (null == e)
                {
                    Log.d(TAG, "list size = " + list.size());
                    
                    ((ActiveFragment) mFragments.get(0)).getActiveAdapter()
                            .setListData(list);
                    
                    notifyDataSetChanged();
                    //                    mCommunityAdapter.notifyDataSetChanged();
                }
            }
        });
    }
    
    private void notifyDataSetChanged()
    {
        ((ActiveFragment) mFragments.get(0)).notifyDataSetChanged();
        
    }
    
    private void initView()
    {
        
        mTabLayout = mRootView.findViewById(R.id.tab_layout);
        mCommunityVp = mRootView.findViewById(R.id.community_vp);
        mCommunityVp.setOffscreenPageLimit(2);
        //        mCommunityVp.setNoScroll(true);
        
        mTabLayout.addTab(mTabLayout.newTab().setCustomView(initTabName("动态")));
        mTabLayout.addTab(mTabLayout.newTab().setCustomView(initTabName("关注")));
        
        mFragments = new ArrayList<>();
        
        mCommunityAdapter = new CommunityAdapter(getFragmentManager());
        mCommunityAdapter.setList(mFragments);
        
        mFragments.add(new ActiveFragment());
        
        FocusFragment focus = new FocusFragment();
        focus.setCommunityAdapter(mCommunityAdapter);
        mFragments.add(focus);
        
        mCommunityAdapter.notifyDataSetChanged();
        
        mCommunityVp.setAdapter(mCommunityAdapter);
        
        initData();
        mTabLayout.setupWithViewPager(mCommunityVp, true);
        
        //        mTabLayout.setTabGravity(Gravity.CENTER);
        //        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        
        mCommunityAdapter.notifyDataSetChanged();
        
    }
    
    @Override
    public void onClick(View view)
    {
        Log.d("TAG eg", "onclick");
        MenuItemView menuItemView = (MenuItemView) view;
        Toast.makeText(getActivity(),
                menuItemView.getLabelTextView().getText(),
                Toast.LENGTH_SHORT).show();
    }
    
    class CommunityAdapter extends FragmentPagerAdapter
    {
        
        private List<Fragment> list;
        
        private int position = 0;
        
        public void setPosition(int position)
        {
            this.position = position;
        }
        
        public void setList(List<Fragment> list)
        {
            this.list = list;
        }
        
        public CommunityAdapter(FragmentManager fm)
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
        
        @Override
        public void notifyDataSetChanged()
        {
            super.notifyDataSetChanged();
            mTabLayout.removeAllTabs();
            
            mTabLayout.addTab(mTabLayout.newTab()
                    .setCustomView(initTabName("动态")));
            mTabLayout.addTab(mTabLayout.newTab()
                    .setCustomView(initTabName("关注")));
            //        mTabLayout.getTabAt(0).setText("动态");
            //        mTabLayout.getTabAt(1).setText("关注");
            
            Log.d(TAG,
                    "tab textview = "
                            + mTabLayout.getTabAt(0)
                                    .getCustomView()
                                    .findViewById(R.id.tab_name));
            
            mTabLayout.setTabTextColors(Color.WHITE, Color.WHITE);
            mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
            {
                @Override
                public void onTabSelected(TabLayout.Tab tab)
                {
                    mCommunityVp.setCurrentItem(tab.getPosition());
                }
                
                @Override
                public void onTabUnselected(TabLayout.Tab tab)
                {
                    
                }
                
                @Override
                public void onTabReselected(TabLayout.Tab tab)
                {
                    
                }
            });
            
            mCommunityVp.setCurrentItem(position);
        }
    }
    
}
