package com.polaris.blog.utils;

import javax.xml.crypto.Data;

public interface Constants {
    //登录端
    String FROM_PC = "p_";
    String FROM_MOBILE = "m_";

    //app下载地址，app可以二维码扫描登录
    String APP_DOWNLOAD_PATH = "/portal/app/";

    interface User{
        String ROLE_ADMIN = "role_admin";
        String ROLE_NORMAL = "role_normal";
        String DEFAULT_AVATAR = "https://ss1.bdstatic.com/70cFvXSh_" +
                "Q1YnxGkpoWK1HF6hhy/it/u=289446672,1693173871&fm=26&gp=0.jpg";
        String DEFAULT_STATE="1";
        String COOKIE_TOKEN_KEY = "polaris_blog_token";

        //Redis要用的key
        String KEY_CAPTCHA_CONTENT = "key_captcha_content_";
        String KEY_EMAIL_CONTENT = "key_email_content_";
        String KEY_EMAIL_SEND_IP = "key_email_send_ip";
        String KEY_EMAIL_SEND_ADDRESS = "key_email_send_ADDRESS";
        String KEY_TOKEN = "key_token_";
        String KEY_COMMIT_TOKEN_RECORD = "key_commit_token_record_";

        //二维码id前缀
        String KEY_PC_LOGIN_ID = "key_pc_login_id_";
        String KEY_PC_LOGIN_STATE_FALSE = "false";
        int QR_CODE_STATE_CHECK_WAITING_TIME = 30;

        String LAST_REQUEST_LOGIN_ID = "l_r_l_i";
    }

    interface  Setting{
        String MANAGER_ACCOUNT_INIT_STATE = "manager_account_init_state";
        //title
        String WEB_SIZE_TITLE = "web_size_title";
        //seo
        String WEB_SIZE_DESCRIPTION = "web_size_description";
        String WEB_SIZE_KEYWORDS = "web_size_keywords";
        //view_count
        String WEB_SIZE_VIEW_COUNT = "web_size_view_count";
    }

    interface Page {
        int DEFAULT_PAGE = 1;
        int MIN_SIZE = 1;
    }

    interface  ImageType {
        String PREFIX = "image/";

        String TYPE_PNG = "png";
        String TYPE_JPEG = "jpeg";
        String TYPE_JPG = "jpg";
        String TYPE_GIF = "gif";

        String TYPE_PREFIX_PNG = PREFIX + "png";
        String TYPE_PREFIX_JPEG = PREFIX + "jpeg";
        String TYPE_PREFIX_GIF = PREFIX + "gif";
    }

    interface Article {
        String TYPE_MARKDOWN = "1";
        String TYPE_RICH_TEXT = "0";
        int TITLE_MAX_LENGTH = 128;
        int SUMMARY_MAX_LENGTH = 256;
        //0-删除，1-已发布，2-草稿，3-置顶
        String STATE_DELETE = "0";
        String STATE_PUBLISH = "1";
        String STATE_DRAFT = "2";
        String STATE_TOP = "3";
        String KEY_ARTICLE_CACHE = "key_article_cache_";
        String KEY_ARTICLE_VIEW_COUNT = "key_article_view_count_";
        String KEY_ARTICLE_FIRST_PAGE_CACHE_BY_LABEL = "key_article_first_page_cache_by_label_";
    }

    interface Comment {
        int TITLE_MAX_LENGTH = 128;
        int SUMMARY_MAX_LENGTH = 256;
        //1-已发布,3-置顶
        String STATE_PUBLISH = "1";
        String STATE_TOP = "3";
        String KEY_COMMENT_FIRST_PAGE_CACHE = "key_comment_first_page_cache_";
    }

    interface TimeValue{
        int SECOND_10 = 10;
        int SECOND_30 = 30;
        int MIN = 60;
        int MIN_10 = 10 * MIN;
        int MIN_5 = 5 * MIN;
        int HOUR = 60 * MIN;
        int HOUR_2 = 2 * HOUR;
        int DAY = 24 * HOUR;
        int WEEK = 7 * DAY;
        int MONTH = 30 * DAY;
        int YEAR = 365 * DAY;
    }

}
