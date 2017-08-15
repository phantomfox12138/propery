package com.junjingit.propery.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.junjingit.propery.HomeListAdapter;
import com.junjingit.propery.Image;
import com.junjingit.propery.ModifyProfileActivity;
import com.junjingit.propery.R;
import com.junjingit.propery.Utility;
import com.junjingit.propery.common.FusionAction;
import com.junjingit.propery.utils.Define;
import com.junjingit.propery.utils.ImageReaderUtil;
import com.junjingit.propery.utils.MyImageLoader;
import com.junjingit.propery.utils.NetWorkUtil;
import com.junjingit.propery.utils.ToastUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
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

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.avos.avoscloud.AVUser.getCurrentUser;
import static com.junjingit.propery.common.FusionAction.QuoteExtra.IMAGE_LOADER_COUNT_EXTRA;
import static com.junjingit.propery.common.FusionAction.QuoteExtra.IMAGE_RESULT_LIST;
import static com.junjingit.propery.common.FusionAction.QuoteExtra.REQUEST_ADD_IMAGE_CODE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "MeFragment";

    private View mRootView;

    private Dialog dialog;

    private CircleImageView user_icon;

    private TextView user_nickName, takePhoto, takeAlbum, takeCancel, myFans_txt, myFollow_txt;

    private View mModifyProfile;

    String strImgPath = "";

    String fileName = "";
    String nickName;
    String userIconUrl;
    String user_cover_img_url;
    private ImageLoadingListener animateFirstListener = new MeFragment.AnimateFirstDisplayListener();

    private LinearLayout exitLinearLayout, me_circle_layout, me_modify_layout;
    private UpDateReceiver updateReceiver;
    private Button exit_btn;

    private ImageView backdrop;//封面上传

    private int coverFlag=0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        upViewData();//注册广播
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
        user_icon = (CircleImageView) mRootView.findViewById(R.id.user_icon);
        user_nickName = mRootView.findViewById(R.id.user_nickName);
        exit_btn = mRootView.findViewById(R.id.exit_btn);
        me_circle_layout = mRootView.findViewById(R.id.me_circle_layout);
        myFans_txt = mRootView.findViewById(R.id.myFans_txt);
        myFollow_txt = mRootView.findViewById(R.id.myFollow_txt);
        me_modify_layout = mRootView.findViewById(R.id.me_modify_layout);
        backdrop=(ImageView)mRootView.findViewById(R.id.backdrop);
        setListener();
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        String userObjectId = getCurrentUser().getObjectId();
        AVObject anotherTodo = AVObject.createWithoutData("_User", userObjectId);
        anotherTodo.fetchInBackground(new GetCallback<AVObject>(){
            @Override
            public void done(AVObject avObject, AVException e) {
                if (e == null) {
                    if (avObject.get("user_icon_url") == null){
                        userIconUrl = "";
                    }else{
                        userIconUrl = avObject.get("user_icon_url").toString();
                    }
                    if (avObject.get("nickname") == null){
                        user_nickName.setText(getString(R.string.nickname_empty));
                    } else {
                        nickName = avObject.get("nickname").toString();
                        user_nickName.setText(nickName);
                    }
                    if (!TextUtils.isEmpty(userIconUrl)){
                        //暂时还没有头像
                        ImageLoader.getInstance().displayImage(userIconUrl, user_icon, MyImageLoader.MyCircleDisplayImageOptions(), animateFirstListener);
                    }
                    if(avObject.get("user_cover_img_url")==null){
                        user_cover_img_url="";
                    }else{
                        user_cover_img_url=avObject.get("user_cover_img_url").toString();
                        ImageLoader.getInstance().displayImage(user_cover_img_url,backdrop, MyImageLoader.MyNormalCoverImageOptions(), animateFirstListener);
                    }
                }
            }
        });
        getMyFollow(userObjectId);
        getMyFans();

    }

    public void setListener() {
        user_icon.setOnClickListener(this);
        exit_btn.setOnClickListener(this);
        me_circle_layout.setOnClickListener(this);
        me_modify_layout.setOnClickListener(this);
        myFollow_txt.setOnClickListener(this);
        myFans_txt.setOnClickListener(this);
        backdrop.setOnClickListener(this);
    }

    public void onAddPic() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.add_pic_dialog,
                null);
        takePhoto = layout.findViewById(R.id.takePhoto);
        takeAlbum = layout.findViewById(R.id.takeAlbum);
        takeCancel = layout.findViewById(R.id.take_cancel);
        dialog = new AlertDialog.Builder(getActivity(),
                R.style.ActionSheetDialogStyle).create();
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
        takeAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectIconImage();
            }
        });
        takeCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user_icon:
                coverFlag=0;
                getSheet();
                break;
            case R.id.me_circle_layout:
                Intent toMyCircle = new Intent(FusionAction.MINE_CIRCLE);
                startActivity(toMyCircle);
                break;
            case R.id.exit_btn:
                exitLogin();
                break;
            case R.id.me_modify_layout:
                Intent toUserInfo = new Intent(FusionAction.USER_INFO);
                startActivity(toUserInfo);
                break;
            case R.id.myFollow_txt:
                Intent followUser = new Intent(FusionAction.FOLLOW_USER);
                startActivity(followUser);
                break;
            case R.id.myFans_txt:
                Intent followFans= new Intent(FusionAction.FOLLOW_FANS);
                startActivity(followFans);
                break;
            case R.id.backdrop:
                coverFlag=1;
                getSheet();
                break;
            default:
                break;
        }
    }

    /**
     * 调用相机拍照
     */
    public void getPicFromCamera() {
        // dialog.dismiss();
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            strImgPath = Utility.getFileCameraPath(getContext());// 存放照片的文件夹
            fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())
                    + ".png";// 照片命名
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
            Toast.makeText(getContext(),
                    getString(R.string.check_sd_card),
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 从相册选择照片
     */
    public void selectIconImage() {
        //  dialog.dismiss();
        //跳转选择图片页面
        Intent pickPhoto = new Intent(FusionAction.IMAGE_LOADER_ACTION);
        //参数2位设置可选择图片的最大值
        pickPhoto.putExtra(IMAGE_LOADER_COUNT_EXTRA, 1);
        startActivityForResult(pickPhoto, REQUEST_ADD_IMAGE_CODE);
    }

    /**
     * 上传图片
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                    break;
                default:
                    break;
            }
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x1125:
                    //TODO: update User表头像文件名和头像图片url字段
                    Image img = (Image) msg.obj;
                    final String netImgPath = img.getImagePath();
                    final String netImgName = img.getImageName();
                    String userObjectId = getCurrentUser().getObjectId();
                    AVObject todo = AVObject.createWithoutData("_User",userObjectId);
                    if(coverFlag==0){//上传头像
                        todo.put("user_icon_url", netImgPath);
                        todo.put("userIcon", netImgName);
                        todo.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null) {
                                    ToastUtils.showToast(getActivity(),getString(R.string.upload_usericon_success));
                                    AVFile file = new AVFile(netImgName,
                                            netImgPath,
                                            new HashMap<String, Object>());
                                    String thumbUrl = file.getThumbnailUrl(true, 128, 128);
                                    ImageLoader.getInstance().displayImage(thumbUrl,user_icon,MyImageLoader.MyCircleDisplayImageOptions(), animateFirstListener);
                                } else {
                                    ToastUtils.showToast(getActivity(),getString(R.string.upload_usericon_failed));
                                }
                            }
                        });
                    }else if(coverFlag==1){
                        todo.put("user_cover_img_url", netImgPath);
                        todo.put("user_cover_img_name", netImgName);
                        todo.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null) {
                                    ToastUtils.showToast(getActivity(),getString(R.string.upload_cover_success));
                                    AVFile file = new AVFile(netImgName, netImgPath,new HashMap<String, Object>());
                                    String thumbUrl = file.getThumbnailUrl(true, 1920, 1080);
                                    ImageLoader.getInstance().displayImage(thumbUrl,backdrop,MyImageLoader.MyNormalCoverImageOptions(), animateFirstListener);
                                } else {
                                    ToastUtils.showToast(getActivity(),getString(R.string.upload_cover_failed));
                                }
                            }
                        });
                    }
                    break;
            }
        }
    };

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
     * 退出登录
     */
    private void exitLogin() {
        XanderPanel.Builder mBuilder = new XanderPanel.Builder(getActivity());
        mBuilder.setTitle(getString(R.string.tip))
                .setMessage(getString(R.string.exit_login_tip))
                .setGravity(Gravity.BOTTOM)
                .setController(getString(R.string.tip_cancel), getString(R.string.tip_sure), new PanelInterface.PanelControllerListener() {
                    @Override
                    public void onPanelNagetiiveClick(XanderPanel panel) {
                        ToastUtils.showToast(getActivity(),"取消");
                    }
                    @Override
                    public void onPanelPositiveClick(XanderPanel panel) {
                        AVUser.logOut();//清除缓存用户对象
                        AVUser currentUser = getCurrentUser();
                        if (currentUser == null) {
                            //跳转到首页
                            Intent intent = new Intent(FusionAction.LOGIN_ACTION);
                            startActivity(intent);
                            getActivity().finish();
                        }
                    }
                })
                .setCanceledOnTouchOutside(true);
        XanderPanel xanderPanel = mBuilder.create();
        xanderPanel.show();
    }

    /**
     * 获取我的关注
     */
    private void getMyFollow(String userObjectId) {
        AVQuery<AVUser> followeeQuery = AVUser.followeeQuery(userObjectId, AVUser.class);
        followeeQuery.findInBackground(new FindCallback<AVUser>() {
            @Override
            public void done(List<AVUser> avObjects, AVException avException) {
                //avObjects 就是用户的关注用户列表
                if (avObjects != null) {
                    myFollow_txt.setText("我的关注  " + avObjects.size());
                }
            }
        });
    }

    /**
     * 得到我的粉丝
     */
    private void getMyFans() {
        AVUser currentUser = AVUser.getCurrentUser();
        AVQuery<AVUser> followerQuery = null;
        try {
            followerQuery = currentUser.followerQuery(AVUser.class);
            followerQuery.findInBackground(new FindCallback<AVUser>() {
                @Override
                public void done(List<AVUser> avObjects, AVException avException) {
                    // avObjects 包含了 userA 的粉丝列表
                    if (avObjects != null) {
                        myFans_txt.setText("我的粉丝  " + avObjects.size());
                    }
                }
            });
        } catch (AVException e) {
            e.printStackTrace();
        }
    }

    private void getSheet() {
        XanderPanel.Builder mBuilder = new XanderPanel.Builder(getActivity());
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
     * 广播注册 用于修改头像 昵称等
     */
    class UpDateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("modifyUserIcon")||intent.getAction().equals("modifyNickName")) {
                  fetchData();
            }
        }
    }

    private void upViewData(){
        updateReceiver = new UpDateReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("modifyUserIcon");
        filter.addAction("modifyNickName");
        getActivity().registerReceiver(updateReceiver, filter);
    }

    /**
     * 销毁广播
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (updateReceiver != null) {
            getActivity().unregisterReceiver(updateReceiver);
        }
    }
    /**
     * 拉取服务端最新数据
     */
    private void fetchData() {
        AVObject todo = AVObject.createWithoutData("_User", getCurrentUser().getObjectId());
        todo.fetchInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                if(e==null){
                    nickName = avObject.getString("nickname");
                    userIconUrl = avObject.getString("user_icon_url").toString();
                    user_nickName.setText(nickName);
                    if (!TextUtils.isEmpty(userIconUrl)) {
                        //暂时还没有头像
                        ImageLoader.getInstance().displayImage(userIconUrl, user_icon, MyImageLoader.MyCircleDisplayImageOptions(), animateFirstListener);
                    }
                }
            }
        });
    }

}
