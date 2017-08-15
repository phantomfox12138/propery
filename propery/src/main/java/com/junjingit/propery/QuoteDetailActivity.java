package com.junjingit.propery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVStatus;
import com.avos.avoscloud.AVStatusQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.junjingit.propery.widget.CollapsibleTextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.tiancaicc.springfloatingactionmenu.Utils;

public class QuoteDetailActivity extends AppCompatActivity
{
    
    private static final String TAG = "QuoteDetailActivity";
    
    private RecyclerView mCorpImage;
    
    private ImagePerviewAdapter mImageAdapter;
    
    private CollapsibleTextView mTextContent;
    
    private TextView mBackBtn;
    
    private TextView mUserName;
    
    private String mFullText; //= "I swirl, dip, leap and step, To the rhythmic, rolling, reverberating melody, Of gleaming copper, and polished bronze; A shivering note, long held in the air. The deep, monotonous, shivering song, of shining, gleaming, chiming bells. I must leave before the twelfth gong. Prepare my pumpkin. I lost my shoe.";
    
    private Image mImage;
    
    private RelativeLayout mCommentLayout;
    
    private RelativeLayout mDescLayout;
    
    private EditText mCommentEdit;
    
    private ImageView mCommentBtn;
    
    private RecyclerView mCommentList;
    
    private TextView mCommentCount;
    
    private TextView mZanCount;
    
    private RelativeLayout mZanLayout;
    
    private List<AVObject> mCommentDataList = new ArrayList<>();
    
    private String mObjId;
    
    private String mType;
    
    private boolean isReply;
    
    private AVObject mReplyObj;
    
    private CommentListAdapter mCommentAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote_detail);
        
        mObjId = getIntent().getStringExtra("objectId");
        mType = getIntent().getStringExtra("type");
        
        initView();
        
        initData();
        
    }
    
    private void initData()
    {
        mUserName = (TextView) findViewById(R.id.user_name);
        
        if (!StringUtil.isNullOrEmpty(mType) && mType.equals("status"))
        {
            AVStatusQuery query = AVStatus.inboxQuery(AVUser.getCurrentUser(),
                    AVStatus.INBOX_TYPE.TIMELINE.toString());
            
            query.getInBackground(mObjId, new GetCallback<AVStatus>()
            {
                @Override
                public void done(AVStatus avStatus, AVException e)
                {
                    if (null == e)
                    {
                        mFullText = avStatus.getMessage();
                        mTextContent.setFullString(mFullText);
                        
                        mUserName.setText(avStatus.getSource()
                                .getString("nickname"));
                        
                        loadStatusImages(avStatus);
                        
                    }
                }
            });
        }
        
        AVQuery<AVObject> avquery = new AVQuery<>("Public_Status");
        avquery.getInBackground(mObjId, new GetCallback<AVObject>()
        {
            @Override
            public void done(AVObject avObject, AVException e)
            {
                if (null == e)
                {
                    mFullText = avObject.getString("message");
                    mTextContent.setFullString(mFullText);
                    
                    AVQuery<AVUser> query = new AVQuery<AVUser>("_User");
                    query.getInBackground(avObject.getString("userId"),
                            new GetCallback<AVUser>()
                            {
                                @Override
                                public void done(AVUser avUser, AVException e)
                                {
                                    if (null == e)
                                    {
                                        mUserName.setText(avUser.getString("nickname"));
                                    }
                                }
                            });
                    
                    loadStatusImages(avObject);
                }
                
            }
        });
        
        updateCommentList();
    }
    
    private void loadStatusImages(AVObject avObject)
    {
        
        if (avObject instanceof AVStatus)
        {
            AVStatus avStatus = (AVStatus) avObject;
            
            List<String> imgNameList = new ArrayList<String>();
            List<String> imgUrlList = new ArrayList<String>();
            List<String> imgThumbList = new ArrayList<String>();
            
            String image = (String) avStatus.getData().get("image");
            String imageName = (String) avStatus.getData().get("imageName");
            
            if (!StringUtil.isNullOrEmpty(image)
                    && !StringUtil.isNullOrEmpty(imageName))
            {
                String[] images = image.split(",");
                String[] imageNames = imageName.split(",");
                
                for (int i = 0; i < images.length; i++)
                {
                    String imgUrl = images[i];
                    String imgName = imageNames[i];
                    
                    imgNameList.add(imgName);
                    imgUrlList.add(imgUrl);
                    
                    AVFile file = new AVFile(imgName, imgUrl,
                            new HashMap<String, Object>());
                    imgThumbList.add(file.getThumbnailUrl(true, 100, 100));
                    
                }
                
                mImage.setImageNameList(imgNameList);
                mImage.setImagePathList(imgUrlList);
                mImage.setImgThumbUrlList(imgThumbList);
                
                mImageAdapter.notifyDataSetChanged();
            }
        }
        else
        {
            List<String> imgNameList = new ArrayList<String>();
            List<String> imgUrlList = new ArrayList<String>();
            List<String> imgThumbList = new ArrayList<String>();
            
            String image = avObject.getString("image");
            String imageName = avObject.getString("image_name");
            
            if (!StringUtil.isNullOrEmpty(image)
                    && !StringUtil.isNullOrEmpty(imageName))
            {
                String[] images = image.split(",");
                String[] imageNames = imageName.split(",");
                
                for (int i = 0; i < images.length; i++)
                {
                    String imgUrl = images[i];
                    String imgName = imageNames[i];
                    
                    imgNameList.add(imgName);
                    imgUrlList.add(imgUrl);
                    
                    AVFile file = new AVFile(imgName, imgUrl,
                            new HashMap<String, Object>());
                    imgThumbList.add(file.getThumbnailUrl(true, 100, 100));
                    
                }
                
                mImage.setImageNameList(imgNameList);
                mImage.setImagePathList(imgUrlList);
                mImage.setImgThumbUrlList(imgThumbList);
                
                mImageAdapter.notifyDataSetChanged();
            }
        }
        
    }
    
    private void updateCommentList()
    {
        AVQuery<AVObject> queryComment = new AVQuery<>("Reply");
        queryComment.whereEqualTo("status_id", mObjId);
        
        queryComment.findInBackground(new FindCallback<AVObject>()
        {
            @Override
            public void done(List<AVObject> list, AVException e)
            {
                mCommentCount.setText(String.valueOf(list.size()));
                
                mCommentDataList.clear();
                
                mCommentDataList.addAll(list);
                mCommentAdapter.notifyDataSetChanged();
            }
        });
    }
    
    private void initView()
    {
        mCorpImage = (RecyclerView) findViewById(R.id.corp_image);
        mTextContent = (CollapsibleTextView) findViewById(R.id.text_content);
        mCommentLayout = (RelativeLayout) findViewById(R.id.comment_layout);
        mDescLayout = (RelativeLayout) findViewById(R.id.desc_layout);
        mCommentEdit = (EditText) findViewById(R.id.comment_edit);
        mCommentBtn = (ImageView) findViewById(R.id.comment_send_btn);
        mCommentList = (RecyclerView) findViewById(R.id.comment_list);
        mCommentCount = (TextView) findViewById(R.id.item_desc_count);
        mZanCount = (TextView) findViewById(R.id.item_zan_count);
        mZanLayout = (RelativeLayout) findViewById(R.id.zan_layout);
        
        AVQuery<AVObject> commentCountQuery = new AVQuery<>("Reply");
        commentCountQuery.whereEqualTo("status_id", mObjId);
        
        commentCountQuery.countInBackground(new CountCallback()
        {
            @Override
            public void done(int i, AVException e)
            {
                if (null == e)
                {
                    mCommentCount.setText(String.valueOf(i));
                }
            }
        });
        
        mZanLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                
                AVQuery<AVObject> query = new AVQuery<AVObject>("zan");
                query.whereEqualTo("status_id", mObjId);
                query.whereEqualTo("user_id", AVUser.getCurrentUser()
                        .getObjectId());
                
                query.findInBackground(new FindCallback<AVObject>()
                {
                    @Override
                    public void done(List<AVObject> list, AVException e)
                    {
                        if (null == e)
                        {
                            if (list.size() == 1)
                            {
                                list.get(0).deleteInBackground();
                                
                                String zanCount = mZanCount.getText()
                                        .toString();
                                
                                int count = Integer.valueOf(zanCount);
                                mZanCount.setText(String.valueOf(count - 1));
                            }
                            else
                            {
                                AVObject zanObj = new AVObject("zan");
                                zanObj.put("status_id", mObjId);
                                zanObj.put("user_id", AVUser.getCurrentUser()
                                        .getObjectId());
                                
                                zanObj.saveInBackground(new SaveCallback()
                                {
                                    @Override
                                    public void done(AVException e)
                                    {
                                        if (null == e)
                                        {
                                            AVQuery<AVObject> query = new AVQuery<>(
                                                    "zan");
                                            query.whereEqualTo("status_id",
                                                    mObjId);
                                            query.countInBackground(new CountCallback()
                                            {
                                                @Override
                                                public void done(int i,
                                                        AVException e)
                                                {
                                                    if (e == null)
                                                    {
                                                        mZanCount.setText(String.valueOf(i));
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
                
            }
        });
        
        AVQuery<AVObject> query = new AVQuery<>("zan");
        query.whereEqualTo("status_id", mObjId);
        query.findInBackground(new FindCallback<AVObject>()
        {
            @Override
            public void done(List<AVObject> list, AVException e)
            {
                if (null == e)
                {
                    mZanCount.setText(String.valueOf(list.size()));
                }
            }
        });
        
        mDescLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mCommentLayout.setVisibility(View.VISIBLE);
                
                mCommentEdit.setFocusable(true);
                mCommentEdit.setFocusableInTouchMode(true);
                mCommentEdit.requestFocus();
                mCommentEdit.setHint("发表评论");
                isReply = false;
                
                InputMethodManager inputManager = (InputMethodManager) mCommentEdit.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                
                //                inputManager.showSoftInput(mCommentEdit, 0);
                inputManager.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                
            }
        });
        
        mCommentBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String comment = mCommentEdit.getText().toString();
                
                if (!isReply)
                {
                    mReplyObj = new AVObject("Reply");
                    mReplyObj.put("fromUserId", AVUser.getCurrentUser()
                            .getUsername());
                    mReplyObj.put("comment", comment);
                }
                else
                {
                    mReplyObj.put("reply_comment", comment);
                }
                
                mReplyObj.put("status_id", mObjId);
                
                mReplyObj.saveInBackground(new SaveCallback()
                {
                    @Override
                    public void done(AVException e)
                    {
                        if (null == e)
                        {
                            Toast.makeText(QuoteDetailActivity.this,
                                    "发表成功",
                                    Toast.LENGTH_LONG).show();
                            
                            InputMethodManager inputManager = (InputMethodManager) mCommentEdit.getContext()
                                    .getSystemService(Context.INPUT_METHOD_SERVICE);
                            
                            inputManager.toggleSoftInput(0,
                                    InputMethodManager.HIDE_NOT_ALWAYS);
                            if (mCommentLayout.isShown())
                            {
                                mCommentLayout.setVisibility(View.GONE);
                            }
                            mCommentEdit.setText("");
                            
                            updateCommentList();
                        }
                    }
                });
            }
        });
        
        mTextContent.setCollapsedText(" 全文");
        mTextContent.setExpandedText(" 隐藏");
        
        mImage = new Image();
        
        mBackBtn = (TextView) findViewById(R.id.back_btn);
        
        mBackBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });
        
        mImageAdapter = new ImagePerviewAdapter();
        mCorpImage.setLayoutManager(new GridLayoutManager(this, 3));
        SpacesItemDecoration decoration = new SpacesItemDecoration(
                Utils.dpToPx(Utils.getDimension(this, R.dimen.dp_2),
                        getResources()));
        mCorpImage.addItemDecoration(decoration);
        
        mImageAdapter.setImage(mImage);
        
        mCorpImage.setAdapter(mImageAdapter);
        
        mCommentList.setLayoutManager(new LinearLayoutManager(this));
        mCommentAdapter = new CommentListAdapter();
        mCommentAdapter.setList(mCommentDataList);
        mCommentList.setAdapter(mCommentAdapter);
        
    }
    
    class CommentListAdapter extends RecyclerView.Adapter<CommentHolder>
    {
        
        List<AVObject> list;
        
        public void setList(List<AVObject> list)
        {
            this.list = list;
        }
        
        @Override
        public CommentHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            return new CommentHolder(
                    LayoutInflater.from(QuoteDetailActivity.this)
                            .inflate(R.layout.comment_item_layout, null));
        }
        
        @Override
        public void onBindViewHolder(CommentHolder holder, int position)
        {
            AVObject obj = list.get(position);
            final String fromUser = obj.getString("fromUserId");
            final String replyUser = obj.getString("replyUserId");
            String comment = obj.getString("comment");
            String replyComment = obj.getString("reply_comment");
            Date createTime = obj.getDate("createdAt");
            
            holder.commentContainer.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    
                    isReply = true;
                    mReplyObj = new AVObject("Reply");
                    mReplyObj.put("replyUserId", AVUser.getCurrentUser()
                            .getUsername());
                    mReplyObj.put("fromUserId", fromUser);
                    
                    mCommentLayout.setVisibility(View.VISIBLE);
                    
                    mCommentEdit.setHint("回复 " + fromUser);
                    mCommentEdit.setFocusable(true);
                    mCommentEdit.setFocusableInTouchMode(true);
                    mCommentEdit.requestFocus();
                    
                    InputMethodManager inputManager = (InputMethodManager) mCommentEdit.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    
                    inputManager.toggleSoftInput(0,
                            InputMethodManager.SHOW_FORCED);
                }
            });
            
            holder.userName.setText(fromUser);
            
            SimpleDateFormat formatter = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm");
            holder.commentCreateTime.setText(formatter.format(createTime));
            
            if (StringUtil.isNullOrEmpty(replyUser))
            {
                holder.commentContent.setText(comment);
                holder.commentBack.setVisibility(View.GONE);
                holder.backUserName.setVisibility(View.GONE);
            }
            else
            {
                holder.commentBack.setVisibility(View.VISIBLE);
                holder.commentContent.setText(replyComment);
                holder.backUserName.setVisibility(View.VISIBLE);
                holder.backUserName.setText(replyUser);
            }
            
        }
        
        @Override
        public int getItemCount()
        {
            return null != list ? list.size() : 0;
        }
    }
    
    class ImagePerviewAdapter extends RecyclerView.Adapter<ImageHolder>
    {
        
        private Image image;
        
        public void setImage(Image image)
        {
            this.image = image;
        }
        
        @Override
        public ImageHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            return new ImageHolder(
                    LayoutInflater.from(QuoteDetailActivity.this)
                            .inflate(R.layout.image_layout, null));
        }
        
        @Override
        public void onBindViewHolder(final ImageHolder holder,
                final int position)
        {
            
            ImageLoader.getInstance().loadImage(image.getImgThumbUrlList()
                    .get(position),
                    new ImageLoadingListener()
                    {
                        @Override
                        public void onLoadingStarted(String imageUri, View view)
                        {
                            
                        }
                        
                        @Override
                        public void onLoadingFailed(String imageUri, View view,
                                FailReason failReason)
                        {
                            
                        }
                        
                        @Override
                        public void onLoadingComplete(String imageUri,
                                View view, Bitmap loadedImage)
                        {
                            
                            holder.image.setImageBitmap(loadedImage);
                            
                        }
                        
                        @Override
                        public void onLoadingCancelled(String imageUri,
                                View view)
                        {
                            
                        }
                    });
            
            holder.image.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    
                }
            });
            
        }
        
        @Override
        public int getItemCount()
        {
            return null != image.getImageNameList() ? image.getImageNameList()
                    .size() : 0;
        }
    }
    
    class ImageHolder extends RecyclerView.ViewHolder
    {
        
        ImageView image;
        
        public ImageHolder(View itemView)
        {
            super(itemView);
            
            image = itemView.findViewById(R.id.image_item);
        }
    }
    
    class CommentHolder extends RecyclerView.ViewHolder
    {
        TextView userName;
        
        TextView commentBack;
        
        TextView backUserName;
        
        TextView commentContent;
        
        RelativeLayout commentContainer;
        
        TextView commentCreateTime;
        
        public CommentHolder(View itemView)
        {
            super(itemView);
            
            userName = itemView.findViewById(R.id.user_name);
            commentBack = itemView.findViewById(R.id.comment_back);
            backUserName = itemView.findViewById(R.id.back_user_name);
            commentContent = itemView.findViewById(R.id.comment_content);
            commentContainer = itemView.findViewById(R.id.comment_container);
            commentCreateTime = itemView.findViewById(R.id.create_time);
            
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
    
    @Override
    public void onBackPressed()
    {
        if (mCommentLayout.isShown())
        {
            mCommentLayout.setVisibility(View.GONE);
        }
        else
        {
            super.onBackPressed();
        }
    }
}
