package com.junjingit.propery;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVStatus;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendCycleActivity extends AppCompatActivity
{
    
    private static final String TAG = "FriendCycleActivity";
    
    private FriendCycleAdapter mAdapter;
    
    private SwipeMenuRecyclerView mFriendCycleList;
    
    private Toolbar mToolbar;
    
    private List<AVStatus> mFriendStatusList = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_cycle);
        
        initView();
        
        initData();
        
    }
    
    private void initView()
    {
        mFriendCycleList = (SwipeMenuRecyclerView) findViewById(R.id.friend_cycle_list);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        
        mFriendCycleList.setLayoutManager(new LinearLayoutManager(this));
        
        mAdapter = new FriendCycleAdapter();
        mAdapter.setDataList(mFriendStatusList);
    }
    
    private void initData()
    {
        
        AVStatus.inboxQuery(AVUser.getCurrentUser(),
                AVStatus.INBOX_TYPE.TIMELINE.toString())
                .findInBackground(new FindCallback<AVStatus>()
                {
                    @Override
                    public void done(List<AVStatus> list, AVException e)
                    {
                        if (null == e)
                        {
                            try
                            {
                                AVStatus.statusQuery(AVUser.getCurrentUser())
                                        .findInBackground(new FindCallback<AVStatus>()
                                        {
                                            @Override
                                            public void done(
                                                    List<AVStatus> list,
                                                    AVException e)
                                            {
                                                
                                            }
                                        });
                            }
                            catch (AVException e1)
                            {
                                e1.printStackTrace();
                            }
                        }
                    }
                });
    }
    
    class FriendCycleAdapter extends RecyclerView.Adapter<FriendCycleHolder>
    {
        
        private List<AVStatus> dataList;
        
        public void setDataList(List<AVStatus> dataList)
        {
            this.dataList = dataList;
        }
        
        @Override
        public int getItemViewType(int position)
        {
            String imageName = dataList.get(position).getImageUrl();
            if (StringUtil.isNullOrEmpty(imageName))
            {
                return 2;
            }
            
            return super.getItemViewType(position);
        }
        
        @Override
        public FriendCycleHolder onCreateViewHolder(ViewGroup parent,
                int viewType)
        {
            FriendCycleHolder holder = null;
            
            if (viewType == 2)
            {
                holder = new FriendCycleHolder(
                        LayoutInflater.from(FriendCycleActivity.this)
                                .inflate(R.layout.item_simple_list_1, null));
                
            }
            else
            {
                holder = new FriendCycleHolder(
                        LayoutInflater.from(FriendCycleActivity.this)
                                .inflate(R.layout.home_item_layout, null));
            }
            return holder;
        }
        
        @Override
        public void onBindViewHolder(FriendCycleHolder holder, int position)
        {
            AVStatus status = dataList.get(position);
            
        }
        
        @Override
        public int getItemCount()
        {
            return null != dataList ? dataList.size() : 0;
        }
    }
    
    class FriendCycleHolder extends RecyclerView.ViewHolder
    {
        
        ImageView icon;
        
        ImageView imgContent;
        
        TextView userName;
        
        TextView msgContent;
        
        TextView cycleName;
        
        TextView descCount;
        
        TextView title;
        
        TextView zanCount;
        
        RelativeLayout zanLayout;
        
        TextView updateTime;
        
        TextView time;
        
        CardView container;
        
        View zanAndDesc;
        
        CircleImageView textItemIcon;
        
        public FriendCycleHolder(View itemView)
        {
            super(itemView);
            
            icon = itemView.findViewById(R.id.iv_icon);
            userName = itemView.findViewById(R.id.tv_id);
            msgContent = itemView.findViewById(R.id.tv_content);
            imgContent = itemView.findViewById(R.id.iv_cover);
            cycleName = itemView.findViewById(R.id.cycle_name);
            descCount = itemView.findViewById(R.id.item_desc_count);
            title = itemView.findViewById(R.id.tv_title);
            zanCount = itemView.findViewById(R.id.item_zan_count);
            zanLayout = itemView.findViewById(R.id.zan_layout);
            updateTime = itemView.findViewById(R.id.update_time);
            time = itemView.findViewById(R.id.tv_time);
            container = itemView.findViewById(R.id.text_container);
            zanAndDesc = itemView.findViewById(R.id.zan_desc_layout);
            textItemIcon = itemView.findViewById(R.id.civ_avatar);
        }
    }
}
