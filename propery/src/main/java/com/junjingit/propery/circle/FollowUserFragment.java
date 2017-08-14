package com.junjingit.propery.circle;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.junjingit.propery.HomeListAdapter;
import com.junjingit.propery.R;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

/**
 * Created by Administrator on 2017/8/13 0013.
 */

public class FollowUserFragment extends Fragment {
    public static final String TAG = "FollowUserFragment";
    private View mRootView;
    private SwipeMenuRecyclerView follow_user_list;
    private SwipeRefreshLayout swipe_layout;
    private MyFollowListAdapter myFollowListAdapter;

    public MyFollowListAdapter getActiveAdapter() {
        return myFollowListAdapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_follow_user, null);
            follow_user_list = mRootView.findViewById(R.id.follow_user_list);
            swipe_layout = mRootView.findViewById(R.id.swipe_layout);
            initData();
        }
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        return mRootView;
    }

    /**
     * 初始化控件和数据
     */
    private void initData() {
        myFollowListAdapter = new MyFollowListAdapter(getActivity());
        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        follow_user_list.setLayoutManager(llm);
        follow_user_list.setAdapter(myFollowListAdapter);
        swipe_layout.setRefreshing(false);
    }
    public void notifyDataSetChanged() {
        myFollowListAdapter.notifyDataSetChanged();
        swipe_layout.setRefreshing(false);
        // 第一次加载数据：一定要调用这个方法，否则不会触发加载更多。
        // 第一个参数：表示此次数据是否为空，假如你请求到的list为空(== null || list.size == 0)，那么这里就要true。
        // 第二个参数：表示是否还有更多数据，根据服务器返回给你的page等信息判断是否还有更多，这样可以提供性能，如果不能判断则传true。
        follow_user_list.loadMoreFinish(false, true);
    }
}
