package com.junjingit.propery.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.junjingit.propery.R;
import com.junjingit.propery.common.FusionAction;
import com.junjingit.propery.utils.ToastUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by jxy on 2017/7/31.
 */

public class RegisterSecondFragment extends Fragment implements View.OnClickListener {
    private final String TAG = "RegisterSecondFragment";
    private View contentView;
    @Bind(R.id.register_second_psd)
    EditText register_second_psd;
    @Bind(R.id.register_second_rePsd)
    EditText register_second_rePsd;
    @Bind(R.id.register_second_btn)
    Button register_second_btn;
    public IRegisterSecondClickListener iRegisterSecondClickListener;
    public String mobile = "";
    public String messageCode = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.activity_register_second, container, false);
        ButterKnife.bind(this, contentView);
        return contentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mobile = getArguments().getString("mobile");
        messageCode = getArguments().getString("message");
        setListener();
    }

    /**
     * 设置监听
     */
    private void setListener() {
        register_second_btn.setOnClickListener(this);
    }

    public boolean toCheckMessage() {
        String psw1 = register_second_psd.getText().toString().trim();
        String psw2 = register_second_psd.getText().toString().trim();
        if(TextUtils.isEmpty(psw1)){
            ToastUtils.showToast(getActivity(),getString(R.string.login_password_empty));
            return false;
        }else if (psw1.length()<6 || psw1.length() > 25) {
            ToastUtils.showToast(getActivity(), "密码长度在6-25之间!");
            return false;
        } else if (TextUtils.isEmpty(psw2)) {
            ToastUtils.showToast(getActivity(), getString(R.string.login_sdpassword_empty));
            return false;
        }
        if (psw2.length() < 6 || psw2.length() > 26) {
            ToastUtils.showToast(getActivity(), "密码长度在6-25之间!");
            return false;
        } else if (!psw2.equals(psw2)) {
            ToastUtils.showToast(getActivity(), getString(R.string.login_fspassword));
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_second_btn:
                if(toCheckMessage()){
                   //去更新用户信息
                   String objectId= AVUser.getCurrentUser().getObjectId();
                    Log.v(TAG,"##############objectId"+objectId);
                    AVObject avObject = AVObject.createWithoutData("_User",objectId);
                    avObject.put("username",mobile);
                    avObject.put("password",register_second_psd.getText().toString());
                    avObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if(e==null){
                              ToastUtils.showToast(getActivity(),"密码设置成功，跳转登录页面");
                            }
                            Intent intent = new Intent(FusionAction.LOGIN_ACTION);
                            startActivity(intent);
                            getActivity().finish();
                        }
                    });//保存到服务器
                }
                break;
            default:
                break;
        }
    }

    //定义回调接口
    public interface IRegisterSecondClickListener {
        void onBAction();
    }

    //设置回调接口
    public void setiRegisterSecondClickListener(IRegisterSecondClickListener iRegisterSecondClickListener) {
        this.iRegisterSecondClickListener = iRegisterSecondClickListener;
    }
}