package com.junjingit.propery;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;

public class CreateCycleActivity extends AppCompatActivity
{
    
    private TextView mCreateEdit;
    
    private Button mCreateBtn;
    
    private Toolbar mToolbar;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_cycle);
        
        initView();
    }
    
    private void initView()
    {
        mCreateEdit = (EditText) findViewById(R.id.cycle_name);
        mCreateBtn = (Button) findViewById(R.id.create_cycle_btn);
        mToolbar = (Toolbar) findViewById(R.id.create_cycle_toolbar);
        
        setSupportActionBar(mToolbar);
        
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        mCreateBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String cycleName = mCreateEdit.getText().toString();
                
                final AVObject object = new AVObject("cycle");
                object.put("cycle_name", cycleName);
                object.put("cycle_created_by", AVUser.getCurrentUser()
                        .getObjectId());
                
                object.saveInBackground(new SaveCallback()
                {
                    @Override
                    public void done(AVException e)
                    {
                        if (null == e)
                        {
                            Toast.makeText(CreateCycleActivity.this,
                                    "创建成功",
                                    Toast.LENGTH_LONG).show();
                            
                            AVUser user = AVUser.getCurrentUser();
                            
                            user.getRelation("cycle").add(object);
                            user.saveInBackground(new SaveCallback()
                            {
                                @Override
                                public void done(AVException e)
                                {
                                    if (null == e)
                                    {
                                        object.increment("focus_count");
                                        object.saveInBackground();
                                        
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
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
