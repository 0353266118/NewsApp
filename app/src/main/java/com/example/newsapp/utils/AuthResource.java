// File: utils/AuthResource.java
package com.example.newsapp.utils;

// Đặt là public để các package khác có thể truy cập
public class AuthResource<T> {
    public enum AuthStatus { SUCCESS, ERROR, LOADING }

    public final AuthStatus status;
    public final T data;
    public final String message;

    private AuthResource(AuthStatus status, T data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> AuthResource<T> success(T data) {
        return new AuthResource<>(AuthStatus.SUCCESS, data, null);
    }

    public static <T> AuthResource<T> error(String msg) {
        return new AuthResource<>(AuthStatus.ERROR, null, msg);
    }

    public static <T> AuthResource<T> loading() {
        return new AuthResource<>(AuthStatus.LOADING, null, null);
    }
}