package ru.mobigear.mobigearinterview.network;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ru.mobigear.mobigearinterview.models.Event;

/**
 * Created by eugene on 3/25/15.
 */
public class EventsListParser extends ResponseParser<List<Event>> {
    @Override
    public List<Event> parseData(JSONObject data) {
        List<Event> events = new ArrayList<>();
        JSONArray jsonArray = data.optJSONArray("events");
        if (jsonArray == null)
            return events;
        EventParser articleParser = new EventParser();
        for (int i = 0; i < jsonArray.length(); ++i) {
            JSONObject jsonEvent = jsonArray.optJSONObject(i);
            if (jsonEvent != null) {
                Event event = articleParser.parseData(jsonEvent);
                if (!event.isInvalid())
                    events.add(event);
            }
        }
        return events;
    }
}