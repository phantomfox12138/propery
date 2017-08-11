package com.junjingit.propery.common;

/**
 * Created by niufan on 17/7/13.
 */

public class FusionAction {

    public static final String HOME_PAGE_ACTION = "com.junjingit.propery.HOME_PAGE";

    public static final String LOGIN_ACTION = "com.junjingit.propery.LOGIN_ACTION";

    public static final String IMAGE_LOADER_ACTION = "com.junjing.propery.IMAGE_LOADER";

    public static final String REGISTER_ACTION = "com.junjingit.propery.REGISTER_ACTION";

    public static final String FORGET_ACTION = "com.junjingit.propery.FORGET_ACTION";

    public static final String CYCLE_DETAIL_LIST_ACTION = "com.junjingit.propery.CYCLE_DETAIL_LIST";

    public static final String FOCUS_LIST_ACTION = "com.junjingit.propery.FOCUS_LIST";

    public static final String USER_STATUS_LIST_ACTION = "com.junjingit.propery.USER_STATUS";

    public static final String FRIEND_CYCLE_ACTION = "com.junjingit.propery.FRIEND_CYCLE";

    public static final String HOME_TITLE_LIST_ACTION = "com.junjingit.propery.HOME_TITLE_LIST";

    public static final String HOME_TITLE_PAY_ACTION = "com.junjingit.propery.PROPERY_PAY";

    public static final String MINE_CIRCLE = "com.junjingit.propery.PROPERY_MY_CIRCLE";

    public static final String MINE_NUMBER = "com.junjingit.propery.PROPERY_MY_NUMBER";

    public static final String USER_INFO = "com.junjingit.propery.PROPERY_MY_USERINFO";
    public interface HomeListExtra {
        String TYPE = "list_type";

        String NOTIFY = "notify";

        String ACTIVE = "active";

        String TITLE_NAME = "title_name";
    }

    public interface QuoteExtra {
        int REQUEST_ADD_IMAGE_CODE = 0x33ff;

        int REQUEST_CODE_ASK_PERMISSIONS = 0x22ff;

        /**
         * 获取图片总数
         */
        String IMAGE_LOADER_COUNT_EXTRA = "maxnum";

        String IMAGE_RESULT_LIST = "datalist";
    }

    public interface FocusListExtra {
        String OBJECT_ID = "object_id";

        String USER_ID = "user_id";

        String FROM = "from_where";
    }
    public interface  CircleListExtra{
        String CIRCLE_ID="circle_id";
    }

}
