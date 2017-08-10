package com.junjingit.propery.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVStatus;
import com.avos.avoscloud.AVStatusQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.FollowCallback;
import com.avos.avoscloud.GetCallback;
import com.junjingit.propery.FocusListActivity;
import com.junjingit.propery.HomeActivity;
import com.junjingit.propery.R;
import com.junjingit.propery.common.FusionAction;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemMoveListener;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemStateChangedListener;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

/**
 * A simple {@link Fragment} subclass.
 */
public class FocusFragment extends Fragment
{
    private static final String TAG = "FocusFragment";

    private View mRootView;

    private SwipeMenuRecyclerView mFocusList;

    private CommunityFragment.CommunityAdapter mCommunityAdapter;

    private List<AVUser> mFocusCycleList = new ArrayList<>();

    private FocusListAdapter mAdapter;

    /**
     * 菜单创建器，在Item要创建菜单的时候调用。
     */
    private SwipeMenuCreator mSwipeMenuCreator = new SwipeMenuCreator()
    {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu,
                                 SwipeMenu swipeRightMenu, int viewType)
        {
            int width = getResources().getDimensionPixelSize(R.dimen.dp_72);

            // 1. MATCH_PARENT 自适应高度，保持和Item一样高;
            // 2. 指定具体的高，比如80;
            // 3. WRAP_CONTENT，自身高度，不推荐;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            SwipeMenuItem closeItem = new SwipeMenuItem(getActivity()).setBackground(android.R.color.holo_red_light)
                    //                    .setImage(R.mipmap.ic_action_close)
                    .setWidth(width)
                    .setText("取消关注")
                    .setTextColor(getActivity().getResources()
                            .getColor(R.color.white))
                    .setHeight(height);

            // 添加菜单到右侧。
            swipeRightMenu.addMenuItem(closeItem);
        }
    };

    public void setCommunityAdapter(
            CommunityFragment.CommunityAdapter communityAdapter)
    {
        this.mCommunityAdapter = communityAdapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        mRootView = inflater.inflate(R.layout.fragment_focus, null);
        initView();
        initData();
        return mRootView;
    }

    private void initData()
    {

        //查询关注者
        AVQuery<AVUser> followeeQuery = AVUser.followeeQuery(AVUser.getCurrentUser()
                        .getObjectId(),
                AVUser.class);
        followeeQuery.findInBackground(new FindCallback<AVUser>()
        {
            @Override
            public void done(List<AVUser> avObjects, AVException avException)
            {
                if (null == avException && null != avObjects)
                {
                    mFocusCycleList.clear();
                    mFocusCycleList.add(0, new AVUser());
                    mFocusCycleList.addAll(avObjects);

                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void initView()
    {
        mFocusList = mRootView.findViewById(R.id.focus_list);
        mAdapter = new FocusListAdapter(getActivity());
        mAdapter.setFocusList(mFocusCycleList);
        mFocusList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mFocusList.setAdapter(mAdapter);

        mFocusList.setSwipeMenuItemClickListener(new SwipeMenuItemClickListener()
        {
            @Override
            public void onItemClick(SwipeMenuBridge menuBridge)
            {
                menuBridge.closeMenu();

                // 左侧还是右侧菜单。
                int direction = menuBridge.getDirection();

                // RecyclerView的Item的position。
                final int adapterPosition = menuBridge.getAdapterPosition();

                // 菜单在RecyclerView的Item中的Position。
                // int menuPosition = menuBridge.getPosition();

                if (direction == SwipeMenuRecyclerView.RIGHT_DIRECTION)
                {
                    AVUser.getCurrentUser()
                            .unfollowInBackground(mFocusCycleList.get(adapterPosition)
                                            .getObjectId(),
                                    new FollowCallback()
                                    {
                                        @Override
                                        public void done(AVObject avObject,
                                                         AVException e)
                                        {
                                            if (null == e)
                                            {
                                                mFocusCycleList.remove(adapterPosition);
                                                mAdapter.notifyDataSetChanged();

                                                //                                                mCommunityAdapter.setPosition(1);
                                                //                                                mCommunityAdapter.notifyDataSetChanged();

                                                Log.d(TAG, "取消关注成功");
                                            }
                                        }

                                    });
                }
            }
        });

        // 设置菜单创建器。
        // 设置菜单Item点击监听。
        //        mFocusList.setLongPressDragEnabled(true); // 开启拖拽。
        //        mFocusList.setItemViewSwipeEnabled(true);
        mFocusList.setSwipeMenuCreator(mSwipeMenuCreator);

        mFocusList.setOnItemStateChangedListener(new OnItemStateChangedListener()
        {
            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder,
                                          int actionState)
            {
                if (actionState == OnItemStateChangedListener.ACTION_STATE_SWIPE)
                {

                    if (viewHolder.getPosition() == 0)
                    {
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        mFocusList.setOnItemMoveListener(new OnItemMoveListener()
        {
            @Override
            public boolean onItemMove(RecyclerView.ViewHolder srcHolder,
                                      RecyclerView.ViewHolder targetHolder)
            {
                return false;
            }

            @Override
            public void onItemDismiss(int position)
            {
            }
        });
    }

    class FocusListAdapter extends RecyclerView.Adapter<FocusHolder>
    {
        private Context context;

        private List<AVUser> focusList;

        private Badge badge;

        public void setFocusList(List<AVUser> focusList)
        {
            this.focusList = focusList;
        }

        public FocusListAdapter(Context context)
        {
            this.context = context;
        }

        @Override
        public FocusHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            FocusHolder holder = new FocusHolder(LayoutInflater.from(context)
                    .inflate(R.layout.focus_item_layout, null));

            return holder;
        }

        @Override
        public int getItemViewType(int position)
        {
            if (position == 0)
            {
                return 2;
            }
            //            else if (position == 1)
            //            {
            //                return 3;
            //            }

            return super.getItemViewType(position);
        }

        @Override
        public void onBindViewHolder(final FocusHolder holder,
                                     final int position)
        {
            final AVUser user = focusList.get(position);

            if (getItemViewType(position) == 2)
            {
                holder.itemName.setVisibility(View.VISIBLE);
                holder.itemName.setText("我的圈子");

                AVRelation<AVObject> relation = AVUser.getCurrentUser()
                        .getRelation("cycle");

                relation.getQuery()
                        .getFirstInBackground(new GetCallback<AVObject>()
                        {
                            @Override
                            public void done(final AVObject avObject,
                                             AVException e)
                            {
                                if (null == e && null != avObject)
                                {
                                    String lastUpdateCycleId = avObject.getObjectId();

                                    AVQuery<AVObject> query = new AVQuery<AVObject>(
                                            "Public_Status");
                                    query.whereEqualTo("cycle_id",
                                            lastUpdateCycleId);
                                    query.orderByAscending("createdAt");

                                    query.getFirstInBackground(new GetCallback<AVObject>()
                                    {
                                        @Override
                                        public void done(AVObject avObject,
                                                         AVException e)
                                        {
                                            if (null == e&&avObject!=null)
                                            {
                                                String msg = avObject.getString("message");

                                                holder.msgPerview.setText(msg);

                                                Date date = avObject.getCreatedAt();
                                                SimpleDateFormat sdf = new SimpleDateFormat(
                                                        "yyyy-MM-dd hh:mm");
                                                holder.updateTime.setText(sdf.format(date.getTime()));
                                            }
                                        }
                                    });

                                }
                            }
                        });

            }
            else
            {
                holder.itemName.setVisibility(View.GONE);

                //                AVUser.getCurrentUser().followeeQuery()

                Log.d(TAG, "nikename = "
                        + AVUser.getCurrentUser().getObjectId());

                AVUser.getQuery().getInBackground(user.getObjectId(),
                        new GetCallback<AVUser>()
                        {
                            @Override
                            public void done(AVUser avUser, AVException e)
                            {
                                if (null == e)
                                {
                                    holder.name.setText(avUser.getString("nikename"));
                                }
                            }
                        });

                AVStatusQuery inboxQuery = AVStatus.inboxQuery(AVUser.getCurrentUser(),
                        AVStatus.INBOX_TYPE.TIMELINE.toString());
                inboxQuery.orderByAscending("createdAt");
                inboxQuery.getFirstInBackground(new GetCallback<AVStatus>()
                {
                    @Override
                    public void done(AVStatus avStatus, AVException e)
                    {
                        if (null == e && null != avStatus)
                        {
                            String msg = avStatus.getMessage();
                            holder.msgPerview.setText(msg);

                            Date date = avStatus.getCreatedAt();
                            SimpleDateFormat sdf = new SimpleDateFormat(
                                    "yyyy-MM-dd hh:mm");
                            holder.updateTime.setText(sdf.format(date.getTime()));

                        }
                    }
                });

                AVStatus.getUnreadStatusesCountInBackground(AVStatus.INBOX_TYPE.TIMELINE.toString(),
                        new CountCallback()
                        {
                            @Override
                            public void done(int i, AVException e)
                            {
                                if (null == e)
                                {
                                    badge = addBadgeAt(holder.icon, i);
                                }
                            }
                        });

            }

            //            if (getItemViewType(position) == 3)
            //            {
            //                holder.name.setText("我关注的");
            //            }

            holder.item.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if (getItemViewType(position) == 2)
                    {
                        Intent toFocusList = new Intent(
                                FusionAction.FOCUS_LIST_ACTION);

                        startActivity(toFocusList);

                    }
                    else
                    {
                        Intent toUserStatus = new Intent(
                                FusionAction.USER_STATUS_LIST_ACTION);
                        toUserStatus.putExtra(FusionAction.FocusListExtra.USER_ID,
                                user.getObjectId());
                        badge.hide(true);
                        startActivity(toUserStatus);
                    }
                }
            });

        }

        @Override
        public int getItemCount()
        {
            int size = null != focusList ? focusList.size() : 0;

            return size;// == 0 ? 1 : size;
        }
    }

    class FocusHolder extends RecyclerView.ViewHolder
    {
        ImageView icon;

        TextView name;

        TextView msgPerview;

        TextView updateTime;

        TextView itemName;

        LinearLayout item;

        public FocusHolder(View itemView)
        {
            super(itemView);

            icon = itemView.findViewById(R.id.item_icon);
            name = itemView.findViewById(R.id.item_user_id);
            msgPerview = itemView.findViewById(R.id.item_msg_perview);
            updateTime = itemView.findViewById(R.id.item_time);
            item = itemView.findViewById(R.id.focus_item);
            itemName = itemView.findViewById(R.id.item_name);
        }

    }

    private Badge addBadgeAt(View view, int number)
    {
        // add badge
        return new QBadgeView(getActivity()).setBadgeNumber(number)
                .setGravityOffset(12, 2, true)
                .bindTarget(view)
                .setOnDragStateChangedListener(new Badge.OnDragStateChangedListener()
                {
                    @Override
                    public void onDragStateChanged(int dragState, Badge badge,
                                                   View targetView)
                    {
                        if (Badge.OnDragStateChangedListener.STATE_SUCCEED == dragState)
                        {
                            //                            Toast.makeText(getActivity(),
                            //                                    "已读",
                            //                                    Toast.LENGTH_SHORT).show();

                            badge.hide(true);
                        }
                    }
                });
    }

}
