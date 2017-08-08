package com.junjingit.propery.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.SaveCallback;
import com.junjingit.propery.Image;
import com.junjingit.propery.ModifyProfileActivity;
import com.junjingit.propery.R;
import com.junjingit.propery.common.FusionAction;
import com.junjingit.propery.utils.ToastUtils;

import java.io.FileNotFoundException;
import java.util.List;

import static com.junjingit.propery.common.FusionAction.QuoteExtra.IMAGE_LOADER_COUNT_EXTRA;
import static com.junjingit.propery.common.FusionAction.QuoteExtra.IMAGE_RESULT_LIST;
import static com.junjingit.propery.common.FusionAction.QuoteExtra.REQUEST_ADD_IMAGE_CODE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeFragment extends Fragment implements View.OnClickListener {

    private View mRootView;

    private NavigationView mNaviView;

    private Dialog dialog;

    private ImageView imageView, user_icon;
    private TextView user_nickName;

    private View mModifyProfile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_me, null);
            initView();
        }

        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }

        final Toolbar toolbar = (Toolbar) mRootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setDisplayShowTitleEnabled(false);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) mRootView.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("");
        return mRootView;
    }

    public void initView() {
        mModifyProfile = mRootView.findViewById(R.id.profile_modify_profile);
        user_icon = mRootView.findViewById(R.id.user_icon);
        user_nickName = mRootView.findViewById(R.id.user_nickName);
        setListener();
    }

    public void setListener() {
        user_icon.setOnClickListener(this);
    }

    public void onAddPic(View v) {
        imageView = (ImageView) v;
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.add_pic_dialog,
                null);
        dialog = new AlertDialog.Builder(getActivity()).create();
        dialog.setCancelable(true);
        dialog.show();
        dialog.getWindow().setContentView(layout);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user_icon:
                selectIconImage();
                break;
            default:
                break;
        }
    }

    public void selectIconImage() {
        //跳转选择图片页面
        Intent pickPhoto = new Intent(FusionAction.IMAGE_LOADER_ACTION);

        //参数2位设置可选择图片的最大值
        pickPhoto.putExtra(IMAGE_LOADER_COUNT_EXTRA, 1);

        startActivityForResult(pickPhoto, REQUEST_ADD_IMAGE_CODE);
    }

    private void uploadUserIcon(String imgPath) throws FileNotFoundException {
        final String[] ss = imgPath.split("/");
        final String imageName = ss[ss.length - 1];
        //上传图片文件
        final AVFile image = AVFile.withAbsoluteLocalPath(imageName, imgPath);
        image.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (null == e) {
                    //获取图片url
                    Image img = new Image();
                    img.setImagePath(image.getUrl());
                    img.setImageName(imageName);

                    Message msg = new Message();
                    msg.obj = img;
                    msg.what = 0x1125;
                    //  mHandler.sendMessage(msg);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            switch (requestCode) {
                case REQUEST_ADD_IMAGE_CODE:
                    //选择的图片路径
                    List<String> imageList = data.getStringArrayListExtra(IMAGE_RESULT_LIST);
                    break;
            }
        }
    }

}
