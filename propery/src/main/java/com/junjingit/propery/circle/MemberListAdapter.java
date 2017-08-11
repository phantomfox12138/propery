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
import com.junjingit.propery.R;
import com.junjingit.propery.utils.MyImageLoader;
import com.junjingit.propery.utils.ToastUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
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
    private ArrayList<String> slientList;
    IClickListener iClickListener;

    public List<AVObject> getmListData() {
        return mListData;
    }

    public void setmListData(List<AVObject> mListData, ArrayList<String> slientList) {
        this.mListData = mListData;
        this.slientList = slientList;
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
        Log.v("MemberListActivity", "onBindViewHolder"+position);
        String currentMemberId = obj.getObjectId().toString();//得到当前成员的id
        String circleImg;
        String circleName;
        iClickListener.changeMenuType(position);
       /* if(slientList!=null){
            for (int i=0;i<slientList.size();i++){
                String currentValue=slientList.get(i).toString();
                if(currentMemberId.equals(currentValue)){
                    //说明该成员已经被禁言
                    iClickListener.changeMenuType(position);
                }
            }
        }*/
        if (obj.get("member_img") == null) {
            circleImg = "";
        } else {
            circleImg = obj.get("member_img").toString();
        }
        if (obj.get("username") == null) {
            circleName = "";
        } else {
            circleName = obj.get("username").toString();
        }

        ImageLoader.getInstance().displayImage(circleImg, holder.member_img, MyImageLoader.MyCircleDisplayImageOptions(), animateFirstListener);
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
    public interface IClickListener{
        void changeMenuType(int type);
    }
    public void setIClickListener(IClickListener iClickListener){
        this.iClickListener = iClickListener;
    }
}
