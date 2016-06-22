package com.soulkey.calltalent.api.auth;

import android.support.annotation.Nullable;

/**
 * Store the result the authentication
 * Created by peng on 2016/6/12.
 */
public class AuthResult<T> implements IAuthResult<T> {

    private final boolean success;
    private String reason;
    private T data;

    private AuthResult(boolean success, @Nullable String reason, @Nullable T data) {
        this.success = success;
        if (reason != null)
            this.reason = reason;
        if (data != null)
            this.data = data;
    }

    public static <T> AuthResult<T> success(T t) {
        return new AuthResult<>(true, null, t);
    }

    public static <T> AuthResult<T> failure(String reason, T data) {
        return new AuthResult<>(false, reason, data);
    }

    @Override
    public String getReason() {
        return this.reason;
    }

    @Override
    public T getData() {
        return this.data;
    }

    @Override
    public boolean isSuccessful() {
        return this.success;
    }
}
