package com.junjingit.propery;

import java.io.Serializable;
import java.util.List;

/**
 * Created by niufan on 17/7/19.
 */

public class Image implements Serializable
{
    private String imagePath;
    
    private String imageName;
    
    private List<String> imagePathList;
    
    private List<String> imageNameList;
    
    private List<String> imgThumbUrlList;
    
    public String getImagePath()
    {
        return imagePath;
    }
    
    public void setImagePath(String imagePath)
    {
        this.imagePath = imagePath;
    }
    
    public String getImageName()
    {
        return imageName;
    }
    
    public void setImageName(String imageName)
    {
        this.imageName = imageName;
    }
    
    public List<String> getImagePathList()
    {
        return imagePathList;
    }
    
    public void setImagePathList(List<String> imagePathList)
    {
        this.imagePathList = imagePathList;
    }
    
    public List<String> getImageNameList()
    {
        return imageNameList;
    }
    
    public void setImageNameList(List<String> imageNameList)
    {
        this.imageNameList = imageNameList;
    }
    
    public List<String> getImgThumbUrlList()
    {
        return imgThumbUrlList;
    }
    
    public void setImgThumbUrlList(List<String> imgThumbUrlList)
    {
        this.imgThumbUrlList = imgThumbUrlList;
    }
}
