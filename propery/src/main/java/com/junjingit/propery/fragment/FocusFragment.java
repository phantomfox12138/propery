package com.junjingit.propery.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.junjingit.propery.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FocusFragment extends Fragment
{
    private static final String TAG = "FocusFragment";
    
    private View mRootView;
    
    private RecyclerView mFocusList;
    
    private List<AVObject> mFocusCycleList = new ArrayList<>();
    
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
        AVRelation<AVObject> relation = AVUser.getCurrentUser()
                .getRelation("cycle");
        
        relation.getQuery().findInBackground(new FindCallback<AVObject>()
        {
            @Override
            public void done(List<AVObject> list, AVException e)
            {
                if (null == e && null != list)
                {
                    Log.d(TAG, "count = " + list.size());
                    
                    mFocusCycleList.clear();
                    mFocusCycleList.addAll(list);
                    
                    mAdapter.notifyDataSetChanged();
                    
                }
            }
        });
    }
    
    private void initView()
    {
        mFocusList = mRootView.findViewById(R.id.focus_list);
        
        mAdapter = new FocusListAdapter(getActivity());
        mAdapter.setList(mFocusCycleList);
        
        mFocusList.setLayoutManager(new LinearLayoutManager(getActivity()));
        
        mFocusList.setAdapter(mAdapter);
    }
    
    class FocusListAdapter extends RecyclerView.Adapter<FocusHolder>
    {
        private Context context;
        
        private List<AVObject> list;
        
        public void setList(List<AVObject> list)
        {
            this.list = list;
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
        public void onBindViewHolder(final FocusHolder holder, int position)
        {
            AVObject object = list.get(position);
            
            holder.name.setText(object.getString("cycle_name"));
            
            AVQuery<AVObject> query = new AVQuery<>("Public_Status");
            query.whereEqualTo("cycle_id", object.getObjectId());
            query.orderByDescending("createdAt");
            
            query.findInBackground(new FindCallback<AVObject>()
            {
                @Override
                public void done(List<AVObject> list, AVException e)
                {
                    if (null == e && null != list && list.size() > 0)
                    {
                        holder.msgPerview.setText(list.get(0)
                                .getString("message"));
                        
                        holder.updateTime.setText(list.get(0)
                                .getString("createdAt"));
                    }
                }
            });
        }
        
        @Override
        public int getItemCount()
        {
            return null != list ? list.size() : 0;
        }
    }
    
    class FocusHolder extends RecyclerView.ViewHolder
    {
        ImageView icon;
        
        TextView name;
        
        TextView msgPerview;
        
        TextView updateTime;
        
        public FocusHolder(View itemView)
        {
            super(itemView);
            
            icon = itemView.findViewById(R.id.item_icon);
            name = itemView.findViewById(R.id.item_user_id);
            msgPerview = itemView.findViewById(R.id.item_msg_perview);
            updateTime = itemView.findViewById(R.id.item_time);
        }
        
    }
    
}
