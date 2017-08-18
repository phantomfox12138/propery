package com.junjingit.propery;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.avos.avoscloud.AVCloudQueryResult;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.CloudQueryCallback;
import com.junjingit.propery.login.RegisterFirstFragment;
import com.junjingit.propery.login.RegisterSecondFragment;
import com.junjingit.propery.utils.ToastUtils;

/**
 * Created by jxy on 2017/7/28.
 */

public class RegisterActivity extends AppCompatActivity implements
        RegisterFirstFragment.IRegisterFirstClickListener,
        RegisterSecondFragment.IRegisterSecondClickListener {
    private final String TAG = "RegisterActivity";

    private RegisterFirstFragment registerFirstFragment;

    private RegisterSecondFragment registerSecondFragment;
    private Toolbar title_bar;
    public int popType = 0;
    public String userObjectId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        title_bar = (Toolbar) findViewById(R.id.title_bar);
        setSupportActionBar(title_bar);
        getSupportActionBar().setTitle(getString(R.string.back_key));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
    public void onBackPressed() {
        super.onBackPressed();
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            finish();
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                getSupportFragmentManager().popBackStack();
            }
        }
    }

    @Override
    public void onNextAction(String mobile, String messageCode,String userObjectId) {
        if (registerSecondFragment == null) {
            registerSecondFragment = new RegisterSecondFragment();
        }
        popType=1;
        registerSecondFragment.setiRegisterSecondClickListener(this);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction tx = fm.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("mobile", mobile);
        bundle.putString("message", messageCode);
        bundle.putString("userObjectId",userObjectId);
        registerSecondFragment.setArguments(bundle);
        tx.replace(R.id.register_content, registerSecondFragment,
                "registerSecond");
        tx.addToBackStack(null);
        tx.commit();
    }

    @Override
    public void onBAction() {
        Log.v(TAG, "################onBAction");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Toolbar上的左上角的返回箭头的键值为Android.R.id.home  不是R.id.home
        switch (item.getItemId()) {
            case android.R.id.home:
                if (popType == 0) {
                    Log.v(TAG, "######################onOptionsItemSelected");
                    finish();
                } else if (popType == 1) {
                    getSupportFragmentManager().popBackStack();
                }
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void changePopType(int type, String objectId) {
        popType = type;
    }
}
