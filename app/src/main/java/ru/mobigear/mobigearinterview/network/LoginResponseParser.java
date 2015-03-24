package ru.mobigear.mobigearinterview.network;

import org.json.JSONObject;

/**
 * Created by eugene on 3/23/15.
 */
public class LoginResponseParser extends ResponseParser<String> {
    @Override
    protected String parse(JSONObject response) {
        return response.optString("token");
    }
}