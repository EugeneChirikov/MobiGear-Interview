package ru.mobigear.mobigearinterview.network;

import android.content.Context;

import org.json.JSONObject;

/**
 * Created by eugene on 3/24/15.
 */
public interface ResponseHandler {
    public void handle(Context context, JSONObject result);
}
