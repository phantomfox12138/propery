package com.junjingit.propery;

import static com.junjingit.propery.common.FusionAction.QuoteExtra.IMAGE_LOADER_COUNT_EXTRA;
import static com.junjingit.propery.common.FusionAction.QuoteExtra.IMAGE_RESULT_LIST;
import static com.junjingit.propery.common.FusionAction.QuoteExtra.REQUEST_ADD_IMAGE_CODE;

import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import com.avos.avoscloud.SaveCallback;
import com.junjingit.propery.common.FusionAction;
import com.tiancaicc.springfloatingactionmenu.Utils;
import com.xander.panel.PanelInterface;
import com.xander.panel.XanderPanel;

public class HomeSendActivity extends AppCompatActivity
{
    
    private Toolbar mToolbar;
    
    private TextView mTitle;
    
    private RecyclerView mCorpImage;
    
    private TextView mSendBtn;
    
    private EditText mContentEdit;
    
    private String mType;
    
    private ImagePerviewAdapter mImageAdapter;
    
    private List<Bitmap> mImageList = new ArrayList<>();
    
    private List<String> mImagePathList = new ArrayList<>();
    
    private AVObject mRequest;
    
    private XanderPanel mBackDialog;
    
    private Handler mHandler = new Handler()
    {
        
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 0x1125:
                    
                    List<Image> imgList = (List<Image>) msg.obj;
                    
                    List<String> imgNameList = new ArrayList<>();
                    List<String> imgUrlList = new ArrayList<>();
                    
                    for (int i = 0; i < imgList.size(); i++)
                    {
                        Image img = imgList.get(i);
                        
                        imgNameList.add(img.getImageName());
                        imgUrlList.add(img.getImagePath());
                    }
                    
                    mRequest.addAllUnique("imageName", imgNameList);
                    mRequest.addAllUnique("imageUrl", imgUrlList);
                    
                    mRequest.saveInBackground();
                    
                    break;
            }
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_send);
        
        initView();
    }
    
    private void initView()
    {
        mType = getIntent().getStringExtra(FusionAction.QuoteExtra.SEND_TYPE);
        
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitle = (TextView) findViewById(R.id.title);
        mCorpImage = (RecyclerView) findViewById(R.id.corp_image);
        mSendBtn = (TextView) findViewById(R.id.send_btn);
        mContentEdit = (EditText) findViewById(R.id.content_edit);
        
        mTitle.setText(FusionAction.QuoteExtra.SEND_FIX.equals(mType) ? "物业报修"
                : "意见反馈");
        
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
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
        
        XanderPanel.Builder builder = new XanderPanel.Builder(this);
        mBackDialog = builder.setTitle("提示")
                .setIcon(R.mipmap.ic_launcher)
                .setMessage("你确定要退出吗？")
                .setGravity(Gravity.TOP)
                .setController("取消",
                        "确定",
                        new PanelInterface.PanelControllerListener()
                        {
                            @Override
                            public void onPanelNagetiiveClick(XanderPanel panel)
                            {
                            }
                            
                            @Override
                            public void onPanelPositiveClick(XanderPanel panel)
                            {
                                finish();
                            }
                        })
                .setCanceledOnTouchOutside(true)
                .create();
        
        mSendBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String content = mContentEdit.getText().toString();
                
                mRequest = new AVObject(
                        FusionAction.QuoteExtra.SEND_FIX.equals(mType) ? "EquipmentFix"
                                : "Feedback");
                
                mRequest.put("textContent", content);
                mRequest.put("userId", AVUser.getCurrentUser().getObjectId());
                
                //                mRequest.put("user", AVUser.getCurrentUser());
                
                mRequest.saveInBackground(new SaveCallback()
                {
                    @Override
                    public void done(AVException e)
                    {
                        if (null == e)
                        {
                            Toast.makeText(HomeSendActivity.this,
                                    "发送成功",
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
                                        msg.what = 0x1125;
                                        
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
            return new ImageHolder(LayoutInflater.from(HomeSendActivity.this)
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
    
    @Override
    public void onBackPressed()
    {
        mBackDialog.show();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        
        switch (item.getItemId())
        {
            case android.R.id.home:
                
                mBackDialog.show();
                
                break;
        }
        
        return super.onOptionsItemSelected(item);
    }
}
