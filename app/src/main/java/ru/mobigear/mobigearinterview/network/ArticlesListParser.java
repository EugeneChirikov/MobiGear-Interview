package ru.mobigear.mobigearinterview.network;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ru.mobigear.mobigearinterview.models.Article;

/**
 * Created by eugene on 3/25/15.
 */
public class ArticlesListParser extends ResponseParser<List<Article>> {
    @Override
    public List<Article> parseData(JSONObject data) {
        List<Article> articles = new ArrayList<>();
        JSONArray jsonArray = data.optJSONArray("articles");
        if (jsonArray == null)
            return articles;
        ArticleParser articleParser = new ArticleParser();
        for (int i = 0; i < jsonArray.length(); ++i) {
            JSONObject jsonArticle = jsonArray.optJSONObject(i);
            if (jsonArticle != null) {
                Article article = articleParser.parseData(jsonArticle);
                if (!article.isInvalid())
                    articles.add(article);
            }
        }
        return articles;
    }
}
