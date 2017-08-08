package com.junjingit.propery;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.SaveCallback;
import com.junjingit.propery.common.FusionAction;
import com.junjingit.propery.utils.ToastUtils;

import java.io.FileNotFoundException;
import java.util.List;

import static com.junjingit.propery.common.FusionAction.QuoteExtra.IMAGE_LOADER_COUNT_EXTRA;
import static com.junjingit.propery.common.FusionAction.QuoteExtra.IMAGE_RESULT_LIST;
import static com.junjingit.propery.common.FusionAction.QuoteExtra.REQUEST_ADD_IMAGE_CODE;

public class ModifyProfileActivity extends AppCompatActivity {

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 0x1125:

                    Image img = (Image) msg.obj;

                    //TODO: update User表头像文件名和头像图片url字段

                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_profil);
        initView();
    }

    private void initView() {
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

                    mHandler.sendMessage(msg);

                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_ADD_IMAGE_CODE:
                    //选择的图片路径
                    List<String> imageList = data.getStringArrayListExtra(IMAGE_RESULT_LIST);
                    break;
            }
        }
    }

}
