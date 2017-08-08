package com.junjingit.propery;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.junjingit.propery.utils.ImageReaderUtil;
import com.junjingit.propery.utils.NetWorkUtil;

import java.io.File;

/**
 * Created by jxy on 2017/8/2.
 */

public class PostCircleImageActivity extends AppCompatActivity {
    private Dialog dialog;
    private String imgPath;
    private byte[] myByteArray;
    private ImageView imageView;
    private OnImageUploadLiatener uploadLiatener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uploadLiatener = (OnImageUploadLiatener) PostCircleImageActivity.this;
    }
    //点击弹出添加图片的提示框
    public void onAddPic(View v){
        imageView = (ImageView) v;
        LayoutInflater inflater = LayoutInflater.from(this);
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.add_pic_dialog, null);
        dialog = new AlertDialog.Builder(this).create();
        dialog.setCancelable(true);
        dialog.show();
        dialog.getWindow().setContentView(layout);
    }

    /**
     * 拍照
     */
    public void getPicFromCamera(View v) {
        dialog.dismiss();
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            String path = Environment.getExternalStorageDirectory().toString()
                    + "/物业";
            File path1 = new File(path);
            if (!path1.exists()) {
                path1.mkdirs();
            }
            String name = System.currentTimeMillis() + ".jpg";
            imgPath = path + File.separator + name;
            File file = new File(path1, name);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
            startActivityForResult(intent, 1000);
        } else {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.check_sd_card), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 从手机相册选择
     *
     * @param v
     */
    public void getPicForCert(View v) {
        dialog.dismiss();
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 2000);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {

        } else if (requestCode == 2000) {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = managedQuery(data.getData(), proj, null, null, null);
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            imgPath = cursor.getString(column_index);

        } else if (resultCode != RESULT_OK) {
            return;
        } else if (data == null) {
            return;
        }
    }

    public void getImgByte(){
        if(imgPath==null){
            return;
        }
        myByteArray = ImageReaderUtil.getImgByte(imgPath);
        if(NetWorkUtil.isNetworkAvailable(this)){
            //显示上传进度条
            PostFileHttp();

        }


    }
    //上传图片
    public void PostFileHttp(){

    }

    //图片上传后接口回调
    public interface OnImageUploadLiatener {
         void onImageUpload(String url);
    }
}
