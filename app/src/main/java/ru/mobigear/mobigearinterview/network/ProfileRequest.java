package ru.mobigear.mobigearinterview.network;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by eugene on 3/24/15.
 */
public class ProfileRequest extends AbstractRequest {
    @Override
    protected void init() {
        queryType = RequestsFactory.PROFILE;
        responseHandler = new ProfileResponseHandler();
    }

    @Override
    public String getURL() {
        return SERVER + "profile";
    }

    @Override
    public JSONObject getPostParameters() {
        JSONObject jsonPostParameters = new JSONObject();
        try
        {
            jsonPostParameters.put("token", token);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        return jsonPostParameters;
    }
}
