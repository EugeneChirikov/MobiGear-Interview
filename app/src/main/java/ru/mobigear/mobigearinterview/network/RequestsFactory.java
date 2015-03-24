package ru.mobigear.mobigearinterview.network;

import android.os.Bundle;

import ru.mobigear.mobigearinterview.utils.Constants;

/**
 * Created by eugene on 3/24/15.
 */
public class RequestsFactory {
    public static final int UNKNOWN = 0;
    public static final int ARTICLES = 1;
    public static final int EVENTS = 2;
    public static final int PROFILE = 3;

    public static AbstractRequest instantiateQuery(Bundle bundle) {
        int queryType = bundle.getInt(Constants.REQUEST_TYPE_KEY, -1);
        switch (queryType) {
            case ARTICLES:
                return new ArticlesRequest();
            case EVENTS:
                return new EventsRequest();
            case PROFILE:
                return new ProfileRequest();
            default:
                return null;
        }
    }
}