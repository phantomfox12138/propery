package com.junjingit.propery;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.junjingit.propery.login.ForgetFirstFragment;
import com.junjingit.propery.login.ForgetSecondFragment;

/**
 * Created by jxy on 2017/7/28.
 */

public class ForgetActivity extends AppCompatActivity implements
        ForgetFirstFragment.IForgetFirstClickListener,
        ForgetSecondFragment.IForgetSecondClickListener
{
    private final String TAG = "ForgetActivity";
    
    public ForgetFirstFragment forgetFirstFragment;
    
    public ForgetSecondFragment forgetSecondFragment;
    private Toolbar title_bar;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);
        title_bar = (Toolbar) findViewById(R.id.title_bar);
        setSupportActionBar(title_bar);
        getSupportActionBar().setTitle(getString(R.string.back_key));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        forgetFirstFragment = new ForgetFirstFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.forget_content,
                forgetFirstFragment,
                "forgetFirst");
        fragmentTransaction.commit();
        forgetFirstFragment.setiForgetFirstClickListener(this);
    }
    
    /**
     * 跳转到第二个页面
     * 
     * @param mobile
     * @param messageCode
     */
    @Override
    public void onNextAction(String mobile, String messageCode)
    {
        //进行下一步操作
        if (forgetSecondFragment == null)
        {
            forgetSecondFragment = new ForgetSecondFragment();
        }
        forgetSecondFragment.setiForgetSecondClickListener(this);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction tx = fm.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("mobile", mobile);
        forgetSecondFragment.setArguments(bundle);
        tx.replace(R.id.forget_content, forgetSecondFragment, "forgetSecond");
        tx.addToBackStack(null);
        tx.commit();
    }
    
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        if (getSupportFragmentManager().getBackStackEntryCount() == 0)
        {
            finish();
        }
        else
        {
            if (getSupportFragmentManager().getBackStackEntryCount() == 1)
            {
                getSupportFragmentManager().popBackStack();
            }
        }
    }
    
    //从第二个页面或第一个页面直接返回
    @Override
    public void onBAction()
    {
    }
}
