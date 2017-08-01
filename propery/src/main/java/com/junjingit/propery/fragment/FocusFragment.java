package com.junjingit.propery.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVStatus;
import com.avos.avoscloud.AVStatusQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.InboxStatusFindCallback;
import com.junjingit.propery.R;
import com.junjingit.propery.common.FusionAction;

/**
 * A simple {@link Fragment} subclass.
 */
public class FocusFragment extends Fragment
{
    private static final String TAG = "FocusFragment";
    
    private View mRootView;
    
    private RecyclerView mFocusList;
    
    private List<AVUser> mFocusCycleList = new ArrayList<>();
    
    private FocusListAdapter mAdapter;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        mRootView = inflater.inflate(R.layout.fragment_focus, null);
        
        initData();
        
        initView();
        
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
        //        
        mAdapter = new FocusListAdapter(getActivity());
        mAdapter.setFocusList(mFocusCycleList);
        //        
        mFocusList.setLayoutManager(new LinearLayoutManager(getActivity()));
        //        
        mFocusList.setAdapter(mAdapter);
        
        // 设置菜单创建器。
        // 设置菜单Item点击监听。
        //        mFocusList.setLongPressDragEnabled(true); // 开启拖拽。
        //        mFocusList.setItemViewSwipeEnabled(true);
    }
    
    class FocusListAdapter extends RecyclerView.Adapter<FocusHolder>
    {
        private Context context;
        
        private List<AVUser> focusList;
        
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
            AVUser user = focusList.get(position);
            
            if (getItemViewType(position) == 2)
            {
                holder.name.setText("我的圈子");
                
                AVRelation<AVObject> relation = AVUser.getCurrentUser()
                        .getRelation("cycle");
                
                relation.getQuery()
                        .getFirstInBackground(new GetCallback<AVObject>()
                        {
                            @Override
                            public void done(final AVObject avObject,
                                    AVException e)
                            {
                                if (null == e)
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
                                            if (null == e)
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
                AVStatusQuery inboxQuery = AVStatus.inboxQuery(AVUser.getCurrentUser(),
                        AVStatus.INBOX_TYPE.TIMELINE.toString());
                inboxQuery.orderByAscending("createdAt");
                inboxQuery.getFirstInBackground(new GetCallback<AVStatus>()
                {
                    @Override
                    public void done(AVStatus avStatus, AVException e)
                    {
                        if (null == e)
                        {
                            String msg = avStatus.getMessage();
                            holder.msgPerview.setText(msg);
                            
                            Date date = avStatus.getCreatedAt();
                            SimpleDateFormat sdf = new SimpleDateFormat(
                                    "yyyy-MM-dd hh:mm");
                            holder.updateTime.setText(sdf.format(date.getTime()));
                            
                            holder.name.setText(avStatus.getSource()
                                    .getString("nikename"));
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
    
    //    class FocusListAdapter extends RecyclerView.Adapter<FocusHolder>
    //    {
    //        private Context context;
    //        
    //        private List<AVObject> list;
    //        
    //        public void setList(List<AVObject> list)
    //        {
    //            this.list = list;
    //        }
    //        
    //        public FocusListAdapter(Context context)
    //        {
    //            this.context = context;
    //        }
    //        
    //        @Override
    //        public FocusHolder onCreateViewHolder(ViewGroup parent, int viewType)
    //        {
    //            FocusHolder holder = new FocusHolder(LayoutInflater.from(context)
    //                    .inflate(R.layout.focus_item_layout, null));
    //            
    //            return holder;
    //        }
    //        
    //        @Override
    //        public void onBindViewHolder(final FocusHolder holder, int position)
    //        {
    //            final AVObject object = list.get(position);
    //            
    //            holder.name.setText(object.getString("cycle_name"));
    //            
    //            AVQuery<AVObject> query = new AVQuery<>("Public_Status");
    //            query.whereEqualTo("cycle_id", object.getObjectId());
    //            query.orderByDescending("createdAt");
    //            
    //            query.findInBackground(new FindCallback<AVObject>()
    //            {
    //                @Override
    //                public void done(List<AVObject> list, AVException e)
    //                {
    //                    if (null == e && null != list && list.size() > 0)
    //                    {
    //                        holder.msgPerview.setText(list.get(0)
    //                                .getString("message"));
    //                        Date date = list.get(0).getCreatedAt();
    //                        
    //                        SimpleDateFormat sdf = new SimpleDateFormat(
    //                                "yyyy-MM-dd hh:mm");
    //                        holder.updateTime.setText(sdf.format(date.getTime()));
    //                    }
    //                }
    //            });
    //            
    //            holder.item.setOnClickListener(new View.OnClickListener()
    //            {
    //                @Override
    //                public void onClick(View view)
    //                {
    //                    String objId = object.getObjectId();
    //                    
    //                    Intent toFocusList = new Intent(
    //                            FusionAction.CYCLE_DETAIL_LIST_ACTION);
    //                    toFocusList.putExtra(FusionAction.FocusListExtra.OBJECT_ID,
    //                            objId);
    //                    
    //                    startActivity(toFocusList);
    //                }
    //            });
    //            
    //        }
    //        
    //        @Override
    //        public int getItemCount()
    //        {
    //            return null != list ? list.size() : 0;
    //        }
    //    }
    
    class FocusHolder extends RecyclerView.ViewHolder
    {
        ImageView icon;
        
        TextView name;
        
        TextView msgPerview;
        
        TextView updateTime;
        
        LinearLayout item;
        
        public FocusHolder(View itemView)
        {
            super(itemView);
            
            icon = itemView.findViewById(R.id.item_icon);
            name = itemView.findViewById(R.id.item_user_id);
            msgPerview = itemView.findViewById(R.id.item_msg_perview);
            updateTime = itemView.findViewById(R.id.item_time);
            item = itemView.findViewById(R.id.focus_item);
        }
        
    }
    
}
