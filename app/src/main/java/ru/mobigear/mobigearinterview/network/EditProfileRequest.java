package ru.mobigear.mobigearinterview.network;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by eugene on 3/24/15.
 */
public class EditProfileRequest extends AbstractRequest {
    private String fio;
    private String email;
    private String phone;
    private String avatar;

    public void setFio(String fio) {
        this.fio = fio;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    protected void init() {

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
            if (fio != null)
                jsonPostParameters.put("fio", fio);
            if (email != null)
                jsonPostParameters.put("email", email);
            if (phone != null)
                jsonPostParameters.put("phone", phone);
            if (avatar != null)
                jsonPostParameters.put("avatar", avatar);
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
