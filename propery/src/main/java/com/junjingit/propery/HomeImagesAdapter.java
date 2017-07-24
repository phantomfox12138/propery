package com.junjingit.propery;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by niufan on 17/5/27.
 */

public class HomeImagesAdapter extends
        RecyclerView.Adapter<HomeImagesAdapter.ImageHolder>
{
    
    @Override
    public ImageHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return null;
    }
    
    @Override
    public void onBindViewHolder(ImageHolder holder, int position)
    {
        
    }
    
    @Override
    public int getItemCount()
    {
        return 0;
    }
    
    class ImageHolder extends RecyclerView.ViewHolder
    {
        
        public ImageHolder(View itemView)
        {
            super(itemView);
        }
    }
}
