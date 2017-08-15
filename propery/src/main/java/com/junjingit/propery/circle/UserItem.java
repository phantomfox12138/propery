package com.junjingit.propery.circle;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
/**
 * Created by jxy on 2017/8/15.
 */

public class UserItem extends AVObject{
    private String userName;
    private String sortLetters;//拼音的首字母
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
