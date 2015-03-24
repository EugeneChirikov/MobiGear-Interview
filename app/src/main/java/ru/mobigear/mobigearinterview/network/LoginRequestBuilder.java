package ru.mobigear.mobigearinterview.network;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by eugene on 3/23/15.
 */
public class LoginRequestBuilder extends AbstractRequestBuilder {
    private String email;
    private String password;

    public LoginRequestBuilder(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Override
    public String getURL() {
        return SERVER + "auth";
    }

    @Override
    public JSONObject getPostParameters() {
        JSONObject jsonPostParameters = new JSONObject();
        try
        {
            jsonPostParameters.put("email", email);
            jsonPostParameters.put("password", password);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        return jsonPostParameters;
    }
}
