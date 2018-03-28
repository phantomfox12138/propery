package com.junjingit.propery.circle;

import android.content.Context;
import android.graphics.PixelFormat;
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
import com.junjingit.propery.R;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.widget.ListItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/8/13 0013.
 */

public class FollowCircleFragment extends Fragment {
    public static final String TAG = "FollowCircleFragment";
    private SwipeMenuRecyclerView follow_circle_list;
    private SwipeRefreshLayout swipe_layout;
    private View mRootView;
    private List<AVObject> myFollowCircle = new ArrayList<>();
    MyFollowListAdapter myFollowListAdapter;
    private PinyinComparator pinyinComparator;
    private FollowBar circle_bar;
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
            mRootView = inflater.inflate(R.layout.fragment_follow_circle, null);
            follow_circle_list = mRootView.findViewById(R.id.follow_circle_list);
            swipe_layout = mRootView.findViewById(R.id.swipe_layout);
            circle_bar=mRootView.findViewById(R.id.circle_bar);

            swipe_layout.setOnRefreshListener(mRefreshListener);//刷新监听

            follow_circle_list.setLayoutManager(new LinearLayoutManager(getActivity()));
            follow_circle_list.addItemDecoration(new ListItemDecoration(ContextCompat.getColor(getActivity(), R.color.divider_color)));

            // follow_user_list.useDefaultLoadMore(); //使用默认的加载更多的View。
            // follow_user_list.setLoadMoreListener(mLoadMoreListener); // 加载更多的监听。
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
     * 查询我所关注的圈子
     */
    private void initData() {
        String userObjectId = AVUser.getCurrentUser().getObjectId();
        AVQuery<AVObject> query = new AVQuery<>("cycle");
        query.orderByAscending("createdAt");
        query.whereContains("cycle_created_by", userObjectId);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null&&list!=null) {
                    Log.v(TAG,"################打印当前的关注圈子数"+list.size());
                    myFollowCircle.clear();
                    myFollowCircle.addAll(list);
                    userList =transFormPin(list);
                    myFollowListAdapter = new MyFollowListAdapter(getActivity(),myFollowCircle,userList);
                    initBar();
                    initAlpSecData();
                    follow_circle_list.setAdapter(myFollowListAdapter);
                    myFollowListAdapter.notifyDataSetChanged();
                    swipe_layout.setRefreshing(false);
                    follow_circle_list.loadMoreFinish(false, true);
                } else {
                    Log.v(TAG, "##################" + e);
                }
            }
        });
    }
    /**
     * 获取圈子信息的首字母
     *
     * @param avObjects
     */
    private List<UserItem> transFormPin(List<AVObject> avObjects) {
        List<UserItem> userList = new ArrayList<UserItem>();
        if (avObjects != null) {
            for (int i = 0; i <avObjects.size(); i++) {
                String pinyin;
                String userName;
                String circleImg;
                String contactSort;
                String circleNum;
                String objectId;
                final AVObject obj = avObjects.get(i);
                if (obj.get("cycle_name") == null) {
                    userName = "";
                } else {
                    userName = obj.get("cycle_name").toString();
                }
                if (obj.get("cycle_icon_url") == null) {
                    circleImg = "";
                } else {
                    circleImg = obj.get("cycle_icon_url").toString();
                }
                if (obj.get("focus_count") == null) {
                    circleNum = "";
                } else {
                    circleNum = obj.get("focus_count").toString();
                }

                if (obj.get("objectId") == null) {
                    objectId = "";
                } else {
                    objectId = obj.get("objectId").toString();
                }
                if (userName.equals("")) {
                    pinyin = "";
                } else {
                    Log.v(TAG, "###################打印当前的首字母" + userName.substring(0, 1));
                    pinyin = HanyuToPinyin.converterToSpell(userName.substring(0, 1));
                    Log.v(TAG, "###################打印当前的pinyin" + pinyin);
                }
                if (TextUtils.isEmpty(pinyin)) {
                    contactSort = "#";
                } else {
                    contactSort = pinyin.substring(0, 1).toUpperCase();
                    Log.v(TAG, "###################打印当前的contactSort" + pinyin);
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
                userObject.setFocus_num(circleNum);
                userObject.setUser_img(circleImg);
                userObject.setObjectId(objectId);
                userList.add(userObject);
            }
        }
        pinyinComparator = new PinyinComparator();
        // 根据a-z进行排序源数据
        Collections.sort(userList, pinyinComparator);
        return userList;
    }
    /**
     * 初始化侧边栏检索Bar
     */
    private void initBar() {
        circle_bar.setOnTouchingLetterChangedListener(new LetterListViewListener());
        alphaIndexer = new HashMap<String, Integer>();
        handler = new Handler();
        overlayThread = new OverlayThread();
        initOverlay();
    }


    /**
     * 初始化侧边栏提示框首字母
     */
    private void initAlpSecData() {
        sections = new String[userList.size()];
        for (int i = 0; i < userList.size(); i++){
            // 当前汉语拼音首字母
            String currentStr = userList.get(i).getSortLetters().toString();
            // 上一个汉语拼音首字母，如果不存在为" "
            String previewStr = (i - 1) >= 0 ? userList.get(i - 1).getSortLetters().toString() : " ";
            if (!previewStr.equals(currentStr)) {
                String name = userList.get(i).getSortLetters().toString();
                Log.v(TAG,"#########initAlpSecData"+name);
                alphaIndexer.put(name, i);
                sections[i] = name;
            }
        }
    }

    private class LetterListViewListener implements FollowBar.OnTouchingLetterChangedListener {
        @Override
        public void onTouchingLetterChanged(final String s) {
            if (alphaIndexer.get(s) != null) {
                int position = alphaIndexer.get(s);
                smoothMoveToPosition(follow_circle_list, position);
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
        overlay.getBackground().setAlpha(80);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(180, 180,
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(overlay!=null){
            windowManager.removeView(overlay);
        }
    }
}
