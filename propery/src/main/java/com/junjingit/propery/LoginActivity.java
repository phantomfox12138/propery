package com.junjingit.propery;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.junjingit.propery.common.FusionAction;

public class LoginActivity extends AppCompatActivity {
    private EditText mUserName;

    private EditText mUserPwd;

    private Button mLoginBtn;

    private Button mRegistBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
    }

    public void initView() {
        mUserName = (EditText) findViewById(R.id.usernameEditText);
        mUserPwd = (EditText) findViewById(R.id.passwordEditText);
        mLoginBtn = (Button) findViewById(R.id.login);
        mRegistBtn = (Button) findViewById(R.id.register);

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!StringUtil.isNullOrEmpty(mUserName.getText().toString())
                        && !StringUtil.isNullOrEmpty(mUserPwd.getText()
                        .toString())) {
                    String username = mUserName.getText().toString();
                    String password = mUserPwd.getText().toString();
                    if (!TextUtils.isEmpty(username)
                            && !TextUtils.isEmpty(password)) {
                        AVUser.logInInBackground(username,
                                password,
                                new LogInCallback<AVUser>() {
                                    @Override
                                    public void done(AVUser avUser,
                                                     AVException e) {
                                        if (filterException(e)) {
                                            Intent intent = new Intent(
                                                    FusionAction.HOME_PAGE_ACTION);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                });
                    }
                }
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
}
