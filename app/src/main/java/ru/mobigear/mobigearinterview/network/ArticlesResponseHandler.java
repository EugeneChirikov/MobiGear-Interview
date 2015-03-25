package ru.mobigear.mobigearinterview.network;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.net.Uri;

import org.json.JSONObject;

import java.util.List;

import ru.mobigear.mobigearinterview.data.DataContract;
import ru.mobigear.mobigearinterview.models.Article;

/**
 * Created by eugene on 3/24/15.
 */
public class ArticlesResponseHandler implements ResponseHandler {
    @Override
    public void handle(Context context, JSONObject result) {
        ArticlesListParser parser = new ArticlesListParser();
        List<Article> articles = parser.parseData(result);
        ContentResolver contentResolver = context.getContentResolver();
        Uri url = DataContract.getContentUri(DataContract.PATH_ARTICLE);
        for (Article a: articles) {
            try {
                ContentValues values = new ContentValues();
                values.put(DataContract.Article.ARTICLE_ID, a.getId());
                values.put(DataContract.Article.TITLE, a.getTitle());
                values.put(DataContract.Article.BODY, a.getBody());
                values.put(DataContract.Article.IMAGE_URL, a.getImageUrl());
                contentResolver.insert(url, values);
            }
            catch (SQLException | IllegalArgumentException e) {

            }
        }
    }
}
