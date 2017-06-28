package com.junjingit.propery;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by niufan on 17/5/27.
 */

public class HomeListAdapter extends
        RecyclerView.Adapter<HomeListAdapter.HomeHolder>
{
    private Context mContext;
    
    private List<Status> mListData;
    
    public HomeListAdapter(Context context)
    {
        this.mContext = context;
    }
    
    @Override
    public HomeHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        HomeHolder holder = new HomeHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.home_item_layout, null));
        
        return holder;
    }
    
    @Override
    public void onBindViewHolder(HomeHolder holder, int position)
    {
        holder.imgContent.setImageResource(mContext.getResources()
                .getIdentifier("ic_palette_0" + position % 4,
                        "mipmap",
                        mContext.getPackageName()));
        //        holder.imgContent.setImageResource(R.mipmap.ic_palette_00);
    }
    
    @Override
    public int getItemCount()
    {
        //        return null == mListData ? 0 : mListData.size();
        return 10;
    }
    
    class HomeHolder extends RecyclerView.ViewHolder
    {
        ImageView icon;
        
        ImageView imgContent;
        
        TextView userName;
        
        TextView msgContent;
        
        public HomeHolder(View itemView)
        {
            super(itemView);
            
            icon = itemView.findViewById(R.id.iv_icon);
            userName = itemView.findViewById(R.id.tv_id);
            msgContent = itemView.findViewById(R.id.tv_content);
            imgContent = itemView.findViewById(R.id.iv_cover);
        }
    }
}
