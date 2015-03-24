package ru.mobigear.mobigearinterview.network;

import org.json.JSONObject;

import ru.mobigear.mobigearinterview.models.Profile;

/**
 * Created by eugene on 3/25/15.
 */
public class ProfileParser extends ResponseParser<Profile> {
    @Override
    public Profile parseData(JSONObject data) {
        Profile profile = new Profile();
        JSONObject user = data.optJSONObject("user");
        if (user == null)
            return profile;
        String fio = user.optString("fio");
        if (!fio.isEmpty())
            profile.setFio(fio);
        String email = user.optString("email");
        if (!email.isEmpty())
            profile.setEmail(email);
        String phone = user.optString("phone");
        if (!phone.isEmpty())
            profile.setPhone(phone);
        String avatarUrl = user.optString("avatar");
        if (!avatarUrl.isEmpty())
            profile.setAvatarUrl(avatarUrl);
        return profile;
    }
}