package com.junjingit.propery.circle;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.widget.ListItemDecoration;

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
    private SwipeRefreshLayout swipe_layout;
    private View mRootView;
    private List<AVObject> myFollowCircle = new ArrayList<>();
    private ImageLoadingListener animateFirstListener = new FollowCircleFragment.AnimateFirstDisplayListener();
    FollowCircleAdapter followCircleAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_follow_circle, null);
            follow_circle_list = mRootView.findViewById(R.id.follow_circle_list);
            swipe_layout = mRootView.findViewById(R.id.swipe_layout);
            swipe_layout.setOnRefreshListener(mRefreshListener);//刷新监听

            follow_circle_list.setLayoutManager(new LinearLayoutManager(getActivity()));
            follow_circle_list.addItemDecoration(new ListItemDecoration(ContextCompat.getColor(getActivity(), R.color.divider_color)));

            // follow_user_list.useDefaultLoadMore(); //使用默认的加载更多的View。
            // follow_user_list.setLoadMoreListener(mLoadMoreListener); // 加载更多的监听。

            followCircleAdapter = new FollowCircleAdapter(getActivity(), myFollowCircle);
            follow_circle_list.setAdapter(followCircleAdapter);
            //请求服务端数据
            initData();
        }
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        return mRootView;
    }
    /**
     * 下拉刷新
     */
    private SwipeRefreshLayout.OnRefreshListener mRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            initData();
        }
    };
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
                    Log.v(TAG,"################打印当前的关注圈子数"+list.size());
                    myFollowCircle.clear();
                    myFollowCircle.addAll(list);
                    followCircleAdapter.notifyDataSetChanged();
                    swipe_layout.setRefreshing(false);
                    // 第一次加载数据：一定要调用这个方法，否则不会触发加载更多。
                    // 第一个参数：表示此次数据是否为空，假如你请求到的list为空(== null || list.size == 0)，那么这里就要true。
                    // 第二个参数：表示是否还有更多数据，根据服务器返回给你的page等信息判断是否还有更多，这样可以提供性能，如果不能判断则传true。
                    follow_circle_list.loadMoreFinish(false, true);
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
