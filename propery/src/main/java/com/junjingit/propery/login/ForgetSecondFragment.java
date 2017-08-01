package com.junjingit.propery.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.junjingit.propery.R;
import com.junjingit.propery.common.FusionAction;
import com.junjingit.propery.utils.ToastUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by jxy on 2017/7/31.
 */

public class ForgetSecondFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "ForgetSecondFragment";
    private View contentView;
    @Bind(R.id.forget_second_psd)
    EditText forget_second_psd;
    @Bind(R.id.forget_second_rePsd)
    EditText forget_second_rePsd;
    @Bind(R.id.forget_second_btn)
    Button forget_second_btn;
    public IForgetSecondClickListener iForgetSecondClickListener;
    public String mobile = "";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.activity_forget_second, container, false);
        ButterKnife.bind(this, contentView);
        return contentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListener();
        mobile = getArguments().getString("mobile");
    }

    private void setListener() {
        forget_second_btn.setOnClickListener(this);
        forget_second_rePsd.addTextChangedListener(watcher);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.forget_second_btn:
                if (toCheckMessage()){
                    String password = forget_second_psd.getText().toString().trim();
                    forgetPsdHttp(password);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 简单逻辑判断输入密码
     *
     * @return
     */
    private boolean toCheckMessage() {
        String psd = forget_second_psd.getText().toString().trim();
        String repsd = forget_second_rePsd.getText().toString().trim();
        if (psd.length() < 6 || psd.length() > 25) {
            ToastUtils.showToast(getActivity(), getString(R.string.psd_length));
            return false;
        } else if (TextUtils.isEmpty(psd)) {
            ToastUtils.showToast(getActivity(), getString(R.string.login_sdpassword_empty));
            return false;
        } else if (repsd.length() < 6 || repsd.length() > 25) {
            ToastUtils.showToast(getActivity(), getString(R.string.psd__sure_length));
            return false;
        } else if (TextUtils.isEmpty(repsd)) {
            ToastUtils.showToast(getActivity(), getString(R.string.login_sdpassword_empty));
            return false;
        }
        if (!psd.equals(repsd)) {
            ToastUtils.showToast(getActivity(), getString(R.string.login_fspassword));
            return false;
        }
        return true;
    }

    /**
     * 手机号重置密码
     */
    private void forgetPsdHttp(final String password) {
        AVQuery<AVObject> avQuery = new AVQuery<>("_User");
        avQuery.whereEqualTo("mobilePhoneNumber", mobile);
        avQuery.findInBackground(new FindCallback<AVObject>(){
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        AVObject avObject = list.get(0);
                        String objectId = avObject.getObjectId();//得到用户的key
                        //查询该用户的信息
                        AVQuery<AVObject> avQuery = new AVQuery<>("_User");
                        avQuery.getInBackground(objectId, new GetCallback<AVObject>() {
                            @Override
                            public void done(AVObject avObject, AVException e) {
                                if (e == null) {
                                    Log.v(TAG,"########################getInBackground查询成功"+e);
                                    AVObject todo = AVObject.createWithoutData("_User", avObject.getObjectId());
                                    // 修改 content
                                    todo.put("password", password);
                                    // 保存到云端
                                    todo.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(AVException e) {
                                            if(e==null){
                                                Log.v(TAG,"########################saveInBackground登录成功"+e);
                                                AVUser.getCurrentUser().logOut();
                                                Intent intent = new Intent(FusionAction.LOGIN_ACTION);
                                                startActivity(intent);
                                                getActivity().finish();
                                            }else{
                                                Log.v(TAG,"########################saveInBackground登录失败"+e);
                                            }

                                        }
                                    });
                                }else{
                                    Log.v(TAG,"########################getInBackground查询失败"+e);
                                }
                            }
                        });
                    }
                }
            }
        });

    }

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            forget_second_btn.setEnabled(true);
            forget_second_btn.setBackgroundResource(R.drawable.blue_btn_border);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    //定义回调接口
    public interface IForgetSecondClickListener {
        void onBAction();
    }

    //设置回调接口
    public void setiForgetSecondClickListener(IForgetSecondClickListener iForgetSecondClickListener) {
        this.iForgetSecondClickListener = iForgetSecondClickListener;
    }
}
