package com.junjingit.propery.common;

/**
 * Created by niufan on 17/7/13.
 */

public class FusionAction
{
    
    public static final String HOME_PAGE_ACTION = "com.junjingit.propery.HOME_PAGE";
    
    public static final String LOGIN_ACTION = "com.junjingit.propery.LOGIN_ACTION";
    
    public static final String IMAGE_LOADER_ACTION = "com.junjing.propery.IMAGE_LOADER";
    
    public static final String REGISTER_ACTION = "com.junjingit.propery.REGISTER_ACTION";
    
    public static final String FORGET_ACTION = "com.junjingit.propery.FORGET_ACTION";
    
    public static final String CYCLE_DETAIL_LIST_ACTION = "com.junjingit.propery.CYCLE_DETAIL_LIST";
    
    public static final String FOCUS_LIST_ACTION = "com.junjingit.propery.FOCUS_LIST";
    
    public interface QuoteExtra
    {
        public int REQUEST_ADD_IMAGE_CODE = 0x33ff;
        
        public int REQUEST_CODE_ASK_PERMISSIONS = 0x22ff;
        
        /**
         * 获取图片总数
         */
        public String IMAGE_LOADER_COUNT_EXTRA = "maxnum";
        
        public String IMAGE_RESULT_LIST = "datalist";
    }
    
    public interface FocusListExtra
    {
        public String OBJECT_ID = "object_id";
        
        public String FROM = "from_where";
    }
    
}
