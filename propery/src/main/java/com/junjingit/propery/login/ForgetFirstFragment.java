package com.junjingit.propery.login;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVMobilePhoneVerifyCallback;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.RequestMobileCodeCallback;
import com.avos.avoscloud.UpdatePasswordCallback;
import com.junjingit.propery.R;
import com.junjingit.propery.StringUtil;
import com.junjingit.propery.utils.ToastUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by jxy on 2017/7/31.
 */

public class ForgetFirstFragment extends Fragment implements View.OnClickListener {
    private final String TAG = "RegisterFirstFragment";
    private View contentView;
    @Bind(R.id.forget_mobile_edit)
    EditText mMobile_edit;
    @Bind(R.id.forget_message_edit)
    EditText mMobile_message;
    @Bind(R.id.forget_message_txt)
    TextView mMessage_txt;
    @Bind(R.id.forget_first_btn)
    Button mForgetBtn;
    private ForgetFirstFragment.TimeCount timer;//获取短信验证码后倒计时60秒
    public ForgetFirstFragment.IForgetFirstClickListener iForgetFirstClickListener;
    private String messageCode = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.activity_forget_first, container, false);
        ButterKnife.bind(this, contentView);
        return contentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListener();
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    messageCode = mMobile_message.getText().toString();
                    verifyMessageHttp(messageCode);
                    break;
                case 1:
                    getMessageHttp(messageCode);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 设置监听
     */
    public void setListener() {
        mMessage_txt.setEnabled(true);
        timer = new ForgetFirstFragment.TimeCount(60000, 1000);//实例化倒计时的变量，60秒后重新获取，时间间隔为1秒
        mForgetBtn.setOnClickListener(this);
        mMessage_txt.setOnClickListener(this);
        mMobile_edit.addTextChangedListener(watcher1);
        mMobile_message.addTextChangedListener(watcher2);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.forget_message_txt:
                String mobile = mMobile_edit.getText().toString();
                //获取当前输入的手机号,进行简单的校验
                if (TextUtils.isEmpty(mobile)) {
                    ToastUtils.showToast(getActivity(), getString(R.string.login_mobil_empty));
                    return;
                } else if (!StringUtil.checkMobile(mobile)) {
                    ToastUtils.showToast(getActivity(), getString(R.string.login_mobile_error));
                    return;
                } else {
                    forgetGetMessage(mobile);
                }
                break;
            case R.id.forget_first_btn:
                String messageCode = mMobile_message.getText().toString();
                if (TextUtils.isEmpty(messageCode)) {
                    ToastUtils.showToast(getActivity(), getString(R.string.login_message_error));
                    return;
                } else {
                    mHandler.sendEmptyMessage(0);
                }
                break;
            default:
                break;
        }


    }

    /**
     * 短信定时器
     */

    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onTick(long millisUntilFinished) {
            mMessage_txt.setEnabled(false);
            mMessage_txt.setText(String.format(getResources().getString(R.string.get_msg_code_again_time), millisUntilFinished / 1000));
            mMessage_txt.setBackgroundResource(R.drawable.grey_bg_with_corner);
        }

        @Override
        public void onFinish() {
            mMessage_txt.setEnabled(true);
            mMessage_txt.setText(R.string.get_msg_code_again);
            mMessage_txt.setBackgroundResource(R.drawable.blue_btn_border);
        }
    }

    /**
     * 手机号码重置密码  获取短信验证码
     */
    private void forgetGetMessage(final String mobile) {
        timer.start();
        AVUser.requestPasswordResetBySmsCodeInBackground(mobile, new RequestMobileCodeCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    ToastUtils.showToast(getActivity(), "发送验证码成功!");
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 校验短信验证码
     */

    private void verifyMessageHttp(final String messageCode) {
        AVUser.verifyMobilePhoneInBackground(messageCode, new AVMobilePhoneVerifyCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    // 验证成功
                    mHandler.sendEmptyMessage(1);
                    Log.v(TAG, "verifyMessageHttp########################" + e);
                } else {
                    Log.d(TAG, "Verified failed!");
                }
            }
        });
    }

    /**
     * 注意!用户需要先绑定手机号码,然后使用短信验证码来重置密码
     */
    private void getMessageHttp(final String messageCode) {
        final String defaultPassWord="111111";
        final String defaultMobile = mMobile_edit.getText().toString().trim();
        //"111111", "password"为初始默认密码
        AVUser.resetPasswordBySmsCodeInBackground(messageCode, defaultPassWord, new UpdatePasswordCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    AVUser.logInInBackground(defaultMobile, defaultPassWord, new LogInCallback<AVUser>() {
                        @Override
                        public void done(AVUser avUser, AVException e){
                            if(e==null){
                                Log.v(TAG,"#######################logInInBackground"+e);
                            }
                            Log.v(TAG, "###################getMessageHttp" + e);
                            String mobile = mMobile_edit.getText().toString();
                            iForgetFirstClickListener.onNextAction(mobile, messageCode);
                        }
                    });

                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private TextWatcher watcher1 = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mForgetBtn.setEnabled(false);
            mForgetBtn.setBackgroundResource(R.drawable.grey_bg_with_corner);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };
    private TextWatcher watcher2 = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mForgetBtn.setEnabled(true);
            mForgetBtn.setBackgroundResource(R.drawable.blue_btn_border);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    //定义回调接口
    public interface IForgetFirstClickListener {
        void onNextAction(String mobile, String messageCode);
    }

    //设置回调接口
    public void setiForgetFirstClickListener(ForgetFirstFragment.IForgetFirstClickListener iRegisterFirstClickListener) {
        this.iForgetFirstClickListener = iRegisterFirstClickListener;
    }
}
