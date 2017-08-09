package com.junjingit.propery.login;

import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.avos.avoscloud.RequestMobileCodeCallback;
import com.avos.avoscloud.SignUpCallback;
import com.junjingit.propery.R;
import com.junjingit.propery.StringUtil;
import com.junjingit.propery.utils.ToastUtils;
import butterknife.Bind;
import butterknife.ButterKnife;
/**
 * Created by jxy on 2017/7/31.
 */
public class RegisterFirstFragment extends Fragment implements View.OnClickListener {
    private final  String TAG="RegisterFirstFragment";
    private View contentView;
    @Bind(R.id.register_mobile_edit)
    EditText mMobile_edit;
    @Bind(R.id.register_message_edit)
    EditText mMobile_message;
    @Bind(R.id.register_message_txt)
    TextView mMessage_txt;
    @Bind(R.id.register_first_btn)
    Button mRegisterBtn;
    private TimeCount timer;//获取短信验证码后倒计时60秒
    public IRegisterFirstClickListener iRegisterFirstClickListener;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        contentView=inflater.inflate(R.layout.activity_register_first,container,false);
        ButterKnife.bind(this,contentView);
        return contentView;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListener();
    }

    /**
     * 设置监听
     */
    public void setListener(){
        mMessage_txt.setEnabled(true);
        timer = new TimeCount(60000, 1000);//实例化倒计时的变量，60秒后重新获取，时间间隔为1秒
        mRegisterBtn.setOnClickListener(this);
        mMessage_txt.setOnClickListener(this);
        mMobile_edit.addTextChangedListener(watcher1);
        mMobile_message.addTextChangedListener(watcher2);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.register_message_txt:
                String mobile=mMobile_edit.getText().toString();
                //获取当前输入的手机号,进行简单的校验
                if(TextUtils.isEmpty(mobile)){
                    ToastUtils.showToast(getActivity(),getString(R.string.login_mobil_empty));
                }else if(!StringUtil.checkMobile(mobile)){
                    ToastUtils.showToast(getActivity(), getString(R.string.login_mobile_error));
                }else{
                    insertOwner(mobile);
                }
                break;
            case R.id.register_first_btn:
                String messageCode=mMobile_message.getText().toString();
                if(TextUtils.isEmpty(messageCode)){
                    ToastUtils.showToast(getActivity(),getString(R.string.login_message_error));
                }else{
                    verifyMessageHttp(messageCode);
                }
                break;
            default:
                break;
        }


    }

    /**
     * 短信定时器
     */

   class  TimeCount extends CountDownTimer{

       public TimeCount(long millisInFuture, long countDownInterval) {
           super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
       }

       @Override
       public void onTick(long millisUntilFinished) {
           mMessage_txt.setEnabled(false);
           mMessage_txt.setText(String.format(getResources().getString(R.string.get_msg_code_again_time), millisUntilFinished/1000));
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
     * 像服务端插入一条数据记录
     */
   private void insertOwner(final String mobile){
       timer.start();
       AVUser user = new AVUser();
       user.setUsername("defaultuser");
       user.setPassword("123456");
       user.put("nickname","nickname");
       user.put("mobilePhoneNumber",mobile);
       user.signUpInBackground(new SignUpCallback(){
           public void done(AVException e){
               if (e == null) {
                   getMessageHttp(mobile);
                   Log.v(TAG,"#######################"+e);
               } else {
                   // failed
                   Log.v(TAG,"#######################"+e);
               }
           }
       });
   }
    /**
     * 获取短信验证码
     */
   private void getMessageHttp(String mobile){
       //调用发送登录验证码的接口：
       AVUser.requestMobilePhoneVerifyInBackground(mobile, new RequestMobileCodeCallback() {
           @Override
           public void done(AVException e) {
               if(e == null){
                   // 调用成功
                   Log.d(TAG,"#################调用成功");
               } else {
                   Log.d(TAG, "#######################"+e);
               }
           }
       });
   }

    /**
     * 校验短信验证码 是否正确或者失效
     */
   private void verifyMessageHttp(final String messageCode){
       AVUser.verifyMobilePhoneInBackground(messageCode, new AVMobilePhoneVerifyCallback() {
           @Override
           public void done(AVException e) {
               if(e == null){
                   // 验证成功
                   Log.v(TAG,"verifyMessageHttp########################"+e);
                   //跳转到下一步
                   String mobile=mMobile_edit.getText().toString();
                   iRegisterFirstClickListener.onNextAction(mobile, messageCode);
               } else {
                   Log.d(TAG, "Verified failed!");
               }
           }
       });
   }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(timer!=null){
            timer.cancel();
            timer=null;
        }
    }
    //定义回调接口
    public interface IRegisterFirstClickListener {
        void onNextAction(String mobile,String messageCode);
    }
    //设置回调接口
    public void setiRegisterFirstClickListener(IRegisterFirstClickListener iRegisterFirstClickListener) {
        this.iRegisterFirstClickListener = iRegisterFirstClickListener;
    }
    private TextWatcher watcher1 = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mRegisterBtn.setEnabled(false);
            mRegisterBtn.setBackgroundResource(R.drawable.grey_bg_with_corner);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };
    private TextWatcher watcher2= new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mRegisterBtn.setEnabled(true);
            mRegisterBtn.setBackgroundResource(R.drawable.blue_btn_border);
        }
        @Override
        public void afterTextChanged(Editable s) {
        }
    };

}
