package ru.mobigear.mobigearinterview.network;

import org.json.JSONObject;

/**
 * Created by eugene on 3/23/15.
 */
public class TokenParser extends ResponseParser<String> {
    @Override
    public String parseData(JSONObject response) {
        return response.optString("token");
    }
}