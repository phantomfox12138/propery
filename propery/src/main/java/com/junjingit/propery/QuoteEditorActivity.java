package com.junjingit.propery;

import static com.junjingit.propery.common.FusionAction.QuoteExtra.IMAGE_LOADER_COUNT_EXTRA;
import static com.junjingit.propery.common.FusionAction.QuoteExtra.IMAGE_RESULT_LIST;
import static com.junjingit.propery.common.FusionAction.QuoteExtra.REQUEST_ADD_IMAGE_CODE;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.junjingit.propery.common.FusionAction;
import com.ns.developer.tagview.entity.Tag;
import com.ns.developer.tagview.widget.TagCloudLinkView;
import com.tiancaicc.springfloatingactionmenu.Utils;

public class QuoteEditorActivity extends AppCompatActivity implements
        TagCloudLinkView.OnTagSelectListener
{
    
    private static final String TAG = "QuoteEditorActivity";
    
    private RecyclerView mCorpImage;
    
    private TextView mLocationText;
    
    private TextView mSendBtn;
    
    private TagCloudLinkView mTagList;
    
    private TagCloudLinkView mTagList2;
    
    private ImagePerviewAdapter mImageAdapter;
    
    private List<Bitmap> mImageList;
    
    private String provider;//位置提供器
    
    private LocationManager locationManager;//位置服务
    
    private Location location;
    
    private EditText mContentEdit;
    
    private AVObject mRequest;
    
    private List<String> mImagePathList = new ArrayList<>();
    
    private List<AVObject> mCycleList = new ArrayList<>();
    
    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            
            switch (msg.what)
            {
                case 0x1123:
                    
                    List<Image> imgList = (List<Image>) msg.obj;
                    String imageName = "";
                    String imageUrl = "";
                    
                    for (int i = 0; i < imgList.size(); i++)
                    {
                        Image img = imgList.get(i);
                        
                        imageName += img.getImageName() + ",";
                        imageUrl += img.getImagePath() + ",";
                    }
                    
                    imageName = imageName.substring(0, imageName.length() - 1);
                    imageUrl = imageUrl.substring(0, imageUrl.length() - 1);
                    
                    mRequest.put("image_name", imageName);
                    mRequest.put("image", imageUrl);
                    
                    mRequest.saveInBackground();
                    
                    break;
            }
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote_editor);
        
        initData();
        
        initView();
    }
    
    private void initData()
    {
        mTagList = (TagCloudLinkView) findViewById(R.id.tag_list);
        mTagList2 = (TagCloudLinkView) findViewById(R.id.tag_list2);
        
        mTagList.setOnTagSelectListener(this);
        
        mTagList2.setOnTagSelectListener(this);
        
        AVUser.getCurrentUser()
                .getRelation("cycle")
                .getQuery()
                .findInBackground(new FindCallback<AVObject>()
                {
                    @Override
                    public void done(List<AVObject> list, AVException e)
                    {
                        if (null == e && null != list && list.size() > 0)
                        {
                            mCycleList.clear();
                            mCycleList.addAll(list);
                            
                            for (int i = 0; i < list.size(); i++)
                            {
                                mTagList2.add(new Tag(i, list.get(i)
                                        .getString("cycle_name")));
                                
                                mTagList2.drawTags();
                            }
                        }
                    }
                });
        
    }
    
    private void initPosition()
    {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);//获得位置服务
        provider = judgeProvider(locationManager);
        
        if (provider != null)
        {//有位置提供器的情况
         //为了压制getLastKnownLocation方法的警告
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                return;
            }
            
            location = locationManager.getLastKnownLocation(provider);
            
            if (location != null)
            {
                getLocation(location);//得到当前经纬度并开启线程去反向地理编码
            }
            else
            {
                mLocationText.setText("暂时无法获得当前位置");
            }
        }
        else
        {//不存在位置提供器的情况
        
        }
    }
    
    /**
     * 得到当前经纬度并开启线程去反向地理编码
     */
    public void getLocation(Location location)
    {
        String latitude = location.getLatitude() + "";
        String longitude = location.getLongitude() + "";
        String url = "http://api.map.baidu.com/geocoder/v2/?ak=pPGNKs75nVZPloDFuppTLFO3WXebPgXg&callback=renderReverse&location="
                + latitude + "," + longitude + "&output=json&pois=0";
        //        new MyAsyncTask(url).execute();
    }
    
    /**
     * 判断是否有可用的内容提供器
     * 
     * @return 不存在返回null
     */
    private String judgeProvider(LocationManager locationManager)
    {
        List<String> prodiverlist = locationManager.getProviders(true);
        if (prodiverlist.contains(LocationManager.NETWORK_PROVIDER))
        {
            return LocationManager.NETWORK_PROVIDER;
        }
        else if (prodiverlist.contains(LocationManager.GPS_PROVIDER))
        {
            return LocationManager.GPS_PROVIDER;
        }
        else
        {
            Toast.makeText(this, "没有可用的位置提供器", Toast.LENGTH_SHORT).show();
        }
        return null;
    }
    
    private void initView()
    {
        mCorpImage = (RecyclerView) findViewById(R.id.corp_image);
        mLocationText = (TextView) findViewById(R.id.location_text);
        mSendBtn = (TextView) findViewById(R.id.send_btn);
        mContentEdit = (EditText) findViewById(R.id.content_edit);
        
        mImageList = new ArrayList<Bitmap>();
        mImageList.add(BitmapFactory.decodeResource(getResources(),
                R.mipmap.add_image));
        
        mImageAdapter = new ImagePerviewAdapter();
        mCorpImage.setLayoutManager(new GridLayoutManager(this, 3));
        SpacesItemDecoration decoration = new SpacesItemDecoration(
                Utils.dpToPx(Utils.getDimension(this, R.dimen.dp_2),
                        getResources()));
        mCorpImage.addItemDecoration(decoration);
        
        mImageAdapter.setList(mImageList);
        mCorpImage.setAdapter(mImageAdapter);
        
        mSendBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String content = mContentEdit.getText().toString();
                
                mRequest = new AVObject("Public_Status");
                mRequest.put("message", content);
                
                Tag tag = mTagList.getTags().get(0);
                
                for (int i = 0; i < mCycleList.size(); i++)
                {
                    String cycleName = mCycleList.get(i)
                            .getString("cycle_name");
                    
                    if (tag.getText().equals(cycleName))
                    {
                        mRequest.put("cycle_id", mCycleList.get(i)
                                .getObjectId());
                    }
                    
                }
                
                mRequest.put("userId", AVUser.getCurrentUser().getUsername());
                
                mRequest.saveInBackground(new SaveCallback()
                {
                    @Override
                    public void done(AVException e)
                    {
                        if (null == e)
                        {
                            Toast.makeText(QuoteEditorActivity.this,
                                    "提交成功",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
                final List<Image> uploadImagePath = new ArrayList<Image>();
                for (int i = 0; i < mImagePathList.size(); i++)
                {
                    try
                    {
                        String[] ss = mImagePathList.get(i).split("/");
                        final String imageName = ss[ss.length - 1];
                        
                        final AVFile image = AVFile.withAbsoluteLocalPath(imageName,
                                mImagePathList.get(i));
                        image.saveInBackground(new SaveCallback()
                        {
                            @Override
                            public void done(AVException e)
                            {
                                
                                if (null == e)
                                {
                                    Image img = new Image();
                                    img.setImagePath(image.getUrl());
                                    img.setImageName(imageName);
                                    
                                    uploadImagePath.add(img);
                                    
                                    if (uploadImagePath.size() == mImagePathList.size())
                                    {
                                        
                                        Message msg = new Message();
                                        msg.obj = uploadImagePath;
                                        msg.what = 0x1123;
                                        
                                        mHandler.sendMessage(msg);
                                        
                                    }
                                }
                            }
                        });
                    }
                    catch (FileNotFoundException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
                case REQUEST_ADD_IMAGE_CODE:
                    
                    List<String> imageList = data.getStringArrayListExtra(IMAGE_RESULT_LIST);
                    if (imageList.size() + mImageList.size() > 6)
                    {
                        mImageList.remove(0);
                    }
                    
                    mImagePathList.clear();
                    mImagePathList.addAll(imageList);
                    
                    for (int i = 0; i < imageList.size(); i++)
                    {
                        mImageList.add(Utility.createImageThumbnail(imageList.get(i)));
                        
                    }
                    
                    mImageAdapter.notifyDataSetChanged();
                    
                    break;
            }
        }
    }
    
    @Override
    public void onBackPressed()
    {
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle(R.string.app_name)
                .setMessage("你确定要退出吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        finish();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        dialogInterface.dismiss();
                    }
                })
                .create();
        
        dialog.show();
        
    }
    
    @Override
    public void onTagSelected(TagCloudLinkView view, Tag tag, int position)
    {
        switch (view.getId())
        {
        
            case R.id.tag_list2:
                
                List<Tag> list = mTagList.getTags();
                
                if (list.size() > 0)
                {
                    for (int i = 0; i < list.size(); i++)
                    {
                        if (!list.get(i).getText().equals(tag.getText()))
                        {
                            mTagList.add(tag);
                            mTagList.drawTags();
                        }
                        
                    }
                }
                else
                {
                    mTagList.add(tag);
                    mTagList.drawTags();
                    
                }
                
                break;
        }
    }
    
    class ImagePerviewAdapter extends RecyclerView.Adapter<ImageHolder>
    {
        
        private List<Bitmap> list;
        
        public void setList(List<Bitmap> list)
        {
            this.list = list;
        }
        
        @Override
        public ImageHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            return new ImageHolder(
                    LayoutInflater.from(QuoteEditorActivity.this)
                            .inflate(R.layout.image_layout, null));
        }
        
        @Override
        public void onBindViewHolder(ImageHolder holder, final int position)
        {
            holder.image.setImageBitmap(list.get(position));
            holder.image.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if (position == 0)
                    {
                        Intent pickPhoto = new Intent(
                                FusionAction.IMAGE_LOADER_ACTION);
                        pickPhoto.putExtra(IMAGE_LOADER_COUNT_EXTRA, 6);
                        
                        startActivityForResult(pickPhoto,
                                REQUEST_ADD_IMAGE_CODE);
                    }
                }
            });
            
        }
        
        @Override
        public int getItemCount()
        {
            return null != list ? list.size() : 0;
        }
    }
    
    class ImageHolder extends RecyclerView.ViewHolder
    {
        
        private ImageView image;
        
        public ImageHolder(View itemView)
        {
            super(itemView);
            
            image = itemView.findViewById(R.id.image_item);
        }
    }
    
    class SpacesItemDecoration extends RecyclerView.ItemDecoration
    {
        
        private int space;
        
        public SpacesItemDecoration(int space)
        {
            this.space = space;
        }
        
        @Override
        public void getItemOffsets(Rect outRect, View view,
                RecyclerView parent, RecyclerView.State state)
        {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;
            //注释这两行是为了上下间距相同
            //        if(parent.getChildAdapterPosition(view)==0){
            outRect.top = space;
            //        }
        }
    }
    
}
