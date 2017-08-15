package com.junjingit.propery;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.junjingit.propery.common.FusionAction;

public class HomeSendActivity extends AppCompatActivity
{
    
    private Toolbar mToolbar;
    
    private TextView mTitle;
    
    private String mType;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_send);
        
        initView();
    }
    
    private void initView()
    {
        mType = getIntent().getStringExtra(FusionAction.QuoteExtra.SEND_TYPE);
        
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitle = (TextView) findViewById(R.id.title);
        
        mTitle.setText(FusionAction.QuoteExtra.SEND_FIX.equals(mType) ? "设备报修"
                : "意见反馈");
        
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
