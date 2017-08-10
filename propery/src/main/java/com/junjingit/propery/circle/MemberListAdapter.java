package com.junjingit.propery.circle;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVObject;
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

public class MemberListAdapter extends RecyclerView.Adapter<MemberListAdapter.CircleHolder> {

    private Context mContext;
    private List<AVObject> mListData;
    private String mFrom;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    public List<AVObject> getmListData() {
        return mListData;
    }

    public void setmListData(List<AVObject> mListData) {
        this.mListData = mListData;
    }

    public String getmFrom() {
        return mFrom;
    }

    public void setmFrom(String mFrom) {
        this.mFrom = mFrom;
    }

    public MemberListAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public CircleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CircleHolder circleHolder = new CircleHolder(LayoutInflater.from(mContext).inflate(R.layout.activity_mynember_item, null));
        return circleHolder;
    }

    @Override
    public void onBindViewHolder(CircleHolder holder, int position) {

        final AVObject obj = mListData.get(position);
            String circleImg;
            String circleName;
            if (obj.get("member_img") == null) {
                circleImg = "";
            } else {
                circleImg = obj.get("member_img").toString();
            }
            if (obj.get("member_name") == null) {
                circleName = "";
            } else {
                circleName = obj.get("member_name").toString();
            }

            ImageLoader.getInstance().displayImage(circleImg, holder.member_img,MyImageLoader.MyCircleDisplayImageOptions(), animateFirstListener);
            holder.member_name.setText(circleName);
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    class CircleHolder extends RecyclerView.ViewHolder {
        private ImageView member_img;
        private TextView member_name;

        public CircleHolder(View itemView) {
            super(itemView);
            member_img = itemView.findViewById(R.id.member_img);
            member_name = itemView.findViewById(R.id.member_name);
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
