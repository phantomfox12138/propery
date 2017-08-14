package com.junjingit.propery.circle;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.junjingit.propery.R;
import com.junjingit.propery.utils.MyImageLoader;
import com.junjingit.propery.utils.ToastUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/13 0013.
 */

public class FollowCircleFragment extends Fragment {
    public static final String TAG = "FollowCircleFragment";
    private SwipeMenuRecyclerView follow_circle_list;
    private SwipeRefreshLayout mRefresh;
    private View mRootView;
    private List<AVObject> myFollowCircle = new ArrayList<>();
    private ImageLoadingListener animateFirstListener = new FollowCircleFragment.AnimateFirstDisplayListener();
    FollowCircleAdapter followCircleAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_follow_circle, null);
            follow_circle_list = mRootView.findViewById(R.id.follow_circle_list);
            mRefresh = mRootView.findViewById(R.id.refresh_layout);
            initView();
            initData();
        }
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        return mRootView;
    }
    private void initView(){
        followCircleAdapter = new FollowCircleAdapter(getActivity(), myFollowCircle);
        follow_circle_list.setLayoutManager(new LinearLayoutManager(getActivity()));
        follow_circle_list.setAdapter(followCircleAdapter);
    }
    /**
     * 查询我所关注的圈子
     */
    private void initData() {
        String userObjectId = AVUser.getCurrentUser().getObjectId();
        AVQuery<AVObject> query = new AVQuery<>("cycle");
        query.orderByAscending("createdAt");
        query.whereContains("cycle_created_by", userObjectId);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null&&list!=null) {
                    ToastUtils.showToast(getActivity(),"当前关注的圈子数"+list.size());
                    myFollowCircle.clear();
                    myFollowCircle.addAll(list);
                    followCircleAdapter.notifyDataSetChanged();
                } else {
                    Log.v(TAG, "##################" + e);
                }
            }
        });
    }


    class FollowCircleAdapter extends RecyclerView.Adapter<FollowCircleFragment.CircleHolder> {
        private Context mContext;
        private List<AVObject> mListData;

        public FollowCircleAdapter(Context mContext, List<AVObject> mListData) {
            this.mContext = mContext;
            this.mListData = mListData;
        }

        @Override
        public CircleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            CircleHolder circleHolder = new CircleHolder(LayoutInflater.from(mContext).inflate(R.layout.activity_myfollow_item, null));
            return circleHolder;
        }

        @Override
        public void onBindViewHolder(CircleHolder holder, int position) {
            final AVObject obj = mListData.get(position);
            String circleImg;
            String circleName;
            String circleNum;
            if (obj.get("cycle_icon_url") == null) {
                circleImg = "";
            } else {
                circleImg = obj.get("cycle_icon_url").toString();
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
            ImageLoader.getInstance().displayImage(circleImg, holder.follow_img, MyImageLoader.DisplayImageOptions(), animateFirstListener);
            holder.follow_name.setText(circleName);
        }

        @Override
        public int getItemCount() {
            return mListData.size();
        }
    }

    class CircleHolder extends RecyclerView.ViewHolder {
        ImageView follow_img;
        TextView follow_name;
        TextView haved_follow;

        public CircleHolder(View itemView) {
            super(itemView);
            follow_img = itemView.findViewById(R.id.follow_img);
            follow_name = itemView.findViewById(R.id.follow_name);
            haved_follow = itemView.findViewById(R.id.haved_follow);
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
