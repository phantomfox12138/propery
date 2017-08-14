package com.junjingit.propery.circle;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ViewGroup;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.junjingit.propery.R;
import com.junjingit.propery.common.FusionAction;
import com.junjingit.propery.utils.ToastUtils;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.widget.GridItemDecoration;
import com.yanzhenjie.recyclerview.swipe.widget.ListItemDecoration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jxy on 2017/8/10.
 */

public class MemberListActivity extends AppCompatActivity implements
        MemberListAdapter.IClickListener {
    private static String TAG = "MemberListActivity";

    private Toolbar title_bar;

    private SwipeRefreshLayout refresh_layout;

    private SwipeMenuRecyclerView mycircle_list;

    private MemberListAdapter memberListAdapter;

    private List<AVObject> mDataList = new ArrayList<>();

    private List<AVObject> memberDataList = new ArrayList<>();

    private Context mContext;

    private String circleId;

    private ArrayList<String> slientList;

    public int menuType = 0;//表示禁言

    public int itemPosition = 0;

    public AVObject memberAvObject;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mymember_list);
        mContext = MemberListActivity.this;
        circleId = getIntent().getStringExtra(FusionAction.CircleListExtra.CIRCLE_ID);
        initView();
        initData();
    }

    private void initView() {
        refresh_layout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        mycircle_list = (SwipeMenuRecyclerView) findViewById(R.id.member_list);
        title_bar = (Toolbar) findViewById(R.id.title_bar);
        setSupportActionBar(title_bar);
        getSupportActionBar().setTitle(getString(R.string.my_member));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        refresh_layout.setOnRefreshListener(mRefreshListener); // 刷新监听。
    }

    private void initData() {
        AVQuery<AVObject> circleQuery = new AVQuery<>("cycle");
        circleQuery.getInBackground(circleId, new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                if (avObject.get("slient") == null) {
                    slientList = new ArrayList<String>();
                } else {
                    slientList = (ArrayList<String>) avObject.get("slient");

                }
                memberAvObject = avObject;
                getMember(avObject, slientList);
            }
        });
    }

    /**
     * 刷新。
     */
    private SwipeRefreshLayout.OnRefreshListener mRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            mycircle_list.postDelayed(new Runnable() {
                @Override
                public void run() {
                    initData();
                }
            }, 1000);
        }
    };

    /**
     * 菜单创建器。在Item要创建菜单的时候调用。
     */
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu,
                                 SwipeMenu swipeRightMenu, int viewType) {
            AVObject obj = mDataList.get(itemPosition);
            String currentMemberId = obj.getObjectId().toString();//得到当前成员的id
            if (slientList != null) {
                for (int i = 0; i < slientList.size(); i++) {
                    String currentValue = slientList.get(i).toString();
                    if (currentMemberId.equals(currentValue)) {
                        menuType = 1;//解除禁言
                        break;
                    }
                }
            }
            SwipeMenuItem addItem1 = null;
            Log.v(TAG, "onCreateMenu" + menuType);
            int width = getResources().getDimensionPixelSize(R.dimen.dp_96);
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            if (menuType == 0) {//禁言
                addItem1 = new SwipeMenuItem(mContext).setBackground(android.R.color.holo_green_dark)
                        // 点击的背景。
                        .setText("禁言")
                        .setTextSize(16)
                        .setTextColor(getResources().getColor(R.color.white))
                        .setWidth(width)
                        .setHeight(height);
            } else if (menuType == 1) {//解除禁言
                addItem1 = new SwipeMenuItem(mContext).setBackground(android.R.color.holo_orange_light)
                        // 点击的背景。
                        .setText("解除禁言")
                        .setTextSize(16)
                        .setTextColor(getResources().getColor(R.color.white))
                        .setWidth(width)
                        .setHeight(height);
            }
            SwipeMenuItem addItem2 = new SwipeMenuItem(mContext).setBackground(android.R.color.holo_red_light)
                    .setText("删除")
                    .setTextSize(16)
                    .setTextColor(getResources().getColor(R.color.white))
                    .setWidth(width)
                    .setHeight(height);
            swipeRightMenu.addMenuItem(addItem1); // 添加一个按钮到右侧侧菜单
            swipeRightMenu.addMenuItem(addItem2);
        }

    };

    /**
     * RecyclerView的Item的Menu点击监听。
     */
    private SwipeMenuItemClickListener mMenuItemClickListener = new SwipeMenuItemClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge) {
            menuBridge.closeMenu();
            int direction = menuBridge.getDirection(); // 左侧还是右侧菜单。
            int adapterPosition = menuBridge.getAdapterPosition();//RecyclerView的Item的position。
            int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。
            if (direction == SwipeMenuRecyclerView.RIGHT_DIRECTION) {
                Log.v(TAG, "##############打印当前圈子的Id" + circleId);
                Log.v(TAG,
                        "##############打印当前成员的Id"
                                + mDataList.get(adapterPosition).getObjectId());
                if (menuPosition == 0) {//禁言
                    if (menuType == 0) {
                        forbidTalk(circleId, mDataList.get(adapterPosition)
                                .getObjectId());
                    } else if (menuType == 1) {//解除禁言
                        reMoveforbid(circleId, mDataList.get(adapterPosition)
                                .getObjectId(), adapterPosition);
                    }
                } else if (menuPosition == 1) {
                    myMemberDelete(mDataList.get(adapterPosition).getObjectId(), adapterPosition);
                }
            }
        }
    };

    private void getMember(AVObject avObject, ArrayList<String> silentList) {
        memberListAdapter = new MemberListAdapter(this);
        memberListAdapter.setmFrom("myMember");
        memberListAdapter.setmListData(mDataList, silentList);
        mycircle_list.setLayoutManager(new LinearLayoutManager(this));
        memberListAdapter.setIClickListener(this);
        mycircle_list.setAdapter(memberListAdapter);
        mycircle_list.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
        mycircle_list.addItemDecoration(getItemDecoration());

        // 设置菜单创建器。
        mycircle_list.setSwipeMenuCreator(swipeMenuCreator);
        mycircle_list.setSwipeMenuItemClickListener(mMenuItemClickListener);

        AVQuery<AVUser> query = new AVQuery<>("_User");
        query.whereEqualTo("cycle", avObject);
        query.findInBackground(new FindCallback<AVUser>() {
            @Override
            public void done(List<AVUser> list, AVException e) {
                if (e == null) {
                    mDataList.clear();
                    mDataList.addAll(list);
                    memberListAdapter.notifyDataSetChanged();
                } else {
                    Log.v(TAG, "##################" + e);
                }
            }
        });
        refresh_layout.setRefreshing(false);

        // 第一次加载数据：一定要调用这个方法，否则不会触发加载更多。
        // 第一个参数：表示此次数据是否为空，假如你请求到的list为空(== null || list.size == 0)，那么这里就要true。
        // 第二个参数：表示是否还有更多数据，根据服务器返回给你的page等信息判断是否还有更多，这样可以提供性能，如果不能判断则传true。
        mycircle_list.loadMoreFinish(false, true);
    }

    /**
     * 获取RecyclerView的Item分割线。
     */
    protected RecyclerView.ItemDecoration getItemDecoration() {
        RecyclerView.LayoutManager layoutManager = mycircle_list.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            return new GridItemDecoration(ContextCompat.getColor(this,
                    R.color.divider_color));
        } else {
            return new ListItemDecoration(ContextCompat.getColor(this,
                    R.color.divider_color));
        }
    }

    /**
     * 当前圈子的成员,进行禁言
     *
     * @param circleId
     * @param memberObjectId
     */
    private void forbidTalk(final String circleId, final String memberObjectId) {
        AVQuery<AVObject> avQuery = new AVQuery<>("cycle");
        avQuery.getInBackground(circleId, new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                if (e == null) {
                    AVObject todo = avObject.createWithoutData("cycle",
                            circleId);
                    todo.addAllUnique("slient", Arrays.asList(memberObjectId));
                    // 保存到云端
                    todo.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                ToastUtils.showToast(MemberListActivity.this,
                                        "禁言成功");
                                //刷新列表更改状态[文字改变]
                                initData();
                            } else {
                                ToastUtils.showToast(MemberListActivity.this,
                                        "禁言失败");
                            }
                        }
                    });
                } else {
                }
            }
        });
    }

    /**
     * 更新列表数据
     *
     * @param position
     */
    public void removeData(int position) {
        mDataList.remove(position);
        memberListAdapter.notifyDataSetChanged();
    }

    /**
     * 解除禁言,只要把当前用户的objectId,从slient数组里面移除就可以
     *
     * @param circleId
     * @param memberObjectId
     */
    public void reMoveforbid(final String circleId,
                             final String memberObjectId, final int removePosition) {
        AVObject obj = mDataList.get(removePosition);
        String currentMemberId = obj.getObjectId().toString();//得到当前成员的id
        if (slientList != null) {
            for (int i = 0; i < slientList.size(); i++) {
                String currentValue = slientList.get(i).toString();
                if (currentMemberId.equals(currentValue)) {
                    menuType = 0;//禁言
                    slientList.remove(i);
                    --i;
                    break;
                }
            }
            AVObject todo = AVObject.createWithoutData("cycle", circleId);
            if (slientList != null) {
                for (int i = 0; i < slientList.size(); i++) {
                    String test = slientList.get(i).toString();
                    todo.addAllUnique("slient", Arrays.asList(test));
                }
            } else {
                todo.put("slient", null);
            }
            // 保存到云端
            todo.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null) {
                        ToastUtils.showToast(MemberListActivity.this, "解除禁言成功");
                        //刷新列表更改状态[文字改变]
                        initData();
                    } else {
                        ToastUtils.showToast(MemberListActivity.this, "解除禁言失败");
                    }
                }
            });
            Log.v(TAG, "####################slientList跳出循环" + slientList);
        } else if (slientList == null) {
            Log.v(TAG, "####################slientList为空值" + slientList);
        }
    }

    @Override
    public void changeMenuType(int type) {
        itemPosition = type;
    }

    /**
     * 删除成员
     *
     * @param userObjectId
     * @param memberPosition
     */
    public void myMemberDelete(String userObjectId, final int memberPosition) {
        Log.v(TAG, "deleteMember#################打印当前的" + userObjectId);
        //根据userObjectId去获取当前用户的对象
        final AVQuery<AVUser> avQuery = new AVQuery<>("_User");
        avQuery.getFirstInBackground(new GetCallback<AVUser>() {
            @Override
            public void done(AVUser avUser, AVException e) {
                AVObject cycleObject = (AVObject) avUser.get("cycle");
                removeMember(avUser, cycleObject);
            }
        });
    }

    private void removeMember(AVUser user, AVObject cycle) {
        AVRelation<AVObject> relation = user.getRelation("cycle");
        relation.remove(cycle);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (null == e) {
                    ToastUtils.showToast(mContext, "该成员删除成功!");

                } else {
                    ToastUtils.showToast(mContext, "该成员删除失败!");
                }
            }
        });
    }

}
