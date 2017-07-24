package com.junjingit.propery.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.junjingit.propery.HomeListAdapter;
import com.junjingit.propery.R;

import java.util.ArrayList;
import java.util.List;

public class ActiveFragment extends Fragment
{
    private static final String TAG = "ActiveFragment";
    
    private View mRootView;
    
    private RecyclerView mActiveList;
    
    private SwipeRefreshLayout mRefresh;
    
    private HomeListAdapter mActiveAdapter;
    
    private List<AVObject> mDataList;
    
    public HomeListAdapter getActiveAdapter()
    {
        return mActiveAdapter;
    }
    
    public void setDataList(List<AVObject> dataList)
    {
        this.mDataList = dataList;
    }
    
    public void notifyDataSetChanged()
    {
        mActiveAdapter.notifyDataSetChanged();
        
        mRefresh.setRefreshing(false);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        if (mRootView == null)
        {
            mRootView = inflater.inflate(R.layout.active_list_layout, null);
            
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
        mActiveList = mRootView.findViewById(R.id.active_list);
        mRefresh = mRootView.findViewById(R.id.refresh_layout);
        
        mActiveAdapter = new HomeListAdapter(getActivity());
        mActiveAdapter.setFrom("community");
        //        mActiveAdapter.setListData(initData());
        
        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mActiveList.setLayoutManager(llm);
        mActiveList.setAdapter(mActiveAdapter);
        
        mRefresh.setRefreshing(true);
    }
}
