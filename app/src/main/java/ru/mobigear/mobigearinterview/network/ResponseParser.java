package ru.mobigear.mobigearinterview.network;

import org.json.JSONObject;

/**
 * Created by eugene on 3/23/15.
 */
public abstract class ResponseParser<T> {

    public ServerResponse<T> parseResponse(JSONObject response) {
        if (response == null)
            return null;
        ServerResponse<T> serverResponse = new ServerResponse<T>();
        JSONObject result = response.optJSONObject("result");
        serverResponse.setCode(result.optInt("code", -1));
        serverResponse.setData(parse(result));
        return serverResponse;
    }

    protected abstract T parse(JSONObject response);
}