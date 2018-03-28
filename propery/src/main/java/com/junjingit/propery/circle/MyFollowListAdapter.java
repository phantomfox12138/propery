package com.junjingit.propery.circle;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.avos.avoscloud.AVObject;
import com.junjingit.propery.R;
import com.junjingit.propery.utils.MyImageLoader;
import com.junjingit.propery.utils.ToastUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
/**
 * Created by jxy on 2017/8/10.
 */
public class MyFollowListAdapter extends RecyclerView.Adapter<MyFollowListAdapter.CircleHolder> {
    private static  final  String TAG="MyFollowListAdapter";
    private Context mContext;
    private List<AVObject> mListData;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    private List<UserItem> userList;

    public MyFollowListAdapter(Context mContext,List<AVObject> mListData,List<UserItem> userItemList) {
        this.mContext = mContext;
        this.mListData=mListData;
        this.userList=userItemList;
    }

    @Override
    public CircleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CircleHolder circleHolder = new CircleHolder(LayoutInflater.from(mContext).inflate(R.layout.activity_myfollow_item, null));
        return circleHolder;
    }

    @Override
    public void onBindViewHolder(CircleHolder holder, int position) {
        if(userList.size()>0){
            final UserItem obj = userList.get(position);
            String fansImg="";
            String fansName="";
            fansName=obj.getUserName().toString();
            fansImg=obj.getUser_img().toString();
            String currentStr=obj.getSortLetters().toString();
            String previewStr=(position - 1) >= 0?userList.get(position - 1).getSortLetters().toString() : " ";
            if (!previewStr.equals(currentStr)) {
                holder.alpha.setVisibility(View.VISIBLE);
                holder.alpha.setText(currentStr);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
               // lp.setMargins(0, 20, 0, 0);
               // lp
                holder.item_layout.setLayoutParams(lp);
            } else {
                holder.alpha.setVisibility(View.GONE);
            }
            ImageLoader.getInstance().displayImage(fansImg, holder.follow_img, MyImageLoader.MyCircleDisplayImageOptions(), animateFirstListener);
            holder.follow_name.setText(fansName);

        }

    }

    @Override
    public int getItemCount() {
        return userList == null ? 0 : userList.size();
    }

    class CircleHolder extends RecyclerView.ViewHolder {
        private RelativeLayout item_layout;
        private ImageView follow_img;
        private TextView  alpha,follow_name,haved_follow;
        public CircleHolder(View itemView) {
            super(itemView);
            alpha=itemView.findViewById(R.id.alpha);
            follow_img = itemView.findViewById(R.id.follow_img);
            follow_name = itemView.findViewById(R.id.follow_name);
            haved_follow=itemView.findViewById(R.id.haved_follow);
            item_layout=itemView.findViewById(R.id.item_layout);
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
