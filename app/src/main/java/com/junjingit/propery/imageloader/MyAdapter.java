package com.junjingit.propery.imageloader;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.junjingit.propery.R;
import com.junjingit.propery.imageloader.utils.CommonAdapter;
import com.junjingit.propery.imageloader.utils.ViewHolder;

public class MyAdapter extends CommonAdapter<String>
{
    
    /**
     * 用户选择的图片，存储为图片的完整路径
     */
    public static List<String> mSelectedImage = new LinkedList<String>();
    
    /**
     * 当前图片选择个数显示
     */
    private TextView mText;
    
    /**
     * 当前文件最多选择个数
     */
    private int MaxNum;
    
    /**
     * 文件夹路径
     */
    private String mDirPath;
    
    public MyAdapter(Context context, List<String> mDatas, int itemLayoutId,
            String dirPath)
    {
        super(context, mDatas, itemLayoutId);
        this.mDirPath = dirPath;
    }
    
    public void setTextView(TextView text, int maxnum)
    {
        mText = text;
        MaxNum = maxnum;
    }
    
    @Override
    public void convert(final ViewHolder helper, final String item)
    {
        //设置no_pic
        helper.setImageResource(R.id.id_item_image, R.mipmap.pictures_no);
        //设置no_selected
        helper.setImageResource(R.id.id_item_select,
                R.mipmap.picture_unselected);
        //设置图片
        helper.setImageByUrl(R.id.id_item_image, mDirPath + "/" + item);
        
        final ImageView mImageView = helper.getView(R.id.id_item_image);
        final ImageView mSelect = helper.getView(R.id.id_item_select);
        
        mImageView.setColorFilter(null);
        //设置ImageView的点击事件
        mImageView.setOnClickListener(new OnClickListener()
        {
            //选择，则将图片变暗，反之则反之
            @Override
            public void onClick(View v)
            {
                
                // 已经选择过该图片
                if (mSelectedImage.contains(mDirPath + "/" + item))
                {
                    mSelectedImage.remove(mDirPath + "/" + item);
                    mSelect.setImageResource(R.mipmap.picture_unselected);
                    mImageView.setColorFilter(null);
                    mText.setText("选择图片("
                            + String.valueOf(mSelectedImage.size()) + "/"
                            + String.valueOf(MaxNum) + ")");
                }
                else
                // 未选择该图片
                {
                    if (mSelectedImage.size() < MaxNum)
                    {
                        mSelectedImage.add(mDirPath + "/" + item);
                        mSelect.setImageResource(R.mipmap.pictures_selected);
                        mImageView.setColorFilter(Color.parseColor("#77000000"));
                        mText.setText("选取图片("
                                + String.valueOf(mSelectedImage.size()) + "/"
                                + String.valueOf(MaxNum) + ")");
                    }
                }
                
            }
        });
        
        /**
         * 已经选择过的图片，显示出选择过的效果
         */
        if (mSelectedImage.contains(mDirPath + "/" + item))
        {
            mSelect.setImageResource(R.mipmap.pictures_selected);
            mImageView.setColorFilter(Color.parseColor("#77000000"));
        }
        
    }
}
