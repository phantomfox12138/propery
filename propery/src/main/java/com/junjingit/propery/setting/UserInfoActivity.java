package com.junjingit.propery.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.junjingit.propery.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by jxy on 2017/8/11.
 */

public class UserInfoActivity extends AppCompatActivity {
    @Bind(R.id.userinfo_image)
    CircleImageView userinfo_image;
    @Bind(R.id.userinfo_name_et)
    TextView userinfo_name_et;
    @Bind(R.id.userinfo_company_tv)
    TextView userinfo_company_tv;
    @Bind(R.id.userinfo_password_layout)
    RelativeLayout userinfo_password_layout;
    @Bind(R.id.title_bar)
    Toolbar title_bar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo_layout);
        ButterKnife.bind(this);
        initData();
        setListener();
    }

    /**
     *初始化数据
     */
    private void initData(){
        setSupportActionBar(title_bar);
        getSupportActionBar().setTitle(getString(R.string.my_userinfo));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * 设置监听
     */
    private void setListener(){

    }
}
