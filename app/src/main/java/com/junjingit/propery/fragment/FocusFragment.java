package com.junjingit.propery.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.junjingit.propery.Focus;
import com.junjingit.propery.R;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FocusFragment extends Fragment
{
    private View mRootView;
    
    private RecyclerView mFocusList;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        if (mRootView == null)
        {
            mRootView = inflater.inflate(R.layout.fragment_focus, null);
            initView();
        }
        
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null)
        {
            parent.removeView(mRootView);
        }
        
        return mRootView;
    }
    
    private void initView()
    {
        mFocusList = mRootView.findViewById(R.id.focus_list);
        
        FocusListAdapter adapter = new FocusListAdapter(getActivity());
        mFocusList.setLayoutManager(new LinearLayoutManager(getActivity()));
        
        mFocusList.setAdapter(adapter);
    }
    
    class FocusListAdapter extends RecyclerView.Adapter<FocusHolder>
    {
        private Context context;
        
        private List<Focus> list;
        
        public void setList(List<Focus> list)
        {
            this.list = list;
        }
        
        public FocusListAdapter(Context context)
        {
            this.context = context;
        }
        
        @Override
        public FocusHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            FocusHolder holder = new FocusHolder(LayoutInflater.from(context)
                    .inflate(R.layout.focus_item_layout, null));
            
            return holder;
        }
        
        @Override
        public void onBindViewHolder(FocusHolder holder, int position)
        {
            
        }
        
        @Override
        public int getItemCount()
        {
            return 10;
            //            return null != list ? list.size() : 0;
        }
    }
    
    class FocusHolder extends RecyclerView.ViewHolder
    {
        ImageView icon;
        
        TextView userName;
        
        TextView msgPerview;
        
        TextView updateTime;
        
        public FocusHolder(View itemView)
        {
            super(itemView);
            
            icon = itemView.findViewById(R.id.item_icon);
            userName = itemView.findViewById(R.id.item_user_id);
            msgPerview = itemView.findViewById(R.id.item_msg_perview);
            updateTime = itemView.findViewById(R.id.item_time);
        }
        
    }
    
}
