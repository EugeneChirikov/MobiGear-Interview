package ru.mobigear.mobigearinterview.models;

/**
 * Created by eugene on 3/25/15.
 */
public class Profile implements Model {
    private String fio = "";
    private String email = "";
    private String phone = "";
    private String avatarUrl = "";

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public String getFio() {
        return fio;
    }

    @Override
    public boolean isInvalid() {
        return email.isEmpty();
    }
}
