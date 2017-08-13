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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.junjingit.propery.R;
import com.junjingit.propery.common.FusionAction;
import com.junjingit.propery.utils.ToastUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import static com.avos.avoscloud.AVUser.getCurrentUser;
/**
 * Created by Administrator on 2017/8/13 0013.
 */
public class ModifyPassWordActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {
    @Bind(R.id.title_bar)
    Toolbar title_bar;
    @Bind(R.id.modify_password_old)
    EditText modify_password_old;
    @Bind(R.id.modify_password_first)
    EditText modify_password_first;
    @Bind(R.id.modify_password_second)
    EditText modify_password_second;
    @Bind(R.id.modify_login_btn)
    Button modify_login_btn;
    @Bind(R.id.number_line1)
    TextView number_line1;
    @Bind(R.id.number_line2)
    TextView number_line2;
    @Bind(R.id.number_line3)
    TextView number_line3;
    Context mContext;
    int type = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);
        mContext = ModifyPassWordActivity.this;
        ButterKnife.bind(this);
        initData();
        setListener();
    }

    private void initData() {
        setSupportActionBar(title_bar);
        getSupportActionBar().setTitle(getString(R.string.modify_password));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setListener() {
        modify_password_old.setOnClickListener(this);
        modify_password_first.setOnClickListener(this);
        modify_password_second.setOnClickListener(this);
        modify_login_btn.setOnClickListener(this);
        modify_password_old.setOnFocusChangeListener(this);
        modify_password_first.setOnFocusChangeListener(this);
        modify_password_second.setOnFocusChangeListener(this);
        modify_password_old.addTextChangedListener(mInputTextWatcher);
        modify_password_first.addTextChangedListener(mInputTextWatcher);
        modify_password_second.addTextChangedListener(mInputTextWatcher);

    }

    private void modifyPassWord() {
        String oldPassWord = modify_password_old.getText().toString();
        String firstPassWord = modify_password_first.getText().toString();
        String secondPassWord = modify_password_second.getText().toString();
        if (TextUtils.isEmpty(oldPassWord)) {
            ToastUtils.showToast(mContext, getString(R.string.modify_old_psw_empty));
            return;
        } else if (oldPassWord.length() < 6 || oldPassWord.length() > 25) {
            ToastUtils.showToast(mContext, getString(R.string.modify_psw_length));
            return;
        } else if (TextUtils.isEmpty(firstPassWord) || TextUtils.isEmpty(secondPassWord)) {
            ToastUtils.showToast(mContext, getString(R.string.modify_psw_newEmpty));
        } else if (!firstPassWord.equals(secondPassWord)) {
            ToastUtils.showToast(mContext, getString(R.string.modify_psw_newOld));
            return;
        } else {
            AVObject todo = AVObject.createWithoutData("_User", getCurrentUser().getObjectId());
            todo.put("password", secondPassWord);
            todo.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null) {
                        ToastUtils.showToast(mContext, getString(R.string.modify_psw_success));
                        modify_password_old.setText("");
                        modify_password_first.setText("");
                        modify_password_second.setText("");
                        Intent toLogin = new Intent(FusionAction.LOGIN_ACTION);
                        startActivity(toLogin);
                        ModifyPassWordActivity.this.finish();
                    } else {
                        modify_password_old.setText("");
                        modify_password_first.setText("");
                        modify_password_second.setText("");
                        ToastUtils.showToast(mContext, getString(R.string.modify_psw_failed));
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.modify_login_btn:
                modifyPassWord();
                break;
            default:
                break;
        }
    }

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
                String limitSubstring = getLimitSubstring(temp, 25);
                if (!TextUtils.isEmpty(limitSubstring)) {
                    if (!limitSubstring.equals(temp)) {
                        ToastUtils.showToast(mContext, getString(R.string.modify_psw_length));
                        if (type == 1) {
                            modify_password_old.setText(limitSubstring);
                            modify_password_old.setSelection(limitSubstring.length());
                        } else if (type == 2) {
                            modify_password_first.setText(limitSubstring);
                            modify_password_first.setSelection(limitSubstring.length());
                        } else if (type == 3) {
                            modify_password_second.setText(limitSubstring);
                            modify_password_second.setSelection(limitSubstring.length());
                        }
                    }
                }
            }
        }
    };

    //控制字符输入的字数
    private String getLimitSubstring(String inputStr, int num) {
        int orignLen = inputStr.length();
        int resultLen = 0;
        for (int i = 0; i < orignLen; i++) {
            try {
                resultLen++;
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (resultLen > num) {
                return inputStr.substring(0, i);
            }
        }
        return inputStr;
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        switch (view.getId()) {
            case R.id.modify_password_old:
                if (hasFocus) {
                    type = 1;
                    number_line1.setBackgroundResource(R.color.blue);
                } else {
                    number_line1.setBackgroundResource(R.color.ccccccc);
                }
                break;
            case R.id.modify_password_first:
                if (hasFocus) {
                    type = 2;
                    number_line2.setBackgroundResource(R.color.blue);
                } else {
                    number_line2.setBackgroundResource(R.color.ccccccc);
                }
                break;
            case R.id.modify_password_second:
                if (hasFocus) {
                    type = 3;
                    number_line3.setBackgroundResource(R.color.blue);
                } else {
                    number_line3.setBackgroundResource(R.color.ccccccc);
                }
                break;
            default:
                break;
        }
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
