package com.junjingit.propery;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FollowCallback;
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
        
        //        AVUser.getCurrentUser().followInBackground("5925137fc1005c0053fd5a77",
        //                new FollowCallback()
        //                {
        //                    @Override
        //                    public void done(AVObject object, AVException e)
        //                    {
        //                        if (e == null)
        //                        {
        //                            //                            Log.i(TAG, "follow succeeded.");
        //                        }
        //                        else if (e.getCode() == AVException.DUPLICATE_VALUE)
        //                        {
        //                            //                            Log.w(TAG, "Already followed.");
        //                        }
        //                    }
        //                });
        
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
                    
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                            && ContextCompat.checkSelfPermission(WelcomeActivity.this,
                                    Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(WelcomeActivity.this,
                                new String[] { Manifest.permission.READ_PHONE_STATE },
                                0);
                    }
                    
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
