package com.junjingit.propery.imageloader;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.PopupWindow.OnDismissListener;

import com.junjingit.propery.R;
import com.junjingit.propery.imageloader.bean.ImageFloder;

import static com.junjingit.propery.common.FusionAction.QuoteExtra.IMAGE_LOADER_COUNT_EXTRA;
import static com.junjingit.propery.common.FusionAction.QuoteExtra.IMAGE_RESULT_LIST;
import static com.junjingit.propery.common.FusionAction.QuoteExtra.REQUEST_CODE_ASK_PERMISSIONS;

public class ImageLoadActivity extends Activity implements
        ListImageDirPopupWindow.OnImageDirSelected
{
    /**
     * 记录最多选择个数
     */
    private int maxnum;
    
    private ProgressDialog mProgressDialog;
    
    /**
     * 存储文件夹中的图片数量
     */
    private int mPicsSize;
    
    /**
     * 图片数量最多的文件夹
     */
    private File mImgDir;
    
    /**
     * 所有的图片
     */
    private List<String> mImgs;
    
    private TextView mTitle;
    
    private GridView mGirdView;
    
    private MyAdapter mAdapter;
    
    /**
     * 临时的辅助类，用于防止同一个文件夹的多次扫描
     */
    private HashSet<String> mDirPaths = new HashSet<String>();
    
    /**
     * 扫描拿到所有的图片文件夹
     */
    private List<ImageFloder> mImageFloders = new ArrayList<ImageFloder>();
    
    private RelativeLayout mBottomLy;
    
    private TextView mChooseDir;
    
    private TextView mImageCount;
    
    int totalCount = 0;
    
    private int mScreenHeight;
    
    private ListImageDirPopupWindow mListImageDirPopupWindow;
    
    private Handler mHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            mProgressDialog.dismiss();
            // 为View绑定数据
            data2View();
            // 初始化展示文件夹的popupWindw
            initListDirPopupWindw();
        }
    };
    
    /**
     * 为View绑定数据
     */
    private void data2View()
    {
        if (mImgDir == null)
        {
            Toast.makeText(getApplicationContext(),
                    "擦，一张图片没扫描到",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        
        mImgs = Arrays.asList(mImgDir.list());
        /**
         * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
         */
        mAdapter = new MyAdapter(getApplicationContext(), mImgs,
                R.layout.imageloade_grid_item, mImgDir.getAbsolutePath());
        mAdapter.setTextView(mTitle, maxnum);
        mGirdView.setAdapter(mAdapter);
        mImageCount.setText(totalCount + "张");
    }
    
    ;
    
    /**
     * 初始化展示文件夹的popupWindw
     */
    private void initListDirPopupWindw()
    {
        mListImageDirPopupWindow = new ListImageDirPopupWindow(
                LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.7),
                mImageFloders, LayoutInflater.from(getApplicationContext())
                        .inflate(R.layout.imageloade_list_dir, null));
        
        mListImageDirPopupWindow.setOnDismissListener(new OnDismissListener()
        {
            
            @Override
            public void onDismiss()
            {
                // 设置背景颜色变暗
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });
        // 设置选择文件夹的回调
        mListImageDirPopupWindow.setOnImageDirSelected(this);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.imageloader);
        
        Intent intent = getIntent();
        maxnum = intent.getExtras().getInt(IMAGE_LOADER_COUNT_EXTRA);
        
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        mScreenHeight = outMetrics.heightPixels;
        
        initView();
        getImages();
        initEvent();
        
    }
    
    /**
     * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹
     */
    private void getImages()
    {
        if (!Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED))
        {
            Toast.makeText(this,
                    R.string.imageloader_extrnal_storage_not_exist,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        
        int hasReadExternalStoragePermission = ContextCompat.checkSelfPermission(ImageLoadActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        
        if (hasReadExternalStoragePermission != PackageManager.PERMISSION_GRANTED)
        {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(ImageLoadActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE))
            {
                
                new AlertDialog.Builder(ImageLoadActivity.this).setTitle(R.string.warning)
                        .setMessage(R.string.imageloader_alert_user_allow_access_extrnal_storage)
                        .setPositiveButton(R.string.confirm,
                                new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                            int which)
                                    {
                                        ActivityCompat.requestPermissions(ImageLoadActivity.this,
                                                new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                                                REQUEST_CODE_ASK_PERMISSIONS);
                                    }
                                })
                        .create()
                        .show();
                
                return;
            }
            ActivityCompat.requestPermissions(ImageLoadActivity.this,
                    new String[] { Manifest.permission.WRITE_CONTACTS },
                    REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }
        else
        {
            scanImage();
        }
        
    }
    
    private void initTitle()
    {
        mTitle = (TextView) findViewById(R.id.title);
        mTitle.setText("选择图片(0/" + String.valueOf(maxnum) + ")");
        
        TextView complete = (TextView) findViewById(R.id.add);
        ImageView cancle = (ImageView) findViewById(R.id.back);
        
        complete.setTextSize(15f);
        //        cancle.setTextSize(15f);
        
        complete.setText("完成");
        //        cancle.setText("返回");
        
        complete.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                ArrayList<String> datalist = new ArrayList<String>();
                datalist.addAll(mAdapter.mSelectedImage);
                Log.d("ImageLoadActivity", "datalist size=" + datalist.size());
                Intent intent = getIntent();
                intent.putStringArrayListExtra(IMAGE_RESULT_LIST, datalist);
                setResult(RESULT_OK, intent);
                finish();
                mAdapter.mSelectedImage.clear();
            }
        });
        
        cancle.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                mAdapter.mSelectedImage.clear();
                finish();
            }
        });
    }
    
    /**
     * 初始化View
     */
    private void initView()
    {
        initTitle();
        mGirdView = (GridView) findViewById(R.id.id_gridView);
        mChooseDir = (TextView) findViewById(R.id.id_choose_dir);
        mImageCount = (TextView) findViewById(R.id.id_total_count);
        
        mBottomLy = (RelativeLayout) findViewById(R.id.id_bottom_ly);
        
    }
    
    private void initEvent()
    {
        /**
         * 为底部的布局设置点击事件，弹出popupWindow
         */
        mBottomLy.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mListImageDirPopupWindow.setAnimationStyle(R.style.anim_popup_dir);
                mListImageDirPopupWindow.showAsDropDown(mBottomLy, 0, 0);
                
                // 设置背景颜色变暗
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = .3f;
                getWindow().setAttributes(lp);
            }
        });
    }
    
    @Override
    public void selected(ImageFloder floder)
    {
        
        mImgDir = new File(floder.getDir());
        mImgs = Arrays.asList(mImgDir.list(new FilenameFilter()
        {
            @Override
            public boolean accept(File dir, String filename)
            {
                if (filename.endsWith(".jpg") || filename.endsWith(".png")
                        || filename.endsWith(".jpeg"))
                    return true;
                return false;
            }
        }));
        /**
         * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
         */
        mAdapter = new MyAdapter(getApplicationContext(), mImgs,
                R.layout.imageloade_grid_item, mImgDir.getAbsolutePath());
        mAdapter.setTextView(mTitle, maxnum);
        mGirdView.setAdapter(mAdapter);
        // mAdapter.notifyDataSetChanged();
        mImageCount.setText(floder.getCount() + "张");
        mChooseDir.setText(floder.getName());
        mListImageDirPopupWindow.dismiss();
        
    }
    
    @Override
    protected void onDestroy()
    {
        mAdapter.mSelectedImage.clear();
        super.onDestroy();
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode,
            @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (requestCode == REQUEST_CODE_ASK_PERMISSIONS)
        {
            if (permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                //用户同意使用write
                scanImage();
            }
            else
            {
                //用户不同意，自行处理即可
                finish();
            }
        }
    }
    
    private void scanImage()
    {
        // 显示进度条
        mProgressDialog = ProgressDialog.show(this,
                null,
                getString(R.string.imageloader_loading));
        
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                
                String firstImage = null;
                
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = ImageLoadActivity.this.getContentResolver();
                
                // 只查询jpeg和png的图片
                Cursor mCursor = mContentResolver.query(mImageUri,
                        null,
                        MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[] { "image/jpeg" },
                        MediaStore.Images.Media.DATE_MODIFIED);
                
                Log.e("TAG", mCursor.getCount() + "");
                while (mCursor.moveToNext())
                {
                    // 获取图片的路径
                    String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    
                    Log.e("TAG", path);
                    // 拿到第一张图片的路径
                    if (firstImage == null)
                        firstImage = path;
                    // 获取该图片的父路径名
                    File parentFile = new File(path).getParentFile();
                    if (parentFile == null)
                        continue;
                    String dirPath = parentFile.getAbsolutePath();
                    ImageFloder imageFloder = null;
                    // 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
                    if (mDirPaths.contains(dirPath))
                    {
                        continue;
                    }
                    else
                    {
                        mDirPaths.add(dirPath);
                        // 初始化imageFloder
                        imageFloder = new ImageFloder();
                        imageFloder.setDir(dirPath);
                        imageFloder.setFirstImagePath(path);
                    }
                    
                    int picSize = parentFile.list(new FilenameFilter()
                    {
                        @Override
                        public boolean accept(File dir, String filename)
                        {
                            if (filename.endsWith(".jpg")
                                    || filename.endsWith(".png")
                                    || filename.endsWith(".jpeg"))
                                return true;
                            return false;
                        }
                    }).length;
                    totalCount += picSize;
                    
                    imageFloder.setCount(picSize);
                    mImageFloders.add(imageFloder);
                    
                    if (picSize > mPicsSize)
                    {
                        mPicsSize = picSize;
                        mImgDir = parentFile;
                    }
                }
                mCursor.close();
                
                // 扫描完成，辅助的HashSet也就可以释放内存了
                mDirPaths = null;
                
                // 通知Handler扫描图片完成
                mHandler.sendEmptyMessage(0x110);
                
            }
        }).start();
    }
}
