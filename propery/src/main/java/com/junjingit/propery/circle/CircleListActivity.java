package com.junjingit.propery.circle;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.avos.avoscloud.AVCloudQueryResult;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CloudQueryCallback;
import com.avos.avoscloud.FindCallback;
import com.junjingit.propery.HomeListAdapter;
import com.junjingit.propery.R;
import com.junjingit.propery.common.FusionAction;
import com.junjingit.propery.utils.ToastUtils;
import com.ns.developer.tagview.entity.Tag;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.widget.GridItemDecoration;
import com.yanzhenjie.recyclerview.swipe.widget.ListItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jxy on 2017/8/10.
 */

public class CircleListActivity extends AppCompatActivity {
    private static String TAG = "CircleListActivity";
    private Toolbar title_bar;
    private SwipeRefreshLayout refresh_layout;
    private SwipeMenuRecyclerView mycircle_list;
    private CircleListAdapter circleListAdapter;
    private List<AVObject> mDataList = new ArrayList<>();
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycircle_list);
        mContext = CircleListActivity.this;
        initView();
        initData();

    }

    private void initView() {
        refresh_layout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        mycircle_list = (SwipeMenuRecyclerView) findViewById(R.id.mycircle_list);
        title_bar = (Toolbar) findViewById(R.id.title_bar);
        setSupportActionBar(title_bar);
        getSupportActionBar().setTitle(getString(R.string.my_circle));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        circleListAdapter = new CircleListAdapter(this);
        circleListAdapter.setmFrom("myCircle");
        circleListAdapter.setmListData(mDataList);
        mycircle_list.setLayoutManager(new LinearLayoutManager(this));
        mycircle_list.setAdapter(circleListAdapter);
        mycircle_list.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
        mycircle_list.addItemDecoration(getItemDecoration());

        // 设置菜单创建器。
        mycircle_list.setSwipeMenuCreator(swipeMenuCreator);
        mycircle_list.setSwipeMenuItemClickListener(mMenuItemClickListener);
    }

    private void initData() {
        String userObjectId= AVUser.getCurrentUser().getObjectId();
        AVQuery<AVObject> query = new AVQuery<>("cycle");
        query.orderByAscending("createdAt");
        //query.whereContains("cycle_created_by",userObjectId);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    mDataList.clear();
                    mDataList.addAll(list);
                    circleListAdapter.notifyDataSetChanged();
                } else {
                    Log.v(TAG, "##################" + e);
                }
            }
        });
    }

    /**
     * 菜单创建器。在Item要创建菜单的时候调用。
     */
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            int width = getResources().getDimensionPixelSize(R.dimen.dp_96);
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            SwipeMenuItem addItem1 = new SwipeMenuItem(mContext)
                    .setBackground(android.R.color.holo_green_dark)// 点击的背景。
                    .setText("成员列表")
                    .setTextSize(16)
                    .setTextColor(getResources().getColor(R.color.white))
                    .setWidth(width) // 宽度。
                    .setHeight(height); // 高度。
            SwipeMenuItem addItem2 = new SwipeMenuItem(mContext)
                    .setBackground(android.R.color.holo_red_light)// 点击的背景。
                    .setText("解散")
                    .setTextSize(16)
                    .setTextColor(getResources().getColor(R.color.white))
                    .setWidth(width) // 宽度。
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
                if(menuPosition==0){//成员列表
                    Intent memberIntent=new Intent(FusionAction.MINE_NUMBER);
                    memberIntent.putExtra(FusionAction.CircleListExtra.CIRCLE_ID,mDataList.get(adapterPosition).getObjectId());
                    startActivity(memberIntent);
                }else if(menuPosition==1){ //解散
                    disBand(mDataList.get(adapterPosition).getObjectId(),menuPosition);
                }
            } else if (direction == SwipeMenuRecyclerView.LEFT_DIRECTION) {
            }
        }
    };

    /**
     * 解散一个圈子
     */
    private void disBand(String objectId,final int position){
        // 执行 CQL 语句实现删除一个 Todo 对象
        AVQuery.doCloudQueryInBackground("delete from cycle where objectId='"+objectId+"'", new CloudQueryCallback<AVCloudQueryResult>() {
            @Override
            public void done(AVCloudQueryResult avCloudQueryResult, AVException e) {
                // 如果 e 为空，说明保存成功
                if(e==null){
                    ToastUtils.showToast(CircleListActivity.this,"圈子解散成功!");
                    removeData(position);
                    circleListAdapter.notifyDataSetChanged();
                }else{
                    ToastUtils.showToast(CircleListActivity.this,"圈子解散失败!");
                    circleListAdapter.notifyDataSetChanged();
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
     * 更新列表数据
     * @param position
     */
    public void removeData(int position) {
        mDataList.remove(position);
        circleListAdapter.notifyDataSetChanged();
    }
}
