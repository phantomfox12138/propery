package com.junjingit.propery.utils;

import com.junjingit.propery.ProperyApplication;
import com.junjingit.propery.R;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * Created by jxy on 2017/8/9.
 */

public class MyImageLoader {
    private static ImageLoaderConfiguration config;
    static {
        config = new ImageLoaderConfiguration.Builder(ProperyApplication.app.getApplicationContext())
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .discCacheFileCount(60)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();
        ImageLoader.getInstance().init(config);
    }

    public static DisplayImageOptions MyDisplayImageOptions(){
        DisplayImageOptions options =new DisplayImageOptions.Builder()
                //.cloneFrom(Define.NORMAL_OPTIONS)//正常显示图片
                .showImageForEmptyUri(R.mipmap.user_default)//设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.user_default)  //设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .build();//构建完成
        return options;
    }

    public static DisplayImageOptions DisplayImageOptions() {
        DisplayImageOptions options =new DisplayImageOptions.Builder()
                //.cloneFrom(Define.NORMAL_OPTIONS)//正常显示图片
                .showImageForEmptyUri(R.mipmap.circle)//设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.circle)  //设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .build();//构建完成
        return options;
    }
    public static DisplayImageOptions MyCircleDisplayImageOptions(){
        DisplayImageOptions options =new DisplayImageOptions.Builder()
               // .cloneFrom(Define.CIRCULAR_OPTIONS)//圆形显示
                .showImageForEmptyUri(R.mipmap.user_default)//设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.user_default)  //设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .build();//构建完成
        return options;
    }

    public static DisplayImageOptions MyNormalCoverImageOptions(){
        DisplayImageOptions options =new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.mipmap.header)//设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.header)  //设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .build();//构建完成
        return options;
    }
}
