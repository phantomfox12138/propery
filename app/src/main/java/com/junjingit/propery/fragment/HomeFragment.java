package com.junjingit.propery.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.junjingit.propery.HomeListAdapter;
import com.junjingit.propery.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment
{
    private View mRootView;
    
    private RecyclerView mHomeList;
    
    private Toolbar mToolbarTb;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        mRootView = inflater.inflate(R.layout.fragment_home, container, false);
        
        return mRootView;
    }
    
    private void initView()
    {
        mToolbarTb = mRootView.findViewById(R.id.tb_toolbar);
        mHomeList = mRootView.findViewById(R.id.rv_content);
        
        if (mToolbarTb != null)
        {
            mToolbarTb.setNavigationIcon(R.mipmap.ic_launcher_round);
            ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbarTb);
            
            ((AppCompatActivity) getActivity()).getSupportActionBar()
                    .setHomeButtonEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar()
                    .setDisplayHomeAsUpEnabled(true);
        }
        
        HomeListAdapter adapter = new HomeListAdapter(getActivity());
        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mHomeList.setLayoutManager(llm);
        
        mHomeList.setAdapter(adapter);
    }
    
}
