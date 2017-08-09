package com.junjingit.propery;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.junjingit.propery.login.RegisterFirstFragment;
import com.junjingit.propery.login.RegisterSecondFragment;

/**
 * Created by jxy on 2017/7/28.
 */

public class RegisterActivity extends AppCompatActivity implements
        RegisterFirstFragment.IRegisterFirstClickListener,
        RegisterSecondFragment.IRegisterSecondClickListener
{
    private final String TAG = "RegisterActivity";
    
    private RegisterFirstFragment registerFirstFragment;
    
    private RegisterSecondFragment registerSecondFragment;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        registerFirstFragment = new RegisterFirstFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.register_content,
                registerFirstFragment,
                "registerFirst");
        fragmentTransaction.commit();
        registerFirstFragment.setiRegisterFirstClickListener(this);
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
    
    @Override
    public void onNextAction(String mobile, String messageCode)
    {
        if (registerSecondFragment == null)
        {
            registerSecondFragment = new RegisterSecondFragment();
        }
        registerSecondFragment.setiRegisterSecondClickListener(this);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction tx = fm.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("mobile", mobile);
        bundle.putString("message", messageCode);
        registerSecondFragment.setArguments(bundle);
        tx.replace(R.id.register_content,
                registerSecondFragment,
                "registerSecond");
        tx.addToBackStack(null);
        tx.commit();
    }
    
    @Override
    public void onBAction()
    {
    }
}
