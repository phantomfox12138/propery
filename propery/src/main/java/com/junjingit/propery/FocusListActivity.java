package com.junjingit.propery;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.junjingit.propery.common.FusionAction;
import com.junjingit.propery.fragment.FocusFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FocusListActivity extends AppCompatActivity
{
    private static final String TAG = "FocusListActivity";
    
    private List<AVObject> mFocusCycleList = new ArrayList<>();
    
    private FocusListAdapter mAdapter;
    
    private RecyclerView mFocusList;
    
    private Toolbar mToolbar;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus_list);
        
        initView();
        
        initData();
        
    }
    
    private void initData()
    {
        AVRelation<AVObject> relation = AVUser.getCurrentUser()
                .getRelation("cycle");
        
        relation.getQuery().findInBackground(new FindCallback<AVObject>()
        {
            @Override
            public void done(List<AVObject> list, AVException e)
            {
                if (null == e && null != list)
                {
                    Log.d(TAG, "count = " + list.size());
                    
                    mFocusCycleList.clear();
                    mFocusCycleList.addAll(list);
                    
                    mAdapter.notifyDataSetChanged();
                    
                }
            }
        });
        
    }
    
    private void initView()
    {
        mFocusList = (RecyclerView) findViewById(R.id.focus_list);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        
        setSupportActionBar(mToolbar);
        
        getSupportActionBar().setTitle("我的圈子");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        mFocusList.setLayoutManager(new LinearLayoutManager(this));
        
        mAdapter = new FocusListAdapter(this);
        mAdapter.setList(mFocusCycleList);
        
        mFocusList.setAdapter(mAdapter);
    }
    
    class FocusListAdapter extends RecyclerView.Adapter<FocusHolder>
    {
        private Context context;
        
        private List<AVObject> list;
        
        public void setList(List<AVObject> list)
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
        public void onBindViewHolder(final FocusHolder holder, int position)
        {
            final AVObject object = list.get(position);
            
            holder.name.setText(object.getString("cycle_name"));
            
            AVQuery<AVObject> query = new AVQuery<>("Public_Status");
            query.whereEqualTo("cycle_id", object.getObjectId());
            query.orderByDescending("createdAt");
            query.getFirstInBackground(new GetCallback<AVObject>()
            {
                @Override
                public void done(AVObject avObject, AVException e)
                {
                    if (null == e)
                    {
                        holder.msgPerview.setText(avObject.getString("message"));
                        Date date = avObject.getCreatedAt();
                        
                        SimpleDateFormat sdf = new SimpleDateFormat(
                                "yyyy-MM-dd hh:mm");
                        holder.updateTime.setText(sdf.format(date.getTime()));
                    }
                }
            });
            
            holder.item.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    String objId = object.getObjectId();
                    
                    Intent toFocusList = new Intent(
                            FusionAction.CYCLE_DETAIL_LIST_ACTION);
                    toFocusList.putExtra(FusionAction.FocusListExtra.OBJECT_ID,
                            objId);
                    
                    startActivity(toFocusList);
                }
            });
            
        }
        
        @Override
        public int getItemCount()
        {
            return null != list ? list.size() : 0;
        }
    }
    
    class FocusHolder extends RecyclerView.ViewHolder
    {
        ImageView icon;
        
        TextView name;
        
        TextView msgPerview;
        
        TextView updateTime;
        
        LinearLayout item;
        
        public FocusHolder(View itemView)
        {
            super(itemView);
            
            icon = itemView.findViewById(R.id.item_icon);
            name = itemView.findViewById(R.id.item_user_id);
            msgPerview = itemView.findViewById(R.id.item_msg_perview);
            updateTime = itemView.findViewById(R.id.item_time);
            item = itemView.findViewById(R.id.focus_item);
        }
        
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        //Toolbar上的左上角的返回箭头的键值为Android.R.id.home  不是R.id.home
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
                
        }
        
        return super.onOptionsItemSelected(item);
    }
}
