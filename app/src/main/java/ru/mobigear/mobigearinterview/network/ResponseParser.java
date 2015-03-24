package ru.mobigear.mobigearinterview.network;

import org.json.JSONObject;

/**
 * Created by eugene on 3/23/15.
 */
public abstract class ResponseParser<T> {

    public static ServerResponse parseResponse(JSONObject response) {
        if (response == null)
            return null;
        ServerResponse serverResponse = new ServerResponse();
        JSONObject result = response.optJSONObject("result");
        serverResponse.setCode(result.optInt("code", -1));
        serverResponse.setData(result);
        return serverResponse;
    }

    public abstract T parseData(JSONObject data);
}