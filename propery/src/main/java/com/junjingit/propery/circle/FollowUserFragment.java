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
import com.avos.avoscloud.FollowCallback;
import com.junjingit.propery.HomeListAdapter;
import com.junjingit.propery.R;
import com.junjingit.propery.utils.ToastUtils;
import com.tencent.qc.stat.common.User;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
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
            follow_user_list.setSwipeMenuCreator(mSwipeMenuCreator);
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
                    userList = transFormPin(avObjects);
                    myFollowListAdapter = new MyFollowListAdapter(getActivity(), userFollowList, userList);
                    initBar();
                    initAlpSecData();
                    cancelFoucs();//取消关注
                    follow_user_list.setAdapter(myFollowListAdapter);
                    myFollowListAdapter.notifyDataSetChanged();
                    swipe_layout.setRefreshing(false);
                    follow_user_list.loadMoreFinish(false, true);
                } else {
                    Log.v(TAG, "############" + avException);
                }
            }
        });
    }


    private void cancelFoucs() {
        follow_user_list.setSwipeMenuItemClickListener(new SwipeMenuItemClickListener() {
            @Override
            public void onItemClick(SwipeMenuBridge menuBridge) {
                menuBridge.closeMenu();
                int direction = menuBridge.getDirection();// 左侧还是右侧菜单。
                final int adapterPosition = menuBridge.getAdapterPosition();// RecyclerView的Item的position。
                if (direction == SwipeMenuRecyclerView.RIGHT_DIRECTION) {
                    AVUser.getCurrentUser()
                            .unfollowInBackground(userList.get(adapterPosition).getObjectId(),
                                    new FollowCallback() {
                                        @Override
                                        public void done(AVObject avObject,
                                                         AVException e) {
                                            if (null == e) {
                                                userList.remove(adapterPosition);
                                                myFollowListAdapter.notifyDataSetChanged();
                                                ToastUtils.showToast(getActivity(), "取消关注成功!");
                                            }
                                        }
                                    });
                }
            }
        });
    }

    /**
     * 初始化侧边栏检索Bar
     */
    private void initBar() {
        user_bar.setOnTouchingLetterChangedListener(new LetterListViewListener());
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
        for (int i = 0; i < userList.size(); i++) {
            // 当前汉语拼音首字母
            String currentStr = userList.get(i).getSortLetters().toString();
            // 上一个汉语拼音首字母，如果不存在为" "
            String previewStr = (i - 1) >= 0 ? userList.get(i - 1).getSortLetters().toString() : " ";
            if (!previewStr.equals(currentStr)) {
                String name = userList.get(i).getSortLetters().toString();
                Log.v(TAG, "#########initAlpSecData" + name);
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

    /**
     * 获取用户信息的首字母
     *
     * @param avObjects
     */
    private List<UserItem> transFormPin(List<AVUser> avObjects) {
        List<UserItem> userList = new ArrayList<UserItem>();
        if (avObjects != null) {
            for (int i = 0; i < 5; i++) {
                String pinyin;
                String nickname;
                String objectId;
                String user_img;
                String contactSort;
                final AVObject obj = avObjects.get(0);
                if (obj.get("nickname") == null) {
                    nickname = "暂无昵称";
                } else {
                    nickname = obj.get("nickname").toString();
                }
                if (obj.get("objectId") == null) {
                    objectId = "";
                } else {
                    objectId = obj.get("objectId").toString();
                }

                if (obj.get("user_icon_url") == null) {
                    user_img = "";
                } else {
                    user_img = obj.get("user_icon_url").toString();
                }

                if (nickname.equals("")) {
                    pinyin = "";
                } else {
                    Log.v(TAG, "###################打印当前的首字母" + nickname.substring(0, 1));
                    pinyin = HanyuToPinyin.converterToSpell(nickname.substring(0, 1));
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
                userObject.setUserName(nickname);
                userObject.setSortLetters(contactSort);
                userObject.setUser_img(user_img);
                userObject.setObjectId(objectId);
                userList.add(userObject);
            }
        }
        pinyinComparator = new PinyinComparator();
        // 根据a-z进行排序源数据
        Collections.sort(userList, pinyinComparator);
        return userList;
    }


    //创建菜单
    /**
     * 菜单创建器，在Item要创建菜单的时候调用。
     */
    private SwipeMenuCreator mSwipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu,
                                 SwipeMenu swipeRightMenu, int viewType) {
            int width = getResources().getDimensionPixelSize(R.dimen.dp_96);
            Log.v(TAG, "######################打印当前的菜单" + viewType);
            // 1. MATCH_PARENT 自适应高度，保持和Item一样高;
            // 2. 指定具体的高，比如80;
            // 3. WRAP_CONTENT，自身高度，不推荐;
            int height = getResources().getDimensionPixelSize(R.dimen.dp_64);
            SwipeMenuItem closeItem = new SwipeMenuItem(getActivity()).setBackground(android.R.color.holo_red_light)
                    .setWidth(width)
                    .setText("取消关注")
                    .setTextColor(getActivity().getResources()
                            .getColor(R.color.white))
                    .setHeight(height);

            // 添加菜单到右侧。
            swipeRightMenu.addMenuItem(closeItem);
        }
    };
}
