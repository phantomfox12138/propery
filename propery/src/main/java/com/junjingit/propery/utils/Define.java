package com.junjingit.propery.utils;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

/**
 * Created by jxy on 2017/8/9.
 * 定义图片样式
 */

public class Define {
    /**
     * imageloader图片处理属性(正常)
     */
    public final static DisplayImageOptions NORMAL_OPTIONS = new DisplayImageOptions.Builder()
            .cacheInMemory(true).bitmapConfig(Bitmap.Config.RGB_565)
            .cacheOnDisc(true).considerExifParams(true)
            .displayer(new FadeInBitmapDisplayer(300)).build();

    /**
     * imageloader图片处理属性(圆形)
     */
    public final static DisplayImageOptions CIRCULAR_OPTIONS = new DisplayImageOptions.Builder()
            .cacheInMemory(true).bitmapConfig(Bitmap.Config.RGB_565)
            .cacheOnDisc(true).considerExifParams(true).build();
           // .displayer(new CircularBitmapDisplayer()).build();

    /**
     * imageloader图片处理属性(圆角)
     */
    public final static DisplayImageOptions ROOUND_OPTIONS = new DisplayImageOptions.Builder()
            .cacheInMemory(true).bitmapConfig(Bitmap.Config.RGB_565)
            .cacheOnDisc(true).considerExifParams(true)
            .displayer(new RoundedBitmapDisplayer(7)).build();// 图片圆角7度
}
