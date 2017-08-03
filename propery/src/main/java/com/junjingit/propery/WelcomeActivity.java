package com.junjingit.propery;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

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
        
        //        AVUser.getCurrentUser().followInBackground("59798d6f8d6d810058a97981",
        //                new FollowCallback()
        //                {
        //                    @Override
        //                    public void done(AVObject avObject, AVException e)
        //                    {
        //                        if (null == e)
        //                        {
        //                            Toast.makeText(WelcomeActivity.this,
        //                                    "关注成功",
        //                                    Toast.LENGTH_LONG).show();
        //                        }
        //                        else if (e.getCode() == AVException.DUPLICATE_VALUE)
        //                        {
        //                            Toast.makeText(WelcomeActivity.this,
        //                                    "不可重复关注",
        //                                    Toast.LENGTH_LONG).show();
        //                        }
        //                    }
        //                    
        //                });
        
    }
    
    private void initView()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(WelcomeActivity.this,
                        Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(WelcomeActivity.this,
                    new String[] { Manifest.permission.READ_PHONE_STATE },
                    0x123);
        }
        else
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
                            startActivity(new Intent(
                                    FusionAction.HOME_PAGE_ACTION));
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
    
    @Override
    public void onRequestPermissionsResult(int requestCode,
            @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == 0x123)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE))
                {
                    //                    AskForPermission();
                    
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
                                    
                                    startActivity(new Intent(
                                            FusionAction.LOGIN_ACTION));
                                }
                                else
                                {
                                    startActivity(new Intent(
                                            FusionAction.HOME_PAGE_ACTION));
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
        }
    }
}
