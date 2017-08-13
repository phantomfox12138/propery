package com.junjingit.propery.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.junjingit.propery.R;
import com.junjingit.propery.utils.NetWorkUtil;
import com.junjingit.propery.utils.ToastUtils;
import com.junjingit.propery.widget.ClearEditText;

/**
 * Created by Administrator on 2017/8/13 0013.
 */

public class ModifyNameActivity extends AppCompatActivity implements View.OnClickListener {
    private ClearEditText modify_name;
    private Button modify_name_btn;
    private String name, modifyName;
    private Context mContext;
    private Toolbar title_bar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_name);
        mContext = ModifyNameActivity.this;
        name = this.getIntent().getStringExtra("name");
        title_bar=(Toolbar)findViewById(R.id.title_bar);
        setSupportActionBar(title_bar);
        getSupportActionBar().setTitle(getString(R.string.modify_password));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        modify_name = (ClearEditText) findViewById(R.id.modify_name);
        modify_name_btn = (Button) findViewById(R.id.modify_name_btn);
        modify_name.setText(name);
        if (name.length() > 0) {
            modify_name.setSelection(name.length());
        }
        modify_name_btn.setOnClickListener(this);
        modify_name.addTextChangedListener(mInputTextWatcher);
    }

    /**
     * 判断输入框不能为空
     *
     * @return
     */
    private boolean checkEnable() {
        modifyName = modify_name.getText().toString().trim();
        if ("".equals(modifyName)) {
            ToastUtils.showToast(mContext, getString(R.string.modify_nickname_empty));
            return false;
        } else if (modifyName.length() > 10) {
            ToastUtils.showToast(mContext, getString(R.string.modify_nickname_length));
            return false;
        }
        return true;
    }

    //设置监听
    private TextWatcher mInputTextWatcher = new TextWatcher() {
        private String temp;

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            temp = s.toString();
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!TextUtils.isEmpty(temp)) {
                String limitSubstring = getLimitSubstring(temp);
                if (!TextUtils.isEmpty(limitSubstring)) {
                    if (!limitSubstring.equals(temp)) {
                        ToastUtils.showToast(getApplicationContext(), "昵称不能超过10个字");
                        modify_name.setText(limitSubstring);
                        modify_name.setSelection(limitSubstring.length());
                    }
                }
            }
        }
    };

    private String getLimitSubstring(String inputStr) {
        int orignLen = inputStr.length();
        int resultLen = 0;
        for (int i = 0; i < orignLen; i++) {
            try {
                resultLen++;
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (resultLen > 10) {
                return inputStr.substring(0, i);
            }
        }
        return inputStr;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.modify_name_btn:
                if (checkEnable()) {
                    if (NetWorkUtil.isNetworkAvailable(mContext)) {
                        modifyName(modifyName);
                    } else {
                        ToastUtils.showToast(mContext, getString(R.string.common_no_network));
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 修改昵称 更新服务器数据， 拉取服务端最新数据
     */
    private void modifyName(String name) {
        AVObject todo = AVObject.createWithoutData("_User", AVUser.getCurrentUser().getObjectId());
        todo.put("nickname", name);
        // 保存到云端
        todo.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {//表示保存成功
                    Intent nameData = new Intent();
                    nameData.putExtra("name", modifyName);
                    setResult(RESULT_OK, nameData);
                    ModifyNameActivity.this.finish();
                    ToastUtils.showToast(mContext, "修改成功!");
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
