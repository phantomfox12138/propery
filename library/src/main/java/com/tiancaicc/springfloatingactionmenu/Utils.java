package com.tiancaicc.springfloatingactionmenu;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.DimenRes;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.readystatesoftware.systembartint.SystemBarTintManager;

/**
 * Copyright (C) 2016 tiancaiCC
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
public class Utils
{
    
    private static final String TAG = "Utils";
    
    public static int getDimension(Context context, @DimenRes int id)
    {
        return context.getResources().getDimensionPixelSize(id);
    }
    
    public static final int dpToPx(float dp, Resources res)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp,
                res.getDisplayMetrics());
    }
    
    public static final ViewGroup.MarginLayoutParams createLayoutParams(
            int width, int height, Resources res)
    {
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(
                width, height);
        
        params.bottomMargin = dpToPx(24, res);
        
        return params;
        
    }
    
    public static final ViewGroup.MarginLayoutParams createMatchParams(
            Resources res)
    {
        return createLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                res);
    }
    
    public static final ViewGroup.MarginLayoutParams createWrapParams(
            Resources res)
    {
        return createLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                res);
    }
    
    public static final ViewGroup.MarginLayoutParams createWrapMatchParams(
            Resources res)
    {
        return createLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                res);
    }
    
    public static final ViewGroup.MarginLayoutParams createMatchWrapParams(
            Resources res)
    {
        return createLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                res);
    }
    
    public static void setInsets(Activity context, View view)
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            return;
        SystemBarTintManager tintManager = new SystemBarTintManager(context);
        SystemBarTintManager.SystemBarConfig config = tintManager.getConfig();
        view.setPadding(0,
                config.getPixelInsetTop(false),
                config.getPixelInsetRight(),
                config.getPixelInsetBottom());
    }
    
    public static int getInsetsTop(Activity context, View view)
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            return 0;
        SystemBarTintManager tintManager = new SystemBarTintManager(context);
        SystemBarTintManager.SystemBarConfig config = tintManager.getConfig();
        return config.getPixelInsetTop(false);
    }
    
    public static int getInsetsBottom(Activity context, View view)
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            return 0;
        SystemBarTintManager tintManager = new SystemBarTintManager(context);
        SystemBarTintManager.SystemBarConfig config = tintManager.getConfig();
        return config.getPixelInsetBottom();
    }
}
