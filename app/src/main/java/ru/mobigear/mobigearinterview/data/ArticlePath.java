package ru.mobigear.mobigearinterview.data;

import android.content.ContentResolver;

/**
 * Created by eugene on 3/24/15.
 */
public class ArticlePath extends AbstractPath {
    public ArticlePath(DatabaseHelper helper, ContentResolver resolver) {
        super(helper, resolver);
    }

    @Override
    protected String getPath() {
        return DataContract.PATH_ARTICLE;
    }

    @Override
    protected String getTable() {
        return DataContract.Article.TABLE_NAME;
    }
}
