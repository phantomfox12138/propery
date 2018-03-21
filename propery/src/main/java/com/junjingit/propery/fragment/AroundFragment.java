package com.junjingit.propery.fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.junjingit.propery.ClassifyMainAdapter;
import com.junjingit.propery.ClassifyMoreAdapter;
import com.junjingit.propery.R;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

/**
 * A simple {@link Fragment} subclass.
 */
public class AroundFragment extends Fragment
{
    
    private View mRootView;
    
    private SwipeMenuRecyclerView mAroundList;
    
    private Toolbar mToolbar;
    
    private SwipeRefreshLayout mRefreshLayout;
    
    private String headers[] = { "综合排序" };
    
    private String order[] = { "综合排序", "销量最好" };
    
    private ListView mainlist;
    
    private ListView morelist;
    
    private List<Map<String, Object>> mainList;
    
    private ClassifyMainAdapter mainAdapter;
    
    private ClassifyMoreAdapter moreAdapter;
    
    private View mMenuView;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        if (mRootView == null)
        {
            mRootView = inflater.inflate(R.layout.fragment_around, null);
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
        mToolbar = mRootView.findViewById(R.id.toolbar);
        mAroundList = mRootView.findViewById(R.id.around_list);
        mRefreshLayout = mRootView.findViewById(R.id.refresh_layout);
        mMenuView = LayoutInflater.from(getActivity())
                .inflate(R.layout.around_category_layout, null);
        
        //        initCategoryMenu();
        
    }

    //    private void initCategoryMenu()
    //    {
    //        mainList = new ArrayList<Map<String, Object>>();
    //        for (int i = 0; i < Model.LISTVIEWIMG.length; i++)
    //        {
    //            Map<String, Object> map = new HashMap<String, Object>();
    //            map.put("img", Model.LISTVIEWIMG[i]);
    //            map.put("txt", Model.LISTVIEWTXT[i]);
    //            mainList.add(map);
    //        }
    //
    //        mainlist = (ListView) mMenuView.findViewById(R.id.classify_mainlist);
    //        morelist = (ListView) mMenuView.findViewById(R.id.classify_morelist);
    //        mainAdapter = new ClassifyMainAdapter(getActivity(), mainList);
    //        mainAdapter.setSelectItem(0);
    //        mainlist.setAdapter(mainAdapter);
    //
    //        mainlist.setOnItemClickListener(new AdapterView.OnItemClickListener()
    //        {
    //
    //            @Override
    //            public void onItemClick(AdapterView<?> parent, View view,
    //                    int position, long id)
    //            {
    //                initAdapter(Model.MORELISTTXT[position]);
    //                mainAdapter.setSelectItem(position);
    //                mainAdapter.notifyDataSetChanged();
    //            }
    //        });
    //        mainlist.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    //        // 一定要设置这个属性，否则ListView不会刷新
    //        initAdapter(Model.MORELISTTXT[0]);
    //
    //        morelist.setOnItemClickListener(new AdapterView.OnItemClickListener()
    //        {
    //
    //            @Override
    //            public void onItemClick(AdapterView<?> parent, View view,
    //                    int position, long id)
    //            {
    //                moreAdapter.setSelectItem(position);
    //                moreAdapter.notifyDataSetChanged();
    //            }
    //        });
    //
    //    }
    
    private void initAdapter(String[] array)
    {
        moreAdapter = new ClassifyMoreAdapter(getActivity(), array);
        morelist.setAdapter(moreAdapter);
        moreAdapter.notifyDataSetChanged();
    }
    
}
