package com.junjingit.propery;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVCloudQueryResult;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CloudQueryCallback;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

/**
 * Created by niufan on 17/5/27.
 */

public class HomeListAdapter extends
        RecyclerView.Adapter<HomeListAdapter.HomeHolder>
{
    private static final String TAG = "HomeListAdapter";
    
    private Context mContext;
    
    private List<AVObject> mListData;
    
    private String mFrom;
    
    public HomeListAdapter(Context context)
    {
        this.mContext = context;
    }
    
    public void setFrom(String from)
    {
        this.mFrom = from;
    }
    
    public void setListData(List<AVObject> listData)
    {
        this.mListData = listData;
    }
    
    @Override
    public HomeHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        HomeHolder holder = new HomeHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.home_item_layout, null));
        
        return holder;
    }
    
    @Override
    public void onBindViewHolder(final HomeHolder holder, final int position)
    {
        //        holder.imgContent.setImageResource(mContext.getResources()
        //                .getIdentifier("ic_palette_0" + position % 4,
        //                        "mipmap",
        //                        mContext.getPackageName()));
        
        final AVObject obj = mListData.get(position);
        
        String images = obj.getString("image");
        
        if (StringUtil.isNullOrEmpty(images))
        {
            String[] imgs = images.split(",");
            
            ImageLoader.getInstance().loadImage(imgs[0],
                    new ImageLoadingListener()
                    {
                        @Override
                        public void onLoadingStarted(String imageUri, View view)
                        {
                            
                        }
                        
                        @Override
                        public void onLoadingFailed(String imageUri, View view,
                                FailReason failReason)
                        {
                            
                        }
                        
                        @Override
                        public void onLoadingComplete(String imageUri,
                                View view, Bitmap loadedImage)
                        {
                            holder.imgContent.setImageBitmap(loadedImage);
                        }
                        
                        @Override
                        public void onLoadingCancelled(String imageUri,
                                View view)
                        {
                            
                        }
                    });
        }
        
        holder.userName.setText(mListData.get(position).getString("userId"));
        holder.msgContent.setText(mListData.get(position).getString("message"));
        holder.descCount.setText(mListData.get(position)
                .getString("ReplyCount"));
        holder.updateTime.setText(mListData.get(position)
                .getString("createdAt"));
        
        AVQuery<AVObject> query = new AVQuery<AVObject>("Reply");
        query.whereEqualTo("status_id", obj.getObjectId());
        
        query.findInBackground(new FindCallback<AVObject>()
        {
            @Override
            public void done(List<AVObject> list, AVException e)
            {
                if (null == e)
                {
                    holder.descCount.setText(String.valueOf(list.size()));
                }
            }
        });
        
        holder.zanLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                final String statusId = obj.getObjectId();
                
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
                                zanObj.put("user_id", AVUser.getCurrentUser()
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
        
        if (mFrom.equals("community"))
        {
            holder.title.setVisibility(View.GONE);
        }
        else if (mFrom.equals("home"))
        {
            holder.title.setText(mListData.get(position)
                    .getString("notify_title"));
        }
        
        holder.imgContent.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (mFrom.equals("home"))
                {
                    mContext.startActivity(new Intent(
                            "com.junjignit.propery.NOTIFY_DETAIL"));
                }
                else if (mFrom.equals("community"))
                {
                    //                    if (Integer.valueOf(Build.VERSION.SDK_INT) < 21)
                    //                    {
                    
                    Intent intent = new Intent(
                            "com.junjing.propery.QUOTE_DETAIL");
                    intent.putExtra("objectId", obj.getObjectId());
                    
                    mContext.startActivity(intent);
                    
                    //                    }
                    //                    else
                    //                    {
                    //                        mContext.startActivity(new Intent(
                    //                                "com.junjing.propery.QUOTE_DETAIL"),
                    //                                ActivityOptions.makeSceneTransitionAnimation((Activity) mContext,
                    //                                        view,
                    //                                        "transName")
                    //                                        .toBundle());
                    //                    }
                }
            }
        });
        
    }
    
    @Override
    public int getItemCount()
    {
        return null == mListData ? 0 : mListData.size();
        //        return 10;
    }
    
    class HomeHolder extends RecyclerView.ViewHolder
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
        
        public HomeHolder(View itemView)
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
        }
    }
}
