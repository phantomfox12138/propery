package com.junjingit.propery.circle;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
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

public class MemberListActivity extends AppCompatActivity {
    private static String TAG = "MemberListActivity";

    private Toolbar title_bar;

    private SwipeRefreshLayout refresh_layout;

    private SwipeMenuRecyclerView mycircle_list;

    private MemberListAdapter memberListAdapter;

    private List<AVObject> mDataList = new ArrayList<>();

    private Context mContext;
    private String circleId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mymember_list);
        mContext = MemberListActivity.this;
        circleId = getIntent().getStringExtra(FusionAction.CircleListExtra.CIRCLE_ID);
        initView();
        initData();
        AVQuery<AVObject> circleQuery = new AVQuery<>("cycle");
        circleQuery.getInBackground(circleId, new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                getMember(avObject);
            }
        });
    }

    private void initView() {
        refresh_layout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        mycircle_list = (SwipeMenuRecyclerView) findViewById(R.id.mycircle_list);
        title_bar = (Toolbar) findViewById(R.id.title_bar);
        setSupportActionBar(title_bar);
        getSupportActionBar().setTitle(getString(R.string.my_member));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        memberListAdapter = new MemberListAdapter(this);
        memberListAdapter.setmFrom("myMember");
        memberListAdapter.setmListData(mDataList);
        mycircle_list.setLayoutManager(new LinearLayoutManager(this));
        mycircle_list.setAdapter(memberListAdapter);
        mycircle_list.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
        mycircle_list.addItemDecoration(getItemDecoration());

        // 设置菜单创建器。
        mycircle_list.setSwipeMenuCreator(swipeMenuCreator);
        mycircle_list.setSwipeMenuItemClickListener(mMenuItemClickListener);
    }

    private void initData() {

    }

    /**
     * 菜单创建器。在Item要创建菜单的时候调用。
     */
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu,
                                 SwipeMenu swipeRightMenu, int viewType) {
            int width = getResources().getDimensionPixelSize(R.dimen.dp_96);
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            SwipeMenuItem addItem1 = new SwipeMenuItem(mContext).setBackground(android.R.color.holo_green_dark)
                    // 点击的背景。
                    .setText("禁言")
                    .setTextSize(16)
                    .setTextColor(getResources().getColor(R.color.white))
                    .setWidth(width)
                    // 宽度。
                    .setHeight(height); // 高度。
            SwipeMenuItem addItem2 = new SwipeMenuItem(mContext).setBackground(android.R.color.holo_red_light)
                    // 点击的背景。
                    .setText("删除")
                    .setTextSize(16)
                    .setTextColor(getResources().getColor(R.color.white))
                    .setWidth(width)
                    // 宽度。
                    .setHeight(height); // 高度。
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
                if(menuPosition==0){//禁言
                   // mDataList.get(adapterPosition).getObjectId()
                }else if(menuPosition==1){

                }
            }
        }
    };

    private void getMember(AVObject avObject) {
        AVQuery<AVUser> query = new AVQuery<>("_User");
        query.whereEqualTo("cycle", avObject);
        query.findInBackground(new FindCallback<AVUser>() {
            @Override
            public void done(List<AVUser> list, AVException e) {
                if (e == null)
                {
                    mDataList.clear();
                    mDataList.addAll(list);
                    memberListAdapter.notifyDataSetChanged();
                }
                else
                {
                    Log.v(TAG, "##################" + e);
                }
            }
        });
    }

    /**
     * 获取RecyclerView的Item分割线。
     */
    protected RecyclerView.ItemDecoration getItemDecoration() {
        RecyclerView.LayoutManager layoutManager = mycircle_list.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            return new GridItemDecoration(ContextCompat.getColor(this, R.color.divider_color));
        } else {
            return new ListItemDecoration(ContextCompat.getColor(this, R.color.divider_color));
        }
    }

    /**
     * 禁言
     */
    private void forbidTalk(String reminder1){

        AVObject todo = new AVObject("cycle");
        todo.addAllUnique("slient", Arrays.asList(reminder1));
        todo.saveInBackground();
    }
    /**
     * 删除成员
     */
    private void myMemberDelete(){

    }

}
