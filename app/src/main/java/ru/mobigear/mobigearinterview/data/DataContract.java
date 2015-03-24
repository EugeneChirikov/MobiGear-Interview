package ru.mobigear.mobigearinterview.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by eugene on 3/24/15.
 */
public class DataContract {
    public static final String CONTENT_AUTHORITY = "ru.mobigear.mobigearinterview.provider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String MIMETYPE_DIR = "vnd.android.cursor.dir/";
    public static final String MIMETYPE_ITEM = "vnd.android.cursor.item/";
    public static final String PATH_ARTICLE = "article";
    public static final String PATH_EVENT = "event";
    public static final String PATH_PROFILE = "profile";


    public static Uri buildUri(long id, String path) {
        return ContentUris.withAppendedId(getContentUri(path), id);
    }

    public static String getRowIdFromUri(Uri uri) {
        return uri.getPathSegments().get(1);
    }

    public static Uri getContentUri(String path) {
        return BASE_CONTENT_URI.buildUpon().appendPath(path).build();
    }

    public static String getContentType(String path) {
        return MIMETYPE_DIR + CONTENT_AUTHORITY + "/" + path;
    }

    public static String getContentItemType(String path) {
        return MIMETYPE_ITEM + CONTENT_AUTHORITY + "/" + path;
    }

    public static final class Article implements BaseColumns {
        public static final String TABLE_NAME = "article";

        public static final String ARTICLE_ID = "article_id";
        public static final String TITLE = "title";
        public static final String BODY = "body";
        public static final String IMAGE_URL = "image_url";
    }

    public static final class Event implements BaseColumns {
        public static final String TABLE_NAME = "event";

        public static final String EVENT_ID = "event_id";
        public static final String TITLE = "title";
        public static final String BODY = "body";
        public static final String DATE = "date";
        public static final String IMAGE_URL = "image_url";
        public static final String IS_USER_REGISTERED = "is_user_registered";
    }

    public static final class Profile implements BaseColumns {
        public static final String TABLE_NAME = "profile";

        public static final String FIO = "fio";
        public static final String EMAIL = "email";
        public static final String PHONE = "phone";
        public static final String AVATAR_URL = "avatar_url";
    }
}