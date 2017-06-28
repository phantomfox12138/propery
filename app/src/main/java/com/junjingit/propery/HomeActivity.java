package com.junjingit.propery;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class HomeActivity extends AppCompatActivity
{
    
    private RecyclerView mHomeList;
    
    private Toolbar mToolbarTb;
    
    private BottomNavigationView mBottomNavi;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        initView();
    }
    
    private void initView()
    {
        mToolbarTb = (Toolbar) findViewById(R.id.tb_toolbar);
        mHomeList = (RecyclerView) findViewById(R.id.rv_content);
        mBottomNavi = (BottomNavigationView) findViewById(R.id.bnv_menu);
        
        mBottomNavi.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {


                return false;
            }
        });
        
        if (mToolbarTb != null)
        {
            mToolbarTb.setNavigationIcon(R.mipmap.ic_launcher_round);
            setSupportActionBar(mToolbarTb);
            
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        HomeListAdapter adapter = new HomeListAdapter(this);
        final LinearLayoutManager llm = new LinearLayoutManager(this);
        mHomeList.setLayoutManager(llm);
        
        mHomeList.setAdapter(adapter);
    }
}
