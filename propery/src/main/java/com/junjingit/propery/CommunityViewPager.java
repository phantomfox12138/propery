package com.junjingit.propery;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by niufan on 17/7/10.
 */

public class CommunityViewPager extends ViewPager
{
    private boolean noScroll = false;
    
    public CommunityViewPager(Context context)
    {
        super(context);
    }
    
    public CommunityViewPager(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    
    public void setNoScroll(boolean noScroll)
    {
        this.noScroll = noScroll;
    }
    
    @Override
    public void scrollTo(int x, int y)
    {
        super.scrollTo(x, y);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent arg0)
    {
        /* return false;//super.onTouchEvent(arg0); */
        if (noScroll)
            return false;
        else
            return super.onTouchEvent(arg0);
    }
    
    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0)
    {
        if (noScroll)
            return false;
        else
            return super.onInterceptTouchEvent(arg0);
    }
    
    @Override
    public void setCurrentItem(int item, boolean smoothScroll)
    {
        super.setCurrentItem(item, smoothScroll);
    }
    
    @Override
    public void setCurrentItem(int item)
    {
        super.setCurrentItem(item);
    }
}
