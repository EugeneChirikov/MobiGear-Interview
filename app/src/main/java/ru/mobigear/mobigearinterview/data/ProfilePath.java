package ru.mobigear.mobigearinterview.data;

import android.content.ContentResolver;

/**
 * Created by eugene on 3/24/15.
 */
public class ProfilePath extends AbstractPath {
    public ProfilePath(DatabaseHelper helper, ContentResolver resolver) {
        super(helper, resolver);
    }

    @Override
    protected String getPath() {
        return DataContract.PATH_PROFILE;
    }

    @Override
    protected String getTable() {
        return DataContract.Profile.TABLE_NAME;
    }
}
