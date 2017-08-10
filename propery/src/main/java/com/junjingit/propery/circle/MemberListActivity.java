package com.junjingit.propery.circle;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ViewGroup;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.junjingit.propery.R;
import com.junjingit.propery.utils.ToastUtils;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jxy on 2017/8/10.
 */

public class MemberListActivity extends AppCompatActivity
{
    private static String TAG = "MemberListActivity";
    
    private Toolbar title_bar;
    
    private SwipeRefreshLayout refresh_layout;
    
    private SwipeMenuRecyclerView mycircle_list;
    
    private CircleListAdapter circleListAdapter;
    
    private List<AVObject> mDataList = new ArrayList<>();
    
    private Context mContext;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycircle_list);
        mContext = MemberListActivity.this;
        initView();
        initData();
    }
    
    private void initView()
    {
        refresh_layout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        mycircle_list = (SwipeMenuRecyclerView) findViewById(R.id.mycircle_list);
        title_bar = (Toolbar) findViewById(R.id.title_bar);
        setSupportActionBar(title_bar);
        getSupportActionBar().setTitle(getString(R.string.my_member));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        circleListAdapter = new CircleListAdapter(this);
        circleListAdapter.setmFrom("myMember");
        circleListAdapter.setmListData(mDataList);
        mycircle_list.setLayoutManager(new LinearLayoutManager(this));
        mycircle_list.setAdapter(circleListAdapter);
        mycircle_list.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
        mycircle_list.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        
        // 设置菜单创建器。
        mycircle_list.setSwipeMenuCreator(swipeMenuCreator);
        mycircle_list.setSwipeMenuItemClickListener(mMenuItemClickListener);
    }
    
    private void initData()
    {
        String userObjectId = AVUser.getCurrentUser().getObjectId();
        AVQuery<AVObject> query = new AVQuery<>("cycle");
        query.orderByAscending("createdAt");
        query.whereContains("cycle_created_by", userObjectId);
        query.findInBackground(new FindCallback<AVObject>()
        {
            @Override
            public void done(List<AVObject> list, AVException e)
            {
                if (e == null)
                {
                    mDataList.clear();
                    mDataList.addAll(list);
                    circleListAdapter.notifyDataSetChanged();
                    ToastUtils.showToast(MemberListActivity.this, "查询当前的圈子的数量"
                            + list.size());
                }
                else
                {
                    Log.v(TAG, "##################" + e);
                }
            }
        });
    }
    
    /**
     * 菜单创建器。在Item要创建菜单的时候调用。
     */
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator()
    {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu,
                SwipeMenu swipeRightMenu, int viewType)
        {
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
    private SwipeMenuItemClickListener mMenuItemClickListener = new SwipeMenuItemClickListener()
    {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge)
        {
            menuBridge.closeMenu();
            int direction = menuBridge.getDirection(); // 左侧还是右侧菜单。
            int adapterPosition = menuBridge.getAdapterPosition();//RecyclerView的Item的position。
            int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。
            if (direction == SwipeMenuRecyclerView.RIGHT_DIRECTION)
            {
            }
            else if (direction == SwipeMenuRecyclerView.LEFT_DIRECTION)
            {
            }
        }
    };
    
    private void getMember(AVObject circle)
    {
        AVQuery<AVUser> query = new AVQuery<>("_User");
        
        query.whereEqualTo("cycle", circle);
        query.findInBackground(new FindCallback<AVUser>()
        {
            @Override
            public void done(List<AVUser> list, AVException e)
            {
                if (null == e)
                {
                    //list为关注该圈子的所有成员
                }
            }
        });
    }
}
