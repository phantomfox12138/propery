package com.junjingit.propery.circle;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.junjingit.propery.R;

/**
 * Created by Administrator on 2017/8/13 0013.
 */

public class FollowUserFragment extends Fragment {
    public static final String TAG ="FollowUserFragment";
    private View mRootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(mRootView==null){
            mRootView = inflater.inflate(R.layout.fragment_follow_user,null);
        }
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null)
        {
            parent.removeView(mRootView);
        }
        return mRootView;
    }

    /**
     * 初始化控件和数据
     */
    private void initView(){

    }
}
