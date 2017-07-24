package com.junjingit.propery;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class CycleActivity extends AppCompatActivity
{
    
    private Toolbar mToolbar;
    
    private RecyclerView mCycleList;
    
    private Button mCreateCycle;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cycle);
        
        initView();
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
    
}
