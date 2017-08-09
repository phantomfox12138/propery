package com.junjingit.propery.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.junjingit.propery.AppBarStateChangeListener;
import com.junjingit.propery.HomeListAdapter;
import com.junjingit.propery.R;
import com.junjingit.propery.common.FusionAction;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener
{
    private static final String TAG = "HomeFragment";
    
    private View mRootView;
    
    private RecyclerView mHomeList;
    
    private SwipeRefreshLayout mRefresh;
    
    private HomeListAdapter mHomeAdapter;
    
    private AppBarLayout mAppBarLayout;
    
    private Toolbar mToolbar;
    
    private SliderLayout mHomeSlider;
    
    private View mNotifyView;
    
    private View mActiveView;
    
    private View mPayView;
    
    public HomeListAdapter getHomeAdapter()
    {
        return mHomeAdapter;
    }
    
    public void notifyDataSetChanged()
    {
        mHomeAdapter.notifyDataSetChanged();
        
        mRefresh.setRefreshing(false);
    }
    
    public Toolbar getToolbar()
    {
        return mToolbar;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        if (mRootView == null)
        {
            mRootView = inflater.inflate(R.layout.fragment_home, null);
            initView();
        }
        
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null)
        {
            parent.removeView(mRootView);
        }
        
        return mRootView;
    }
    
    int mDistanceY;
    
    private void initView()
    {
        mHomeList = mRootView.findViewById(R.id.rv_content);
        mRefresh = mRootView.findViewById(R.id.srl_refresh);
        mAppBarLayout = mRootView.findViewById(R.id.appbar);
        mToolbar = mRootView.findViewById(R.id.tb_toolbar);
        mHomeSlider = mRootView.findViewById(R.id.home_slider);
        mNotifyView = mRootView.findViewById(R.id.home_title_notification);
        mActiveView = mRootView.findViewById(R.id.home_title_activity);
        mPayView = mRootView.findViewById(R.id.home_title_pay);
        
        mNotifyView.setOnClickListener(this);
        mActiveView.setOnClickListener(this);
        mPayView.setOnClickListener(this);
        
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        
        mHomeSlider.setPresetTransformer(SliderLayout.Transformer.Default);
        mHomeSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mHomeSlider.setCustomAnimation(new DescriptionAnimation());
        mHomeSlider.setDuration(2000);
        //        mHomeSlider.addOnPageChangeListener(this);
        
        HashMap<String, Integer> file_maps = new HashMap<String, Integer>();
        file_maps.put("Hannibal", R.drawable.hannibal);
        file_maps.put("Big Bang Theory", R.drawable.bigbang);
        file_maps.put("House of Cards", R.drawable.house);
        file_maps.put("Game of Thrones", R.drawable.game_of_thrones);
        
        for (String name : file_maps.keySet())
        {
            TextSliderView textSliderView = new TextSliderView(getActivity());
            
            textSliderView.description(name)
                    .image(file_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit);
            //                                .setOnSliderClickListener(this)
            //                                .setPosition(i);
            
            mHomeSlider.addSlider(textSliderView);
        }
        
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener()
        {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout,
                    int verticalOffset)
            {
                //滑动的距离
                mDistanceY += verticalOffset;
                //toolbar的高度
                int toolbarHeight = mToolbar.getBottom();
                
                //当滑动的距离 <= toolbar高度的时候，改变Toolbar背景色的透明度，达到渐变的效果
                if (Math.abs(verticalOffset) < appBarLayout.getTotalScrollRange())
                {
                    
                    float scale = (float) Math.abs(verticalOffset)
                            / appBarLayout.getTotalScrollRange();
                    float alpha = scale * 255;
                    
                    int resultAplha = ((int) alpha + 100) > 255 ? 255
                            : ((int) alpha + 100);
                    
                    mToolbar.setBackgroundColor(Color.argb(resultAplha,
                            18,
                            23,
                            52));
                }
                else
                {
                    //上述虽然判断了滑动距离与toolbar高度相等的情况，但是实际测试时发现，标题栏的背景色
                    //很少能达到完全不透明的情况，所以这里又判断了滑动距离大于toolbar高度的情况，
                    //将标题栏的颜色设置为完全不透明状态
                    mToolbar.setBackgroundResource(R.color.blue_black);
                }
            }
        });
        
        mHomeList.setOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                
            }
        });
        
        mHomeAdapter = new HomeListAdapter(getActivity());
        mHomeAdapter.setFrom("home");
        
        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mHomeList.setLayoutManager(llm);
        
        mHomeList.setAdapter(mHomeAdapter);
        
        mRefresh.setRefreshing(true);
        
        mAppBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener()
        {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state)
            {
                if (state == State.EXPANDED)
                {
                    //展开状态
                }
                else if (state == State.COLLAPSED)
                {
                    //折叠状态
                }
                else
                {
                    //中间状态
                }
            }
        });
        
    }
    
    @Override
    public void onClick(View view)
    {
        Intent toNotify = new Intent(FusionAction.HOME_TITLE_LIST_ACTION);
        switch (view.getId())
        {
            case R.id.home_title_notification:
                
                toNotify.putExtra(FusionAction.HomeListExtra.TYPE,
                        FusionAction.HomeListExtra.NOTIFY);
                toNotify.putExtra(FusionAction.HomeListExtra.TITLE_NAME, "通知公告");
                startActivity(toNotify);
                break;
            
            case R.id.home_title_activity:
                
                toNotify.putExtra(FusionAction.HomeListExtra.TYPE,
                        FusionAction.HomeListExtra.ACTIVE);
                toNotify.putExtra(FusionAction.HomeListExtra.TITLE_NAME, "园区活动");
                startActivity(toNotify);
                break;
            case R.id.home_title_pay:
                
                Intent toPay = new Intent(FusionAction.HOME_TITLE_PAY_ACTION);
                toPay.putExtra(FusionAction.HomeListExtra.TITLE_NAME, "费用缴纳");
                startActivity(toPay);
                break;
        }
        
    }
}
