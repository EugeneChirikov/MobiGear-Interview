package ru.mobigear.mobigearinterview.network;

import org.json.JSONObject;

/**
 * Created by eugene on 3/23/15.
 */
public abstract class AbstractRequestBuilder {
    protected static String SERVER =  "http://192.168.1.104:8080/";
    protected String token;

    public abstract String getURL();

    public JSONObject getPostParameters() {
        return null;
    }

    public final void setToken(String token) {
        this.token = token;
    }
}
