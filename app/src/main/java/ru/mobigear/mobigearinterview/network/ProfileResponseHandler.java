package ru.mobigear.mobigearinterview.network;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.net.Uri;

import org.json.JSONObject;

import ru.mobigear.mobigearinterview.data.DataContract;
import ru.mobigear.mobigearinterview.models.Profile;

/**
 * Created by eugene on 3/24/15.
 */
public class ProfileResponseHandler implements ResponseHandler {
    @Override
    public void handle(Context context, JSONObject result) {
        ProfileParser parser = new ProfileParser();
        Profile profile = parser.parseData(result);
        if (profile.isInvalid())
            return;
        ContentResolver contentResolver = context.getContentResolver();
        Uri url = DataContract.getContentUri(DataContract.PATH_PROFILE);
        try {
            contentResolver.delete(url, null, null);
            ContentValues values = new ContentValues();
            values.put(DataContract.Profile.FIO, profile.getFio());
            values.put(DataContract.Profile.EMAIL, profile.getEmail());
            values.put(DataContract.Profile.PHONE, profile.getPhone());
            values.put(DataContract.Profile.AVATAR_URL, profile.getAvatarUrl());
            Uri returnUri = contentResolver.insert(url, values);
            long rowId = Long.valueOf(DataContract.getRowIdFromUri(returnUri));
        }
        catch (SQLException | IllegalArgumentException e) {

        }

    }
}
