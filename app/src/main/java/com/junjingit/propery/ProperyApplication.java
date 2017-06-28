package com.junjingit.propery;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;

/**
 * Created by niufan on 17/5/27.
 */

public class ProperyApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        
        AVOSCloud.initialize(this,
                "VgKn8AnKBNQo3y8vqYsLj330-gzGzoHsz",
                "ycNC55H0XuW6DVA7PqMbIYph");
    }
}
