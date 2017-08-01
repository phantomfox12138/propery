package com.junjingit.propery;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.SaveCallback;
import com.junjingit.propery.common.FusionAction;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by jxy on 2017/7/28.
 */

public class LoginNewActivity extends AppCompatActivity {
    private static final String TAG = "LoginNewActivity";

    @Bind(R.id.input_account)
    EditText mUserName;

    @Bind(R.id.input_password)
    EditText mUserPwd;

    @Bind(R.id.btn_login)
    Button mLoginBtn;

    @Bind(R.id.quit_register)
    TextView mRegisterTxt;

    @Bind(R.id.forget_psd)
    TextView mForgetTxt;

    @Bind(R.id.login_lay)
    LinearLayout login_lay;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_new);
        ButterKnife.bind(this);
        initView();
    }
    /**
     * 初始化控件,添加事件
     */
    private void initView() {
        login_lay.getBackground().setAlpha(70);
        mLoginBtn.getBackground().setAlpha(140);
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!StringUtil.isNullOrEmpty(mUserName.getText().toString())
                        && !StringUtil.isNullOrEmpty(mUserPwd.getText()
                        .toString())) {
                    String userName = mUserName.getText().toString();
                    String password = mUserPwd.getText().toString();
                    if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(password)){
                        AVUser.logInInBackground(userName,
                                password,
                                new LogInCallback<AVUser>(){
                                    @Override
                                    public void done(AVUser avUser,
                                                     AVException e) {
                                        if (filterException(e)) {

                                            String objectId = avUser.getString("deviceId");
                                            String deviceId = getDeviceId();
                                            AVObject avObject = AVObject.createWithoutData("Device", objectId);
                                            Log.v("LoginActivity", "##############objectId" + objectId);
                                            avObject.put("deviceId", deviceId);
                                            avObject.saveInBackground();//保存到服务器
                                            Intent intent = new Intent(FusionAction.HOME_PAGE_ACTION);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                });
                    }
                }
            }
        });
        mRegisterTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(FusionAction.REGISTER_ACTION);
                startActivity(intent);
                finish();
            }
        });

        mForgetTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FusionAction.FORGET_ACTION);
                startActivity(intent);
                finish();
            }
        });
    }

    public boolean filterException(Exception e) {
        if (e != null) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();

            return false;
        } else {
            return true;
        }
    }

    /**
     * 获取设置唯一Id
     *
     * @return
     */
    public String getDeviceId() {
        TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

}
