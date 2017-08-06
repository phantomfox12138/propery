package com.junjingit.propery;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;

/**
 * Created by niufan on 17/7/3.
 */

public class Utility
{
    
    public static void disableShiftMode(BottomNavigationView view)
    {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try
        {
            Field shiftingMode = menuView.getClass()
                    .getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++)
            {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                //noinspection RestrictedApi 
                item.setShiftingMode(false);
                // set once again checked value, so view will be updated 
                //noinspection RestrictedApi 
                item.setChecked(item.getItemData().isChecked());
            }
        }
        catch (NoSuchFieldException e)
        {
            Log.e("BNVHelper", "Unable to get shift mode field", e);
        }
        catch (IllegalAccessException e)
        {
            Log.e("BNVHelper", "Unable to change value of shift mode", e);
        }
    }
    
    /**
     * * 使状态栏透明 *
     * <p>
     * * 适用于图片作为背景的界面,此时需要图片填充到状态栏 *
     * <p>
     * * @param activity 需要设置的activity
     */
    public static void setTranslucent(Activity activity)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
    }
    
    public static Bitmap createImageThumbnail(String filePath)
    {
        Bitmap bitmap = null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, opts);
        
        opts.inSampleSize = computeSampleSize(opts, -1, 128 * 128);
        opts.inJustDecodeBounds = false;
        
        try
        {
            bitmap = BitmapFactory.decodeFile(filePath, opts);
        }
        catch (Exception e)
        {
            // TODO: handle exception  
        }
        return bitmap;
    }
    
    public static int computeSampleSize(BitmapFactory.Options options,
            int minSideLength, int maxNumOfPixels)
    {
        int initialSize = computeInitialSampleSize(options,
                minSideLength,
                maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8)
        {
            roundedSize = 1;
            while (roundedSize < initialSize)
            {
                roundedSize <<= 1;
            }
        }
        else
        {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }
    
    private static int computeInitialSampleSize(BitmapFactory.Options options,
            int minSideLength, int maxNumOfPixels)
    {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1
                : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128
                : (int) Math.min(Math.floor(w / minSideLength),
                        Math.floor(h / minSideLength));
        if (upperBound < lowerBound)
        {
            // return the larger one when there is no overlapping zone.  
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1))
        {
            return 1;
        }
        else if (minSideLength == -1)
        {
            return lowerBound;
        }
        else
        {
            return upperBound;
        }
    }
    
    /**
     * 获取状态栏高度
     *
     * @return 状态栏高度
     */
    public static int getBarHeight(Context context)
    {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 38;//默认为38，貌似大部分是这样的
        
        try
        {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = context.getResources().getDimensionPixelSize(x);
            
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
        }
        return sbar;
    }
    
}
