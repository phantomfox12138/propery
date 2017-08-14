package com.junjingit.propery.circle;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.junjingit.propery.R;
import com.junjingit.propery.utils.MyImageLoader;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by jxy on 2017/8/10.
 */

public class MyFollowListAdapter extends RecyclerView.Adapter<MyFollowListAdapter.CircleHolder> {
    private Context mContext;
    private List<AVUser> mListData;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    public void setmListData(List<AVUser> mListData) {
        this.mListData = mListData;
    }

    public MyFollowListAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public CircleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CircleHolder circleHolder = new CircleHolder(LayoutInflater.from(mContext).inflate(R.layout.activity_myfollow_item, null));
        return circleHolder;
    }

    @Override
    public void onBindViewHolder(CircleHolder holder, int position) {
        final AVObject obj = mListData.get(position);
        String fansImg;
        String fansName;
        if(obj.get("user_icon_url")==null){
            fansImg="";
        }else {
            fansImg=obj.get("user_icon_url").toString();
        }
        if (obj.get("username") == null) {
            fansName = "";
        } else {
            fansName = obj.get("username").toString();
        }

        ImageLoader.getInstance().displayImage(fansImg, holder.follow_img, MyImageLoader.MyCircleDisplayImageOptions(), animateFirstListener);
        holder.follow_name.setText(fansName);
    }

    @Override
    public int getItemCount() {
         return null == mListData ? 0 : mListData.size();
    }

    class CircleHolder extends RecyclerView.ViewHolder {
        private ImageView follow_img;
        private TextView follow_name,haved_follow;
        public CircleHolder(View itemView) {
            super(itemView);
            follow_img = itemView.findViewById(R.id.follow_img);
            follow_name = itemView.findViewById(R.id.follow_name);
            haved_follow=itemView.findViewById(R.id.haved_follow);
        }
    }

    private class AnimateFirstDisplayListener extends SimpleImageLoadingListener {
        final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view,
                                      Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }
}
