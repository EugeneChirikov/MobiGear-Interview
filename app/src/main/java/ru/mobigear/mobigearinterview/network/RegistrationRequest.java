package ru.mobigear.mobigearinterview.network;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by eugene on 3/23/15.
 */
public class RegistrationRequest extends AbstractRequest {
    private String fio;
    private String email;
    private String phone;
    private String password;

    public RegistrationRequest(String fio, String email, String phone, String password) {
        this.fio = fio;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    @Override
    protected void init() {

    }

    @Override
    public String getURL() {
        return SERVER + "user";
    }

    @Override
    public JSONObject getPostParameters() {
        JSONObject jsonPostParameters = new JSONObject();
        try
        {
            jsonPostParameters.put("fio", fio);
            jsonPostParameters.put("email", email);
            jsonPostParameters.put("phone", phone);
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
