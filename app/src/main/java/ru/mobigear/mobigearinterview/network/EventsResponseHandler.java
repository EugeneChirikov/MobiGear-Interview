package ru.mobigear.mobigearinterview.network;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.net.Uri;

import org.json.JSONObject;

import java.util.List;

import ru.mobigear.mobigearinterview.data.DataContract;
import ru.mobigear.mobigearinterview.models.Event;

/**
 * Created by eugene on 3/24/15.
 */
public class EventsResponseHandler implements ResponseHandler {
    @Override
    public void handle(Context context, JSONObject result) {
        EventsListParser parser = new EventsListParser();
        List<Event> events = parser.parseData(result);
        ContentResolver contentResolver = context.getContentResolver();
        Uri url = DataContract.getContentUri(DataContract.PATH_EVENT);
        for (Event event: events) {
            try {
                ContentValues values = new ContentValues();
                values.put(DataContract.Event.EVENT_ID, event.getId());
                values.put(DataContract.Event.TITLE, event.getTitle());
                values.put(DataContract.Event.BODY, event.getBody());
                values.put(DataContract.Event.IMAGE_URL, event.getImageUrl());
                values.put(DataContract.Event.DATE, event.getDate());
                values.put(DataContract.Event.IS_USER_REGISTERED, event.isUserRegistered() ? 1 : 0);
                contentResolver.insert(url, values);
            }
            catch (SQLException | IllegalArgumentException e) {

            }
        }
    }
}
