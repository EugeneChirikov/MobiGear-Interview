package ru.mobigear.mobigearinterview.data;

import android.content.ContentResolver;

/**
 * Created by eugene on 3/24/15.
 */
public class EventPath extends AbstractPath {
    public EventPath(DatabaseHelper helper, ContentResolver resolver) {
        super(helper, resolver);
    }

    @Override
    protected String getPath() {
        return DataContract.PATH_EVENT;
    }

    @Override
    protected String getTable() {
        return DataContract.Event.TABLE_NAME;
    }
}
