package com.junjingit.propery.fragment;

import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.design.internal.NavigationMenuPresenter;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.junjingit.propery.R;

import java.lang.reflect.Field;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeFragment extends Fragment
{
    
    private View mRootView;
    
    private NavigationView mNaviView;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        if (mRootView == null)
        {
            mRootView = inflater.inflate(R.layout.fragment_me, null);
            initView();
        }
        
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null)
        {
            parent.removeView(mRootView);
        }
        
        return mRootView;
    }
    
    public void setNavigationMenuLineStyle(NavigationView navigationView,
            @ColorInt final int color, final int height)
    {
        try
        {
            Field fieldByPressenter = navigationView.getClass()
                    .getDeclaredField("mPresenter");
            fieldByPressenter.setAccessible(true);
            NavigationMenuPresenter menuPresenter = (NavigationMenuPresenter) fieldByPressenter.get(navigationView);
            Field fieldByMenuView = menuPresenter.getClass()
                    .getDeclaredField("mMenuView");
            fieldByMenuView.setAccessible(true);
            final NavigationMenuView mMenuView = (NavigationMenuView) fieldByMenuView.get(menuPresenter);
            mMenuView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener()
            {
                @Override
                public void onChildViewAttachedToWindow(View view)
                {
                    RecyclerView.ViewHolder viewHolder = mMenuView.getChildViewHolder(view);
                    if (viewHolder != null
                            && "SeparatorViewHolder".equals(viewHolder.getClass()
                                    .getSimpleName())
                            && viewHolder.itemView != null)
                    {
                        if (viewHolder.itemView instanceof FrameLayout)
                        {
                            FrameLayout frameLayout = (FrameLayout) viewHolder.itemView;
                            View line = frameLayout.getChildAt(0);
                            line.setBackgroundColor(color);
                            line.getLayoutParams().height = height;
                            line.setLayoutParams(line.getLayoutParams());
                        }
                    }
                }
                
                @Override
                public void onChildViewDetachedFromWindow(View view)
                {
                    
                }
            });
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }
    
    private void initView()
    {
        mNaviView = mRootView.findViewById(R.id.navigation);
        
        setNavigationMenuLineStyle(mNaviView, 0xaaaaaa, 1);
    }
    
}
