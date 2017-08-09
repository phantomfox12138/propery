package com.junjingit.propery;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created by niufan on 17/5/27.
 */

public class ProperyApplication extends Application
{
    public static ProperyApplication app;
    @Override
    public void onCreate()
    {
        super.onCreate();
        app = (ProperyApplication) this.getApplicationContext();
        AVOSCloud.initialize(this,
                "VgKn8AnKBNQo3y8vqYsLj330-gzGzoHsz",
                "ycNC55H0XuW6DVA7PqMbIYph");
        
        ImageLoaderConfiguration config = ImageLoaderConfiguration.createDefault(this);
        
        ImageLoader.getInstance().init(config);
    }
}
