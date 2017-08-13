package com.junjingit.propery.circle;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.junjingit.propery.R;

import static com.tencent.qc.stat.StatReportStrategy.f;

/**
 * Created by Administrator on 2017/8/13 0013.
 */

public class FollowCircleFragment extends Fragment {
    public static final String TAG ="FollowCircleFragment";
    private RecyclerView circleList;
    private SwipeRefreshLayout mRefresh;
    private View mRootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(mRootView==null){
            mRootView = inflater.inflate(R.layout.fragment_follow_circle,null);
            initView();
        }
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null)
        {
            parent.removeView(mRootView);
        }
        return mRootView;
    }

    /**
     * 初始化操作
     */
    private void initView(){
        circleList=mRootView.findViewById(R.id.follow_circle_list);
        mRefresh=mRootView.findViewById(R.id.refresh_layout);
    }
}
