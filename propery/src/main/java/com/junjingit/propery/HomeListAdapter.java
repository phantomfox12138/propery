package com.junjingit.propery;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.junjingit.propery.widget.ItemHelpter;
import com.junjingit.propery.widget.SwipeLayout;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

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
    
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    
    private DisplayImageOptions options;
    
    private RecyclerView mRecycler;
    
    public HomeListAdapter(Context context)
    {
        this.mContext = context;
        
        options = new DisplayImageOptions.Builder()//.showImageOnLoading(R.drawable.ic_stub)
        //.showImageForEmptyUri(R.drawable.ic_empty)
        //.showImageOnFail(R.drawable.ic_error)
        .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                //.displayer(new RoundedBitmapDisplayer(20))
                .build();
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
    public int getItemViewType(int position)
    {
        String imageName = mListData.get(position).getString("image_name");
        if (StringUtil.isNullOrEmpty(imageName))
        {
            return 2;
        }
        
        return super.getItemViewType(position);
    }
    
    @Override
    public HomeHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        HomeHolder holder = null;
        
        if (viewType == 2)
        {
            holder = new HomeHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.item_simple_list_1, null));
            
        }
        else
        {
            holder = new HomeHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.home_item_layout, null));
        }
        return holder;
    }
    
    @Override
    public void onBindViewHolder(final HomeHolder holder, final int position)
    {
        
        final AVObject obj = mListData.get(position);
        
        String images = obj.getString("image");
        String imgName = obj.getString("image_name");
        
        String cycleId = obj.getString("cycle_id");
        
        AVQuery<AVObject> cycQuery = new AVQuery<>("cycle");
        cycQuery.getInBackground(cycleId, new GetCallback<AVObject>()
        {
            @Override
            public void done(AVObject avObject, AVException e)
            {
                if (null == e)
                {
                    if (getItemViewType(position) == 2)
                    {
                        
                    }
                    else
                    {
                        holder.cycleName.setText(avObject.getString("cycle_name"));
                    }
                }
                else
                {
                    holder.cycleName.setText("");
                }
            }
        });
        
        if (!StringUtil.isNullOrEmpty(images)
                && !StringUtil.isNullOrEmpty(imgName))
        {
            String[] imgs = images.split(",");
            String[] names = imgName.split(",");
            
            AVFile file = new AVFile(names[0], imgs[0],
                    new HashMap<String, Object>());
            String thumbUrl = file.getThumbnailUrl(true, 1920, 1080);
            
            ImageLoader.getInstance().displayImage(thumbUrl,
                    holder.imgContent,
                    options,
                    animateFirstListener);
            
        }
        else
        {
            if (getItemViewType(position) == 2)
            {
                
            }
            else
            {
                holder.imgContent.setImageResource(mContext.getResources()
                        .getIdentifier("ic_palette_0" + position % 4,
                                "mipmap",
                                mContext.getPackageName()));
            }
        }
        AVQuery<AVUser> userQuery = AVUser.getQuery();
        
        userQuery.getInBackground(mListData.get(position).getString("userId"),
                new GetCallback<AVUser>()
                {
                    @Override
                    public void done(AVUser avUser, AVException e)
                    {
                        if (null == e)
                        {
                            String userName = avUser.getString("nikename");
                            if (getItemViewType(position) == 2)
                            {
                                holder.title.setText(userName);
                            }
                            else
                            {
                                holder.userName.setText(userName);
                            }
                        }
                    }
                });
        
        holder.msgContent.setText(mListData.get(position).getString("message"));
        holder.descCount.setText(mListData.get(position)
                .getString("ReplyCount"));
        Date date = mListData.get(position).getCreatedAt();
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        if (getItemViewType(position) == 2)
        {
            holder.time.setText(sdf.format(date.getTime()));
        }
        else
        {
            holder.updateTime.setText(sdf.format(date.getTime()));
        }
        
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
            
            holder.title.setVisibility(getItemViewType(position) == 2 ? View.VISIBLE
                    : View.GONE);
        }
        else if (mFrom.equals("home"))
        {
            holder.title.setText(mListData.get(position)
                    .getString("notify_title"));
            holder.zanAndDesc.setVisibility(View.GONE);
        }
        
        if (getItemViewType(position) == 2)
        {
            holder.container.setOnClickListener(new View.OnClickListener()
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
        else
        {
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
        
    }
    
    @Override
    public int getItemCount()
    {
        return null == mListData ? 0 : mListData.size();
        //        return 10;
    }
    
    //    @Override
    //    public SwipeLayout getSwipLayout(float x, float y)
    //    {
    //        return (SwipeLayout) mRecycler.findChildViewUnder(x, y);
    //    }
    
    //    @Override
    //    public void onAttachedToRecyclerView(RecyclerView recyclerView)
    //    {
    //        super.onAttachedToRecyclerView(recyclerView);
    //        
    //        mRecycler = recyclerView;
    //        recyclerView.addOnItemTouchListener(new ItemHelpter(mContext, this));
    //    }
    //
    
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
        
        TextView time;
        
        CardView container;
        
        View zanAndDesc;
        
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
            time = itemView.findViewById(R.id.tv_time);
            container = itemView.findViewById(R.id.text_container);
            zanAndDesc = itemView.findViewById(R.id.zan_desc_layout);
        }
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
