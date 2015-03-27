package ru.mobigear.mobigearinterview.utils;

/**
 * Created by eugene on 3/23/15.
 */
public class Constants {
    public static final String USER_NAME_KEY = "user_name_key";
    public static final String LAST_USER_LOGIN_KEY = "last_user_login_key";
    public static final String ACCOUNT_TYPE = "mobigear_interview_account";
    public static final String ACCOUNT_TOKEN_TYPE = "mobigear_interview_account_token_type";
    public static final String REQUEST_TYPE_KEY = "request_type_key";
    public static final String ARTICLE_ID_KEY = "article_id_key";
    public static final String EVENT_ID_KEY = "event_id_key";

    public static class NetworkResponseCode {
        public static final int ERROR_UNKNOWN = -1;
        public static final int SUCCESS = 200;
    }


    public static class Navigation {
        public static final int USER = 0;
        public static final int LOGOUT = 1;
        public static final int ARTICLES = 2;
        public static final int EVENTS = 3;
    }
}
