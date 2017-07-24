package com.junjingit.propery.common;

/**
 * Created by niufan on 17/7/13.
 */

public class FusionAction
{
    
    public static final String HOME_PAGE_ACTION = "com.junjingit.propery.HOME_PAGE";
    
    public static final String LOGIN_ACTION = "com.junjingit.propery.LOGIN_ACTION";
    
    public static final String IMAGE_LOADER_ACTION = "com.junjing.propery.IMAGE_LOADER";
    
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
    
}
