package com.junjingit.propery.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.SaveCallback;
import com.junjingit.propery.Image;
import com.junjingit.propery.ModifyProfileActivity;
import com.junjingit.propery.R;
import com.junjingit.propery.common.FusionAction;
import com.junjingit.propery.utils.ImageReaderUtil;
import com.junjingit.propery.utils.NetWorkUtil;
import com.junjingit.propery.utils.ToastUtils;

import java.io.File;
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
    private TextView user_nickName,takePhoto;

    private View mModifyProfile;
    private String imgPath;
    private byte[] myByteArray;

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

    public void onAddPic() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.add_pic_dialog, null);
        takePhoto=layout.findViewById(R.id.takePhoto);
        dialog = new AlertDialog.Builder(getActivity(), R.style.ActionSheetDialogStyle).create();
        dialog.setCancelable(true);
        dialog.show();
        dialog.getWindow().setContentView(layout);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = 20;
        dialogWindow.setAttributes(lp);
        dialog.show();//显示对话框
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPicFromCamera();
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user_icon:
                onAddPic();
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
    /**
     * 拍照
     */
    public void getPicFromCamera(){
        dialog.dismiss();
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            String path = Environment.getExternalStorageDirectory().toString()+"/物业";
            File path1 = new File(path);
            if (!path1.exists()) {
                path1.mkdirs();
            }
            String name = System.currentTimeMillis() + ".png";
            imgPath = path + File.separator + name;
            File file = new File(path1, name);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
            startActivityForResult(intent, 1000);
        } else {
            Toast.makeText(getContext(),getString(R.string.check_sd_card), Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000){
            getImgByte();
        }
    }
    private void getImgByte() {
        if (imgPath == null){
            return;
        }
        myByteArray = ImageReaderUtil.getImgByte(imgPath);
        try {
            if(NetWorkUtil.isNetworkAvailable(getContext())){
                //加载圈
               // postFile();
            }

        } catch (Exception e) {
            //隐藏加载圈
            e.printStackTrace();
        }

    }

}
