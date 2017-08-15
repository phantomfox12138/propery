package com.junjingit.propery.circle;

import java.util.Comparator;

/**
 * Created by jxy on 2017/8/15.
 */

public class PinyinComparator implements Comparator<UserItem> {
    @Override
    public int compare(UserItem o1, UserItem o2) {
        //这里主要是用来对ListView里面的数据根据ABCDEFG...来排序
        if (o2.getSortLetters().equals("#")) {
            return -1;
        } else if (o1.getSortLetters().equals("#")) {
            return 1;
        } else {
            return o1.getSortLetters().compareTo(o2.getSortLetters());
        }
    }
}
