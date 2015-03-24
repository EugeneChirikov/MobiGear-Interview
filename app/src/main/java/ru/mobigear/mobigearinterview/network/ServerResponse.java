package ru.mobigear.mobigearinterview.network;

/**
 * Created by eugene on 3/23/15.
 */
public class ServerResponse<T> {
    private int code;
    private T data;

    public void setCode(int code) {
        this.code = code;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public T getData() {
        return data;
    }

    public boolean isError() {
        return code != 200;
    }
}
