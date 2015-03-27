package ru.mobigear.mobigearinterview.network;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by eugene on 3/27/15.
 */
public class AttendEventRequest extends AbstractRequest {
    private long eventId;

    public AttendEventRequest(long eventId) {
        this.eventId = eventId;
    }

    @Override
    protected void init() {}

    @Override
    public String getURL() {
        return SERVER + "events/" + String.valueOf(eventId);
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
