package ru.mobigear.mobigearinterview.network;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;

import org.json.JSONObject;

import ru.mobigear.mobigearinterview.utils.Constants;

/**
 * Created by eugene on 3/23/15.
 */
public abstract class AbstractRequest {
    protected static String SERVER =  "http://192.168.1.104:8080/";
    protected String token;
    protected ResponseHandler responseHandler;
    protected int queryType = RequestsFactory.UNKNOWN;
    protected boolean needRetryRequest = false;

    public AbstractRequest() {
        init();
    }

    protected abstract void init();
    public abstract String getURL();

    public JSONObject getPostParameters() {
        return null;
    }

    public final void setToken(String token) {
        this.token = token;
    }

    public final void handleResponse(Context context, JSONObject result) {
        if (responseHandler != null)
            responseHandler.handle(context, result);
    }

    protected Bundle getBundle(Bundle parentBundle) {
        return parentBundle;
    }

    public final Bundle getBundle() {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.REQUEST_TYPE_KEY, queryType);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_DO_NOT_RETRY, !needRetryRequest);
        return getBundle(bundle);
    }

    public int getQueryType() {
        return queryType;
    }

    public void setNeedRetryRequest(boolean needRetryRequest) {
        this.needRetryRequest = needRetryRequest;
    }
}
