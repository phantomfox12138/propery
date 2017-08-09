package com.junjingit.propery;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.junjingit.propery.common.FusionAction;
import com.xander.panel.PanelInterface;
import com.xander.panel.XanderPanel;

public class ProperyPayActivity extends AppCompatActivity
{
    private Toolbar mToolbar;
    
    private XanderPanel.Builder mBuilder;
    
    private View mPayBtn;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_propery_pay);
        
        initView();
        
        initData();
    }
    
    private void initData()
    {
        //TODO:获取数据
        String title = getIntent().getStringExtra(FusionAction.HomeListExtra.TITLE_NAME);
        getSupportActionBar().setTitle(title);
    }
    
    private void initView()
    {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mPayBtn = findViewById(R.id.pay_btn);
        
        mBuilder = new XanderPanel.Builder(this);
        mBuilder.grid(2, 3)
                .setMenu(R.menu.pay_menu,
                        new PanelInterface.PanelMenuListener()
                        {
                            @Override
                            public void onMenuClick(MenuItem menuItem)
                            {
                                Toast.makeText(ProperyPayActivity.this,
                                        "Grid MenuItem click "
                                                + menuItem.getTitle(),
                                        Toast.LENGTH_SHORT).show();
                                
                                mBuilder.setCancelable(true);
                                
                            }
                        })
                .setGravity(Gravity.BOTTOM)
                .setCanceledOnTouchOutside(true);
        mPayBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mBuilder.show();
            }
        });
        
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
}
