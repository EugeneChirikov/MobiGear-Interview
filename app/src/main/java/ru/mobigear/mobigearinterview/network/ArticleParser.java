package ru.mobigear.mobigearinterview.network;

import org.json.JSONObject;

import ru.mobigear.mobigearinterview.models.Article;

/**
 * Created by eugene on 3/25/15.
 */
public class ArticleParser extends ResponseParser<Article> {
    @Override
    public Article parseData(JSONObject data) {
        Article article = new Article();
        long id = data.optLong("id", -1);
        if (id >= 0)
            article.setId(id);
        String title = data.optString("title");
        if (!title.isEmpty())
            article.setTitle(title);
        String body = data.optString("body");
        if (!body.isEmpty())
            article.setBody(body);
        String imageUrl = data.optString("image");
        if (!imageUrl.isEmpty())
            article.setImageUrl(imageUrl);
        return article;
    }
}