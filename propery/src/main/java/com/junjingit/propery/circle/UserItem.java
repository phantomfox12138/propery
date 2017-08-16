package com.junjingit.propery.circle;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
/**
 * Created by jxy on 2017/8/15.
 */

public class UserItem extends AVObject{
    private String userName;
    private String sortLetters;//拼音的首字母
    private String user_img;
    private String focus_num;
    private String objectId;

    @Override
    public String getObjectId() {
        return objectId;
    }

    @Override
    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getUser_img() {
        return user_img;
    }

    public void setUser_img(String user_img) {
        this.user_img = user_img;
    }

    public String getFocus_num() {
        return focus_num;
    }

    public void setFocus_num(String focus_num) {
        this.focus_num = focus_num;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }
}
