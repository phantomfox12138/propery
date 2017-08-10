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
        CircleHolder circleHolder = new CircleHolder(LayoutInflater.from(mContext).inflate(R.layout.activity_mycircle_item, null));
        return circleHolder;
    }

    @Override
    public void onBindViewHolder(CircleHolder holder, int position) {

        final AVObject obj = mListData.get(position);
        if (getmFrom().equals("myCircle")) {
            String circleImg;
            String circleName;
            String circleNum;
            if (obj.get("cycle_name") == null) {
                circleImg = "";
            } else {
                circleImg = obj.get("cycle_name").toString();
            }
            if (obj.get("cycle_name") == null) {
                circleName = "";
            } else {
                circleName = obj.get("cycle_name").toString();
            }
            if (obj.get("focus_count") == null) {
                circleNum = "";
            } else {
                circleNum = obj.get("focus_count").toString();
            }
            ImageLoader.getInstance().displayImage(circleImg, holder.circle_img,MyImageLoader.MyCircleDisplayImageOptions(), animateFirstListener);
            holder.circle_name.setText(circleName);
            holder.circle_number.setText(circleNum);
        }
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    class CircleHolder extends RecyclerView.ViewHolder {
        private ImageView circle_img;
        private TextView circle_name, circle_number;

        public CircleHolder(View itemView) {
            super(itemView);
            circle_img = itemView.findViewById(R.id.circle_img);
            circle_name = itemView.findViewById(R.id.circle_name);
            circle_number = itemView.findViewById(R.id.circle_number);
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
