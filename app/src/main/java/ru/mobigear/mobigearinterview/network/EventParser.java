package ru.mobigear.mobigearinterview.network;

import org.json.JSONObject;

import ru.mobigear.mobigearinterview.models.Event;

/**
 * Created by eugene on 3/25/15.
 */
public class EventParser extends ResponseParser<Event> {
    @Override
    public Event parseData(JSONObject data) {
        Event event = new Event();
        long id = data.optLong("id", -1);
        if (id >= 0)
            event.setId(id);
        String title = data.optString("title");
        if (!title.isEmpty())
            event.setTitle(title);
        String body = data.optString("body");
        if (!body.isEmpty())
            event.setBody(body);
        String imageUrl = data.optString("image");
        if (!imageUrl.isEmpty())
            event.setImageUrl(imageUrl);
        String date = data.optString("date");
        if (!date.isEmpty())
            event.setDate(date);
        return event;
    }
}
