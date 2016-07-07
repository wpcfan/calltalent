package com.soulkey.calltalent.api.auth;

/**
 * Auth result interface
 * Created by peng on 2016/6/12.
 */
public interface IAuthResult<T> {
    String getReason();
    T getData();
    boolean isSuccessful();

    void setData(T t);

    void setSuccessful(boolean isSuccess);

    void setReason(String reason);
}
