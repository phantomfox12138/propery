package com.junjingit.propery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVStatus;
import com.avos.avoscloud.AVStatusQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendCycleActivity extends AppCompatActivity
{
    
    private static final String TAG = "FriendCycleActivity";
    
    private FriendCycleAdapter mAdapter;
    
    private SwipeMenuRecyclerView mFriendCycleList;
    
    private Toolbar mToolbar;
    
    private DisplayImageOptions options;
    
    private TextView mUserName;
    
    private ImageView mUserIcon;
    
    private ImageView mCover;
    
    private TextView mIdiograph;
    
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    
    private List<AVStatus> mFriendStatusList = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_cycle);
        
        options = new DisplayImageOptions.Builder()//.showImageOnLoading(R.drawable.ic_stub)
        //.showImageForEmptyUri(R.drawable.ic_empty)
        //.showImageOnFail(R.drawable.ic_error)
        .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                //.displayer(new RoundedBitmapDisplayer(20))
                .build();
        
        initView();
        
        initData();
        
    }
    
    private void initView()
    {
        mFriendCycleList = (SwipeMenuRecyclerView) findViewById(R.id.friend_cycle_list);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mUserName = (TextView) findViewById(R.id.user_name);
        mUserIcon = (ImageView) findViewById(R.id.user_icon);
        mIdiograph = (TextView) findViewById(R.id.user_sign);
        mCover = (ImageView) findViewById(R.id.appbar_img);
        
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        mFriendCycleList.setLayoutManager(new LinearLayoutManager(this));
        
        mAdapter = new FriendCycleAdapter();
        mAdapter.setDataList(mFriendStatusList);
        
        mFriendCycleList.setAdapter(mAdapter);
        
        String nickName = AVUser.getCurrentUser().getString("nickname");
        String sign = AVUser.getCurrentUser().getString("user_sign");
        
        mUserName.setText(nickName);
        mIdiograph.setText(StringUtil.isNullOrEmpty(sign) ? "他什么都没有写" : sign);
        
        //TODO：加载用户头像和用户封面
        
    }
    
    private void initData()
    {
        
        AVStatusQuery query = AVStatus.inboxQuery(AVUser.getCurrentUser(),
                AVStatus.INBOX_TYPE.TIMELINE.toString());
        query.orderByDescending("createdAt");
        //        query.orderByAscending("createdAt");
        
        query.findInBackground(new FindCallback<AVStatus>()
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
                                    public void done(List<AVStatus> list,
                                            AVException e)
                                    {
                                        if (null == e)
                                        {
                                            mFriendStatusList.clear();
                                            mFriendStatusList.addAll(list);
                                            
                                            Collections.sort(mFriendStatusList,
                                                    new Comparator<AVStatus>()
                                                    {
                                                        @Override
                                                        public int compare(
                                                                AVStatus avObject,
                                                                AVStatus t1)
                                                        {
                                                            return t1.getCreatedAt()
                                                                    .compareTo(avObject.getCreatedAt());
                                                        }
                                                    });
                                            
                                            mAdapter.notifyDataSetChanged();
                                        }
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
            Map<String, Object> map = dataList.get(position).getData();
            
            if (map.size() == 1)
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
        public void onBindViewHolder(final FriendCycleHolder holder,
                int position)
        {
            final AVStatus status = dataList.get(position);
            
            String userName = status.getSource().getString("nickname");
            holder.cycleName.setVisibility(View.GONE);
            holder.msgContent.setText(status.getMessage());
            
            if (getItemViewType(position) == 2)
            {
                holder.title.setText(userName);
            }
            else
            {
                holder.userName.setText(userName);
                holder.title.setVisibility(View.GONE);
                
                Map<String, Object> data = status.getData();
                String imageUrl = (String) data.get("imageUrl");// status.getString("imageUrl");
                String imgName = (String) data.get("imageName");//status.getString("imageName");
                
                String[] imgs = imageUrl.split(",");
                String[] names = imgName.split(",");
                
                AVFile file = new AVFile(names[0], imgs[0],
                        new HashMap<String, Object>());
                String thumbUrl = file.getThumbnailUrl(true, 1920, 1080);
                
                ImageLoader.getInstance().displayImage(thumbUrl,
                        holder.imgContent,
                        options,
                        animateFirstListener);
            }
            
            Date date = status.getCreatedAt();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            if (getItemViewType(position) == 2)
            {
                holder.time.setText(sdf.format(date.getTime()));
            }
            else
            {
                holder.updateTime.setText(sdf.format(date.getTime()));
            }
            
            AVQuery<AVObject> commentCountQuery = new AVQuery<>("Reply");
            commentCountQuery.whereEqualTo("status_id", status.getObjectId());
            
            commentCountQuery.countInBackground(new CountCallback()
            {
                @Override
                public void done(int i, AVException e)
                {
                    if (null == e)
                    {
                        holder.descCount.setText(String.valueOf(i));
                    }
                }
            });
            
            holder.zanLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    final String statusId = status.getObjectId();
                    
                    AVQuery<AVObject> query = new AVQuery<AVObject>("zan");
                    query.whereEqualTo("status_id", statusId);
                    query.whereEqualTo("user_id", AVUser.getCurrentUser()
                            .getObjectId());
                    
                    query.findInBackground(new FindCallback<AVObject>()
                    {
                        @Override
                        public void done(List<AVObject> list, AVException e)
                        {
                            if (null == e)
                            {
                                if (list.size() == 1)
                                {
                                    list.get(0).deleteInBackground();
                                    
                                    String zanCount = holder.zanCount.getText()
                                            .toString();
                                    
                                    int count = Integer.valueOf(zanCount);
                                    holder.zanCount.setText(String.valueOf(count - 1));
                                }
                                else
                                {
                                    AVObject zanObj = new AVObject("zan");
                                    zanObj.put("status_id", statusId);
                                    zanObj.put("user_id",
                                            AVUser.getCurrentUser()
                                                    .getObjectId());
                                    
                                    zanObj.saveInBackground(new SaveCallback()
                                    {
                                        @Override
                                        public void done(AVException e)
                                        {
                                            if (null == e)
                                            {
                                                AVQuery<AVObject> query = new AVQuery<>(
                                                        "zan");
                                                query.whereEqualTo("status_id",
                                                        statusId);
                                                query.countInBackground(new CountCallback()
                                                {
                                                    @Override
                                                    public void done(int i,
                                                            AVException e)
                                                    {
                                                        if (e == null)
                                                        {
                                                            holder.zanCount.setText(String.valueOf(i));
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
            });
            
            //            holder.descCount.setText();
            
            holder.descCount.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    
                }
            });
            
            if (getItemViewType(position) == 2)
            {
                holder.container.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        Intent intent = new Intent(
                                "com.junjing.propery.QUOTE_DETAIL");
                        intent.putExtra("objectId", status.getObjectId());
                        intent.putExtra("type", "status");
                        
                        startActivity(intent);
                    }
                });
            }
            else
            {
                holder.imgContent.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        Intent intent = new Intent(
                                "com.junjing.propery.QUOTE_DETAIL");
                        intent.putExtra("objectId", status.getObjectId());
                        intent.putExtra("type", "status");
                        
                        startActivity(intent);
                    }
                });
                
            }
            
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
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        //Toolbar上的左上角的返回箭头的键值为Android.R.id.home  不是R.id.home
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
                
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    private class AnimateFirstDisplayListener extends
            SimpleImageLoadingListener
    {
        
        final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());
        
        @Override
        public void onLoadingComplete(String imageUri, View view,
                Bitmap loadedImage)
        {
            if (loadedImage != null)
            {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay)
                {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
                
                //                notifyDataSetChanged();
            }
        }
    }
    
}
