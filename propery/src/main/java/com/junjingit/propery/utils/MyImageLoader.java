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

    public static DisplayImageOptions MyDisplayImageOptions() {
        DisplayImageOptions options =DisplayImageOptions.createSimpleBuild()
                .cloneFrom(Define.NORMAL_OPTIONS)//正常显示图片
                .showImageForEmptyUri(R.mipmap.ic_launcher)//设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.ic_launcher)  //设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .build();//构建完成
        return options;
    }

    public static DisplayImageOptions DisplayImageOptions() {
        DisplayImageOptions options =DisplayImageOptions.createSimpleBuild()
                .cloneFrom(Define.NORMAL_OPTIONS)//正常显示图片
                .showImageForEmptyUri(R.mipmap.ic_launcher)//设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.ic_launcher)  //设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .build();//构建完成
        return options;
    }

    public static DisplayImageOptions MyCircleDisplayImageOptions() {
        DisplayImageOptions options =DisplayImageOptions.createSimpleBuild()
                .cloneFrom(Define.CIRCULAR_OPTIONS)//圆形显示
                .showImageForEmptyUri(R.mipmap.ic_launcher)//设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.ic_launcher)  //设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .build();//构建完成
        return options;
    }
}
