package com.junjingit.propery;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class CycleActivity extends AppCompatActivity
{
    
    private static final String TAG = "CycleActivity";
    
    private Toolbar mToolbar;
    
    private RecyclerView mCycleList;
    
    private Button mCreateCycle;
    
    private List<AVObject> mCycleDataList = new ArrayList<>();
    
    private CycleListAdapter mCycleListAdapter;
    
    private String flag = "";
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cycle);
        
        initView();
        
        initData();
    }
    
    private void initData()
    {
        AVQuery<AVObject> query = new AVQuery<>("cycle");
        query.whereExists("objectId");
        query.findInBackground(new FindCallback<AVObject>()
        {
            @Override
            public void done(List<AVObject> list, AVException e)
            {
                if (null == e)
                {
                    mCycleDataList.clear();
                    mCycleDataList.addAll(list);
                    
                    mCycleListAdapter.notifyDataSetChanged();
                }
            }
        });
    }
    
    private void initView()
    {
        mToolbar = (Toolbar) findViewById(R.id.cycle_toolbar);
        mCycleList = (RecyclerView) findViewById(R.id.cycle_list);
        mCreateCycle = (Button) findViewById(R.id.create_cycle);
        
        mCreateCycle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent("com.junjing.propery.CREATE_CYCLE"));
            }
        });
        
        mToolbar.setNavigationIcon(R.drawable.avoscloud_search_actionbar_back);
        
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });
        
        mCycleListAdapter = new CycleListAdapter();
        mCycleListAdapter.setDatalist(mCycleDataList);
        
        mCycleList.setLayoutManager(new LinearLayoutManager(this));
        mCycleList.setAdapter(mCycleListAdapter);
        
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.cycle_search, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.ab_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        
        return true;
    }
    
    class CycleListAdapter extends RecyclerView.Adapter<CycleHolder>
    {
        
        private List<AVObject> mDatalist;
        
        //        private
        
        public void setDatalist(List<AVObject> datalist)
        {
            this.mDatalist = datalist;
        }
        
        @Override
        public CycleHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            CycleHolder holder = new CycleHolder(
                    LayoutInflater.from(CycleActivity.this)
                            .inflate(R.layout.cycle_item_layout, null));
            
            return holder;
        }
        
        @Override
        public void onBindViewHolder(final CycleHolder holder, int position)
        {
            final AVObject obj = mDatalist.get(position);
            
            holder.cycleName.setText(obj.getString("cycle_name"));
            
            holder.cycleFocusCount.setText(null == obj.get("focus_count") ? 0 + "人关注"
                    : obj.get("focus_count") + "人关注");
            
            //判断当前用户是否已经关注了哪些圈子
            AVRelation<AVObject> relation = AVUser.getCurrentUser()
                    .getRelation("cycle");
            relation.getQuery().findInBackground(new FindCallback<AVObject>()
            {
                @Override
                public void done(List<AVObject> list, AVException e)
                {
                    for (int i = 0; i < list.size(); i++)
                    {
                        if (obj.getObjectId().equals(list.get(i).getObjectId()))
                        {
                            holder.cycleFocusBtn.setTextColor(getResources().getColor(android.R.color.darker_gray,
                                    null));
                            holder.cycleFocusBtn.setText("取消关注");
                            
                            holder.isFocused = true;
                        }
                        else
                        {
                            holder.cycleFocusBtn.setTextColor(getResources().getColor(R.color.blue,
                                    null));
                            holder.cycleFocusBtn.setText("关注");
                            
                            holder.isFocused = false;
                        }
                    }
                }
            });
            
            holder.cycleFocusBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    
                    Log.d(TAG, "focus_count = " + obj.get("focus_count"));
                    
                    final AVUser user = AVUser.getCurrentUser();
                    final AVRelation<AVObject> userRelation = user.getRelation("cycle");
                    
                    AVQuery<AVObject> query = userRelation.getQuery();
                    query.findInBackground(new FindCallback<AVObject>()
                    {
                        @Override
                        public void done(List<AVObject> list, AVException e)
                        {
                            if (null == e)
                            {
                                focusCycle(userRelation, user, obj, holder);
                            }
                        }
                    });
                    
                }
            });
            
            String iconPaths = obj.getString("cycle_icon");
            if (StringUtil.isNullOrEmpty(iconPaths))
            {
                holder.cycleIcon.setImageResource(R.mipmap.ic_launcher_round);
            }
            else
            {
                String[] iconUrls = iconPaths.split(",");
                
                //TODO:下载圈子图片
            }
            
        }
        
        //关注圈子
        private void focusCycle(AVRelation<AVObject> userRelation, AVUser user,
                final AVObject cycleObj, final CycleHolder holder)
                throws IllegalArgumentException
        {
            //用户已关注了圈子
            if (!holder.isFocused)
            {
                userRelation.add(cycleObj);
                
                user.saveInBackground(new SaveCallback()
                {
                    @Override
                    public void done(AVException e)
                    {
                        if (null == e)
                        {
                            
                            cycleObj.increment("focus_count");
                            cycleObj.saveInBackground(new SaveCallback()
                            {
                                @Override
                                public void done(AVException e)
                                {
                                    if (null == e)
                                    {
                                        Toast.makeText(CycleActivity.this,
                                                "关注成功",
                                                Toast.LENGTH_LONG).show();
                                        
                                        holder.cycleFocusBtn.setText("取消关注");
                                        holder.cycleFocusBtn.setTextColor(getResources().getColor(android.R.color.darker_gray,
                                                null));
                                        
                                        holder.isFocused = true;
                                        
                                        notifyDataSetChanged();
                                    }
                                }
                            });
                            
                        }
                    }
                });
                
            }
            else
            {
                userRelation.remove(cycleObj);
                
                user.saveInBackground(new SaveCallback()
                {
                    @Override
                    public void done(AVException e)
                    {
                        if (null == e)
                        {
                            
                            cycleObj.increment("focus_count", -1);
                            cycleObj.saveInBackground(new SaveCallback()
                            {
                                @Override
                                public void done(AVException e)
                                {
                                    if (null == e)
                                    {
                                        Toast.makeText(CycleActivity.this,
                                                "取消成功",
                                                Toast.LENGTH_LONG).show();
                                        
                                        holder.cycleFocusBtn.setText("关注");
                                        holder.cycleFocusBtn.setTextColor(getResources().getColor(R.color.blue,
                                                null));
                                        
                                        holder.isFocused = false;
                                        
                                        notifyDataSetChanged();
                                    }
                                }
                            });
                            
                        }
                    }
                });
            }
            
        }
        
        @Override
        public int getItemCount()
        {
            return null != mDatalist ? mDatalist.size() : 0;
        }
    }
    
    class CycleHolder extends RecyclerView.ViewHolder
    {
        ImageView cycleIcon;
        
        TextView cycleName;
        
        TextView cycleFocusCount;
        
        TextView cycleFocusBtn;
        
        boolean isFocused = false;
        
        public CycleHolder(View itemView)
        {
            super(itemView);
            
            cycleIcon = itemView.findViewById(R.id.cycle_icon);
            cycleName = itemView.findViewById(R.id.cycle_name);
            cycleFocusCount = itemView.findViewById(R.id.cycle_focus_count);
            cycleFocusBtn = itemView.findViewById(R.id.focus_btn);
            
        }
    }
}
