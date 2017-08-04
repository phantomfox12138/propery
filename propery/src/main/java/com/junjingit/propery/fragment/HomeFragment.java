package com.junjingit.propery.fragment;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.junjingit.propery.AppBarStateChangeListener;
import com.junjingit.propery.HomeActivity;
import com.junjingit.propery.HomeListAdapter;
import com.junjingit.propery.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment
{
    private View mRootView;
    
    private RecyclerView mHomeList;
    
    private SwipeRefreshLayout mRefresh;
    
    private HomeListAdapter mHomeAdapter;
    
    private AppBarLayout mAppBarLayout;
    
    private Toolbar mToolbar;
    
    public HomeListAdapter getHomeAdapter()
    {
        return mHomeAdapter;
    }
    
    public void notifyDataSetChanged()
    {
        mHomeAdapter.notifyDataSetChanged();
        
        mRefresh.setRefreshing(false);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        if (mRootView == null)
        {
            mRootView = inflater.inflate(R.layout.fragment_home, null);
            initView();
        }
        
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null)
        {
            parent.removeView(mRootView);
        }
        
        return mRootView;
    }
    
    private void initView()
    {
        mHomeList = mRootView.findViewById(R.id.rv_content);
        mRefresh = mRootView.findViewById(R.id.srl_refresh);
        mAppBarLayout = mRootView.findViewById(R.id.appbar);
        mToolbar = mRootView.findViewById(R.id.tb_toolbar);
        
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener()
        {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout,
                    int verticalOffset)
            {
                if (verticalOffset < 0
                        && Math.abs(verticalOffset) < appBarLayout.getTotalScrollRange())
                {
                    
                }
            }
        });
        
        mHomeAdapter = new HomeListAdapter(getActivity());
        mHomeAdapter.setFrom("home");
        
        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mHomeList.setLayoutManager(llm);
        
        mHomeList.setAdapter(mHomeAdapter);
        
        mRefresh.setRefreshing(true);
        
        mAppBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener()
        {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state)
            {
                if (state == State.EXPANDED)
                {
                    //展开状态
                }
                else if (state == State.COLLAPSED)
                {
                    //折叠状态
                }
                else
                {
                    //中间状态
                }
            }
        });
        
    }
    
}
