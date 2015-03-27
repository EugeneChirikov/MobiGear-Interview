package ru.mobigear.mobigearinterview.network;

import org.json.JSONObject;

/**
 * Created by eugene on 3/27/15.
 */
public interface ResponseListener {
    public void onError(int errorCode);
    public void onSuccess(JSONObject response);
}
