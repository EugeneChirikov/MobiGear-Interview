package ru.mobigear.mobigearinterview.network;

import org.json.JSONObject;

/**
 * Created by eugene on 3/23/15.
 */
public class ServerResponse {
    private int code;
    private JSONObject data;

    public void setCode(int code) {
        this.code = code;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public JSONObject getData() {
        return data;
    }

    public boolean isError() {
        return code != 200;
    }
}
