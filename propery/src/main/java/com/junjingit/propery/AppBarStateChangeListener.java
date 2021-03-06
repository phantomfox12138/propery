package com.junjingit.propery;

import android.support.design.widget.AppBarLayout;
import android.util.Log;

/**
 * Created by niufan on 17/8/3.
 */

public abstract class AppBarStateChangeListener implements
        AppBarLayout.OnOffsetChangedListener
{
    private static final String TAG = "AppBarStateChange";
    
    public enum State
    {
        EXPANDED, COLLAPSED, IDLE
    }
    
    private State mCurrentState = State.IDLE;
    
    @Override
    public final void onOffsetChanged(AppBarLayout appBarLayout, int i)
    {
        
        if (i == 0)
        {
            if (mCurrentState != State.EXPANDED)
            {
                onStateChanged(appBarLayout, State.EXPANDED);
            }
            mCurrentState = State.EXPANDED;
        }
        else if (Math.abs(i) >= appBarLayout.getTotalScrollRange())
        {
            if (mCurrentState != State.COLLAPSED)
            {
                onStateChanged(appBarLayout, State.COLLAPSED);
            }
            mCurrentState = State.COLLAPSED;
        }
        else
        {
            if (mCurrentState != State.IDLE)
            {
                onStateChanged(appBarLayout, State.IDLE);
            }
            mCurrentState = State.IDLE;
        }
    }
    
    public abstract void onStateChanged(AppBarLayout appBarLayout, State state);
}
