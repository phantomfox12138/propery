package com.junjingit.propery.circle;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.junjingit.propery.HomeListAdapter;
import com.junjingit.propery.R;
import com.junjingit.propery.utils.ToastUtils;
import com.tencent.qc.stat.common.User;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.widget.GridItemDecoration;
import com.yanzhenjie.recyclerview.swipe.widget.ListItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/8/13 0013.
 */

public class FollowUserFragment extends Fragment {
    public static final String TAG = "FollowUserFragment";
    private View mRootView;
    private SwipeMenuRecyclerView follow_user_list;
    private SwipeRefreshLayout swipe_layout;
    private MyFollowListAdapter myFollowListAdapter;
    private List<AVObject> userFollowList = new ArrayList<AVObject>();
    private PinyinComparator pinyinComparator;
    private FollowBar user_bar;
    private TextView overlay;
    private OverlayThread overlayThread;
    private HashMap<String, Integer> alphaIndexer;
    private String[] sections;// 存放存在的汉语拼音首字母
    private List<UserItem> userList = new ArrayList<UserItem>();
    private Handler handler;
    WindowManager windowManager;
    private boolean mShouldScroll;
    private int mToPosition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_follow_user, null);
            swipe_layout = mRootView.findViewById(R.id.swipe_layout);
            follow_user_list = mRootView.findViewById(R.id.follow_user_list);
            user_bar = mRootView.findViewById(R.id.user_bar);

            swipe_layout.setOnRefreshListener(mRefreshListener);//刷新监听

            follow_user_list.setLayoutManager(new LinearLayoutManager(getActivity()));
            follow_user_list.addItemDecoration(new ListItemDecoration(ContextCompat.getColor(getActivity(), R.color.divider_color)));

            // follow_user_list.useDefaultLoadMore(); //使用默认的加载更多的View。
            // follow_user_list.setLoadMoreListener(mLoadMoreListener); // 加载更多的监听。

            myFollowListAdapter = new MyFollowListAdapter(getActivity(), userFollowList, userList);
            follow_user_list.setAdapter(myFollowListAdapter);
            //请求服务端数据
            initData();
        }
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        return mRootView;
    }

    /**
     * 下拉刷新
     */
    private SwipeRefreshLayout.OnRefreshListener mRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            initData();
        }
    };


    /**
     * 上拉加载更多
     */
    private SwipeMenuRecyclerView.LoadMoreListener mLoadMoreListener = new SwipeMenuRecyclerView.LoadMoreListener() {
        @Override
        public void onLoadMore() {
            // List<String> strings = getItemList(mMainAdapter.getItemCount());
            //mItemList.addAll(strings);
            // mMainAdapter.notifyItemRangeInserted(mItemList.size() - strings.size(), strings.size());
            // 数据完更多数据，一定要掉用这个方法。
            // 第一个参数：表示此次数据是否为空。
            // 第二个参数：表示是否还有更多数据。
            // mRecyclerView.loadMoreFinish(false, false);

            // 如果加载失败调用下面的方法，传入errorCode和errorMessage。
            // errorCode随便传，你自定义LoadMoreView时可以根据errorCode判断错误类型。
            // errorMessage是会显示到loadMoreView上的，用户可以看到。
            // mRecyclerView.loadMoreError(0, "请求网络失败");
        }
    };

    /**
     * 初始化控件和数据
     */
    private void initData() {
        //查询关注者
        AVQuery<AVUser> followeeQuery = AVUser.followeeQuery(AVUser.getCurrentUser().getObjectId(), AVUser.class);
        followeeQuery.findInBackground(new FindCallback<AVUser>() {
            @Override
            public void done(List<AVUser> avObjects, AVException avException) {
                if (avException == null && avObjects != null) {
                    Log.v(TAG, "################打印当前的关注的人数" + avObjects.size());
                    userFollowList.clear();
                    userFollowList.addAll(avObjects);
                    transFormPin(avObjects);

                    myFollowListAdapter.notifyDataSetChanged();
                    swipe_layout.setRefreshing(false);
                    follow_user_list.loadMoreFinish(false, true);
                } else {
                    Log.v(TAG, "############" + avException);
                }
            }
        });
    }

    private void initBar() {
        alphaIndexer = new HashMap<String, Integer>();
        handler = new Handler();
        overlayThread = new OverlayThread();
        initOverlay();

    }

    private class LetterListViewListener implements FollowBar.OnTouchingLetterChangedListener {
        @Override
        public void onTouchingLetterChanged(final String s) {
            if (alphaIndexer.get(s) != null) {
                int position = alphaIndexer.get(s);

                smoothMoveToPosition(follow_user_list, position);

                overlay.setText(sections[position]);
                overlay.setVisibility(View.VISIBLE);
                handler.removeCallbacks(overlayThread);
                // 延迟一秒后执行，让overlay为不可见
                handler.postDelayed(overlayThread, 1500);
            }
        }
    }

    // 设置overlay不可见
    private class OverlayThread implements Runnable {
        @Override
        public void run() {
            overlay.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化汉语拼音首字母弹出提示框
     */
    private void initOverlay() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        overlay = (TextView) inflater.inflate(R.layout.my_overlay, null);
        overlay.setVisibility(View.INVISIBLE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(160, 160,
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);
        windowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        windowManager.addView(overlay, lp);
    }

    /**
     * 移动到当前的位置
     *
     * @param manager
     * @param n
     */
    public static void MoveToPosition(LinearLayoutManager manager, int n) {
        manager.scrollToPositionWithOffset(n, 0);
        manager.setStackFromEnd(true);
    }

    /**
     * 滑动滚动
     *
     * @param mRecyclerView
     * @param position
     */
    private void smoothMoveToPosition(SwipeMenuRecyclerView mRecyclerView, final int position) {
        // 第一个可见位置
        int firstItem = mRecyclerView.getChildPosition(mRecyclerView.getChildAt(0));
        // 最后一个可见位置
        int lastItem = mRecyclerView.getChildPosition(mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1));

        if (position < firstItem) {
            // 如果跳转位置在第一个可见位置之前，就smoothScrollToPosition可以直接跳转
            mRecyclerView.smoothScrollToPosition(position);
        } else if (position <= lastItem) {
            // 跳转位置在第一个可见项之后，最后一个可见项之前
            // smoothScrollToPosition根本不会动，此时调用smoothScrollBy来滑动到指定位置
            int movePosition = position - firstItem;
            if (movePosition >= 0 && movePosition < mRecyclerView.getChildCount()) {
                int top = mRecyclerView.getChildAt(movePosition).getTop();
                mRecyclerView.smoothScrollBy(0, top);
            }
        } else {
            // 如果要跳转的位置在最后可见项之后，则先调用smoothScrollToPosition将要跳转的位置滚动到可见位置
            // 再通过onScrollStateChanged控制再次调用smoothMoveToPosition，执行上一个判断中的方法
            mRecyclerView.smoothScrollToPosition(position);
            mToPosition = position;
            mShouldScroll = true;
        }
    }

    /**
     * 获取用户信息的首字母
     * @param avObjects
     */
    private List<UserItem> transFormPin(List<AVUser> avObjects) {
        List<UserItem> userList=new ArrayList<UserItem>();
        if (avObjects != null){
            for (int i = 0; i < avObjects.size(); i++) {
                String pinyin;
                String userName;
                String contactSort;
                final AVObject obj = avObjects.get(i);
                if (obj.get("username") == null) {
                    userName="";
                } else {
                    userName = obj.get("username").toString();
                }
                if (userName.equals("")) {
                    pinyin = "";
                } else {
                    pinyin = HanyuToPinyin.converterToSpell(userName.substring(0, 1));
                }
                if(TextUtils.isEmpty(pinyin)){
                    contactSort="#";
                }else{
                    contactSort = pinyin.substring(0, 1).toUpperCase();
                }
                if (contactSort.equals("0") || contactSort.equals("1")
                        || contactSort.equals("2") || contactSort.equals("3")
                        || contactSort.equals("4") || contactSort.equals("5")
                        || contactSort.equals("6") || contactSort.equals("7")
                        || contactSort.equals("8") || contactSort.equals("9")) {
                    contactSort = "#";
                }
                UserItem userObject = new UserItem();
                userObject.setUserName(userName);
                userObject.setSortLetters(contactSort);
                userList.add(userObject);
            }
        }
        pinyinComparator = new PinyinComparator();
        // 根据a-z进行排序源数据
        Collections.sort(userList, pinyinComparator);
        return  userList;
    }

    private void setListAdapter(){

    }
}
