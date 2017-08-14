package com.junjingit.propery;

import static com.junjingit.propery.common.FusionAction.HomeListExtra.ORDER_TYPE;
import static com.junjingit.propery.common.FusionAction.HomeListExtra.TITLE_NAME;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import ru.p3tr0vich.widget.ExpansionPanel;
import ru.p3tr0vich.widget.ExpansionPanelListener;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.junjingit.propery.common.FusionAction;
import com.xander.panel.PanelInterface;
import com.xander.panel.XanderPanel;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

public class HomeOrderListActivity extends AppCompatActivity
{
    
    private Toolbar mToolbar;
    
    private TextView mTitle;
    
    private String mType;
    
    private String mTitleName;
    
    private SwipeMenuRecyclerView mList;
    
    private OrderListAdapter mAdapter;
    
    private XanderPanel.Builder mBuilder;
    
    private List<AVObject> mOrderList = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_order_list);
        
        mType = getIntent().getStringExtra(ORDER_TYPE);
        mTitleName = getIntent().getStringExtra(TITLE_NAME);
        
        initView();
        
        initData();
        
    }
    
    private void initData()
    {
        boolean flag = mType.equals(FusionAction.HomeListExtra.ORDER_TYPE_PROPERY);
        
        AVQuery<AVObject> query = new AVQuery<>(flag ? "ProperyPayOrder"
                : "ElecPayOrder");
        
        query.whereEqualTo("userId", AVUser.getCurrentUser().getObjectId());
        
        query.findInBackground(new FindCallback<AVObject>()
        {
            @Override
            public void done(List<AVObject> list, AVException e)
            {
                if (null == e)
                {
                    mOrderList.clear();
                    mOrderList.addAll(list);
                    
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }
    
    private void initView()
    {
        mToolbar = (Toolbar) findViewById(R.id.order_toolbar);
        mList = (SwipeMenuRecyclerView) findViewById(R.id.order_list);
        mTitle = (TextView) findViewById(R.id.title);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        mBuilder = new XanderPanel.Builder(this);
        mBuilder.grid(2, 3)
                .setMenu(R.menu.pay_menu,
                        new PanelInterface.PanelMenuListener()
                        {
                            @Override
                            public void onMenuClick(MenuItem menuItem)
                            {
                                Toast.makeText(HomeOrderListActivity.this,
                                        "Grid MenuItem click "
                                                + menuItem.getTitle(),
                                        Toast.LENGTH_SHORT).show();
                                
                                mBuilder.setCancelable(true);
                                
                            }
                        })
                .setGravity(Gravity.BOTTOM)
                .setCanceledOnTouchOutside(true);
        
        mTitle.setText(mTitleName);
        
        mList.setLayoutManager(new LinearLayoutManager(this));
        
        mAdapter = new OrderListAdapter();
        mAdapter.setType(mType);
        
        mAdapter.setList(mOrderList);
        
        mList.setAdapter(mAdapter);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        
        switch (item.getItemId())
        {
        
            case android.R.id.home:
                
                finish();
                
                break;
        
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    class OrderListAdapter extends RecyclerView.Adapter<OrderHolder>
    {
        List<AVObject> list;
        
        String type;
        
        public void setType(String type)
        {
            this.type = type;
        }
        
        public void setList(List<AVObject> list)
        {
            this.list = list;
        }
        
        @Override
        public int getItemViewType(int position)
        {
            if (type.equals(FusionAction.HomeListExtra.ORDER_TYPE_PROPERY))
            {
                return 2;
            }
            
            return super.getItemViewType(position);
        }
        
        @Override
        public OrderHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            OrderHolder holder;
            
            if (viewType == 2)
            {
                holder = new OrderHolder(
                        LayoutInflater.from(HomeOrderListActivity.this)
                                .inflate(R.layout.order_list_panel, null));
            }
            else
            {
                holder = new OrderHolder(
                        LayoutInflater.from(HomeOrderListActivity.this)
                                .inflate(R.layout.order_list_item, null));
            }
            
            return holder;
        }
        
        @Override
        public void onBindViewHolder(final OrderHolder holder, int position)
        {
            final AVObject object = list.get(position);
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            holder.orderTime.setText(sdf.format(object.getCreatedAt()));
            
            if (getItemViewType(position) == 2)
            {
                holder.orderName.setText("物业账单");
                
                final float properyPay = object.getNumber("properyPay")
                        .floatValue();
                final float properyPark = object.getNumber("properyPark")
                        .floatValue();
                final float properyRent = object.getNumber("properyRent")
                        .floatValue();
                final float properyWater = object.getNumber("properyWater")
                        .floatValue();
                
                holder.orderCost.setText(String.valueOf(properyPay
                        + properyPark + properyRent + properyWater));
                
                holder.panel.hideStateIcon(true);
                
                holder.orderPayButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        mBuilder.show();
                    }
                });
                
                holder.panel.setListener(new ExpansionPanelListener()
                {
                    @Override
                    public void onExpanding(ExpansionPanel panel)
                    {
                        
                    }
                    
                    @Override
                    public void onCollapsing(ExpansionPanel panel)
                    {
                        
                    }
                    
                    @Override
                    public void onExpanded(ExpansionPanel panel)
                    {
                        holder.properyCost.setText(String.valueOf(properyPay));
                        holder.parkCost.setText(String.valueOf(properyPark));
                        holder.rentCost.setText(String.valueOf(properyRent));
                        holder.waterCost.setText(String.valueOf(properyWater));
                        
                        SimpleDateFormat sdf = new SimpleDateFormat(
                                "yyyy-MM-dd HH:mm:ss");
                        holder.orderTimeExpanded.setText(sdf.format(object.getCreatedAt()));
                        
                        holder.orderCostExpanded.setText(String.valueOf(properyPay
                                + properyPark + properyRent + properyWater));
                    }
                    
                    @Override
                    public void onCollapsed(ExpansionPanel panel)
                    {
                        
                    }
                });
            }
            else
            {
                holder.orderName.setText("电费账单");
                holder.orderPayButton.setVisibility(View.GONE);
                holder.orderCost.setVisibility(View.VISIBLE);
                
                holder.orderCost.setText(String.valueOf(object.getNumber("elecPay")));
            }
        }
        
        @Override
        public int getItemCount()
        {
            return null == list ? 0 : list.size();
        }
    }
    
    class OrderHolder extends RecyclerView.ViewHolder
    {
        
        TextView orderName;
        
        TextView orderTime;
        
        TextView orderCost;
        
        ExpansionPanel panel;
        
        TextView orderCostExpanded;
        
        TextView properyCost;
        
        TextView rentCost;
        
        TextView parkCost;
        
        TextView waterCost;
        
        TextView orderTimeExpanded;
        
        Button orderPayButton;
        
        public OrderHolder(View itemView)
        {
            super(itemView);
            
            orderName = itemView.findViewById(R.id.order_name);
            orderTime = itemView.findViewById(R.id.order_time);
            orderCost = itemView.findViewById(R.id.order_cost);
            panel = itemView.findViewById(R.id.expansion_panel_dialog);
            orderPayButton = itemView.findViewById(R.id.item_pay_btn);
            
            orderCostExpanded = itemView.findViewById(R.id.order_cost_expanded);
            properyCost = itemView.findViewById(R.id.propery_cost);
            rentCost = itemView.findViewById(R.id.rent_cost);
            parkCost = itemView.findViewById(R.id.park_cost);
            waterCost = itemView.findViewById(R.id.water_cost);
            orderTimeExpanded = itemView.findViewById(R.id.order_time_expanded);
            
        }
    }
}
