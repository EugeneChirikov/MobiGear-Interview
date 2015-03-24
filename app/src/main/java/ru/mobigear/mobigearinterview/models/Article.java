package ru.mobigear.mobigearinterview.models;

/**
 * Created by eugene on 3/25/15.
 */
public class Article implements Model {
    private long id = -1;
    private String title = "";
    private String body = "";
    private String imageUrl = "";

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public boolean isInvalid() {
        return id < 0;
    }
}
