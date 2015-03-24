package ru.mobigear.mobigearinterview.models;

/**
 * Created by eugene on 3/25/15.
 */
public class Event implements Model {
    private long id = -1;
    private String title = "";
    private String body = "";
    private String imageUrl = "";
    private String date = "";
    private boolean isUserRegistered = false;

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

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setUserRegistered(boolean isUserRegistered) {
        this.isUserRegistered = isUserRegistered;
    }

    public boolean isUserRegistered() {
        return isUserRegistered;
    }

    @Override
    public boolean isInvalid() {
        return id < 0;
    }
}
