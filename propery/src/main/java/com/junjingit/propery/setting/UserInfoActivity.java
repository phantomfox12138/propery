package com.junjingit.propery.setting;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.junjingit.propery.Image;
import com.junjingit.propery.R;
import com.junjingit.propery.Utility;
import com.junjingit.propery.common.FusionAction;
import com.junjingit.propery.utils.MyImageLoader;
import com.junjingit.propery.utils.ToastUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.xander.panel.PanelInterface;
import com.xander.panel.XanderPanel;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.avos.avoscloud.AVUser.getCurrentUser;
import static com.junjingit.propery.R.id.userinfo_name_et;
import static com.junjingit.propery.common.FusionAction.QuoteExtra.IMAGE_LOADER_COUNT_EXTRA;
import static com.junjingit.propery.common.FusionAction.QuoteExtra.IMAGE_RESULT_LIST;
import static com.junjingit.propery.common.FusionAction.QuoteExtra.REQUEST_ADD_IMAGE_CODE;

/**
 * Created by jxy on 2017/8/11.
 */

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ModifyProfileActivity";
    @Bind(R.id.userinfo_image)
    CircleImageView userInfo_image;
    @Bind(userinfo_name_et)
    TextView userInfo_name_et;
    @Bind(R.id.userinfo_company_tv)
    TextView userInfo_company_tv;
    @Bind(R.id.userinfo_password_layout)
    RelativeLayout userInfo_password_layout;
    @Bind(R.id.title_bar)
    Toolbar title_bar;
    @Bind(R.id.userinfo_mobile)
    TextView userInfo_mobile;
    @Bind(R.id.userinfo_name_layout)
    RelativeLayout userInfo_name_layout;
    String nickName;
    String userIconUrl;
    String companyTxt;
    String mobileTxt;
    String strImgPath = "";
    String fileName = "";
    private ImageLoadingListener animateFirstListener = new UserInfoActivity.AnimateFirstDisplayListener();
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo_layout);
        mContext = UserInfoActivity.this;
        ButterKnife.bind(this);
        initData();
        setListener();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x1125:
                    Image img = (Image) msg.obj;
                    final String netImgPath = img.getImagePath();
                    final String netImgName = img.getImageName();
                    String userObjectId = getCurrentUser().getObjectId();
                    AVObject todo = AVObject.createWithoutData("_User", userObjectId);
                    todo.put("user_icon_url", netImgPath);
                    todo.put("userIcon", netImgName);
                    todo.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                ToastUtils.showToast(mContext, getString(R.string.upload_img_success));
                                AVFile file = new AVFile(netImgName, netImgPath, new HashMap<String, Object>());
                                String thumbUrl = file.getThumbnailUrl(true, 128, 128);
                                ImageLoader.getInstance().displayImage(thumbUrl, userInfo_image, MyImageLoader.MyCircleDisplayImageOptions(), animateFirstListener);
                                Intent intent = new Intent();
                                intent.setAction("modifyUserIcon");
                                mContext.sendBroadcast(intent);
                            } else {
                                ToastUtils.showToast(mContext, getString(R.string.upload_img_failed));
                            }
                        }
                    });
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 初始化数据
     */
    private void initData() {
        setSupportActionBar(title_bar);
        getSupportActionBar().setTitle(getString(R.string.my_userinfo));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        AVObject todo = AVObject.createWithoutData("_User", getCurrentUser().getObjectId());
        todo.fetchInBackground(new GetCallback<AVObject>() {
                                   @Override
                                   public void done(AVObject avObject, AVException e) {
                                       if (e == null) {
                                           if (avObject.get("user_icon_url") == null) {
                                               userIconUrl = "";
                                           } else {
                                               userIconUrl = avObject.get("user_icon_url").toString();
                                           }
                                           if (avObject.get("nickname") == null) {
                                               userInfo_name_et.setText(getString(R.string.nickname_empty));
                                           } else {
                                               nickName = avObject.get("nickname").toString();
                                               userInfo_name_et.setText(nickName);
                                           }
                                           if (avObject.get("company") == null) {
                                               companyTxt = "";
                                           } else {
                                               companyTxt = avObject.get("company").toString();
                                               userInfo_company_tv.setText(companyTxt);
                                           }
                                           if (avObject.get("username") == null) {//联系方式
                                               mobileTxt = "";
                                           } else {
                                               mobileTxt = avObject.get("username").toString();
                                               userInfo_mobile.setText(mobileTxt);
                                           }

                                           if (!TextUtils.isEmpty(userIconUrl)) {
                                               //暂时还没有头像
                                               ImageLoader.getInstance().displayImage(userIconUrl, userInfo_image, MyImageLoader.MyCircleDisplayImageOptions(), animateFirstListener);
                                           }
                                       }
                                   }
                               }
        );
    }

    /**
     * 设置监听
     */
    private void setListener() {
        userInfo_image.setOnClickListener(this);
        userInfo_name_layout.setOnClickListener(this);//修改昵称
        userInfo_password_layout.setOnClickListener(this);//修改密码
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.userinfo_image:
                getSheet();
                break;
            case R.id.userinfo_name_layout:
                Intent mdName = new Intent(FusionAction.MODIFY_USER_INFO);
                mdName.putExtra("name", userInfo_name_et.getText());
                startActivityForResult(mdName, 3000);
                break;
            case R.id.userinfo_password_layout:
                Intent mdPsw = new Intent(FusionAction.MODIFY_USER_PSW);
                startActivity(mdPsw);
                break;
            default:
                break;
        }
    }

    private class AnimateFirstDisplayListener extends
            SimpleImageLoadingListener {
        final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view,
                                      Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }

    /**
     * 选择照片弹框 底部弹框
     */
    private void getSheet() {
        XanderPanel.Builder mBuilder = new XanderPanel.Builder(mContext);
        mBuilder.setSheet(
                new String[]{getString(R.string.add_photo_dialog_take), getString(R.string.add_photo_dialog_select)}, true, getString(R.string.tip_cancel),
                new PanelInterface.SheetListener() {
                    @Override
                    public void onSheetItemClick(int position) {
                        if (position == 0) {//拍照
                            getPicFromCamera();
                        } else if (position == 1) {//从相册选择
                            selectIconImage();
                        }
                    }

                    @Override
                    public void onSheetCancelClick() {
                    }
                }
        );
        XanderPanel xanderPanel = mBuilder.create();
        xanderPanel.show();
    }

    /**
     * 手机拍照
     */
    public void getPicFromCamera() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            strImgPath = Utility.getFileCameraPath(mContext);// 存放照片的文件夹
            fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".png";// 照片命名
            File out = new File(strImgPath);
            //是否创建新的文件目录
            if (!out.exists()) {
                out.mkdirs();
            }
            out = new File(strImgPath, fileName);
            Uri uri = Uri.fromFile(out);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, 1000);
        } else {
            Toast.makeText(mContext, getString(R.string.check_sd_card), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 从手机相册选择
     */
    public void selectIconImage() {
        //跳转选择图片页面
        Intent pickPhoto = new Intent(FusionAction.IMAGE_LOADER_ACTION);
        //参数2位设置可选择图片的最大值
        pickPhoto.putExtra(IMAGE_LOADER_COUNT_EXTRA, 1);
        startActivityForResult(pickPhoto, REQUEST_ADD_IMAGE_CODE);
    }

    /**
     * 图片上传
     *
     * @param imgPath
     * @throws FileNotFoundException
     */
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
                    img.setImageName(image.getOriginalName());
                    Message msg = new Message();
                    msg.obj = img;
                    msg.what = 0x1125;
                    mHandler.sendMessage(msg);
                } else {
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1000://系统相机
                    String backImgPath = strImgPath + fileName;
                    File backImgFile = new File(backImgPath);
                    if (backImgFile.exists()) {
                        try {
                            uploadUserIcon(backImgPath);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case REQUEST_ADD_IMAGE_CODE:
                    //选择的图片路径
                    List<String> imageList = data.getStringArrayListExtra(IMAGE_RESULT_LIST);
                    if (imageList.size() > 0) {
                        String backAlbumImg = imageList.get(0).toString();
                        try {
                            uploadUserIcon(backAlbumImg);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                case 3000:
                    Bundle bundle = data.getExtras();
                    String modifyName = bundle.getString("name");
                    userInfo_name_et.setText(modifyName);
                    Intent intent = new Intent();
                    intent.setAction("modifyNickName");
                    mContext.sendBroadcast(intent);
                    break;
                default:
                    break;
            }
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
