package ru.mobigear.mobigearinterview.network;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by eugene on 3/24/15.
 */
public class EventsRequest extends AbstractRequest {
    @Override
    protected void init() {
        queryType = RequestsFactory.EVENTS;
        responseHandler = new EventsResponseHandler();
    }

    @Override
    public String getURL() {
        return SERVER + "events";
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
