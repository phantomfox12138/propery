package com.junjingit.propery;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.junjingit.propery.common.FusionAction;

public class WelcomeActivity extends AppCompatActivity
{
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        
        initView();
        
    }
    
    private void initView()
    {
        new Thread()
        {
            
            @Override
            public void run()
            {
                super.run();
                
                try
                {
                    sleep(3000);
                    
                    if (null == AVUser.getCurrentUser())
                    {
                        startActivity(new Intent(FusionAction.LOGIN_ACTION));
                    }
                    else
                    {
                        startActivity(new Intent(FusionAction.HOME_PAGE_ACTION));
                    }
                    
                    finish();
                    
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    
}
