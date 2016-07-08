package com.soulkey.calltalent.exception;

/**
 * Custom Exception for Authentication
 * Created by peng on 2016/5/28.
 */
@SuppressWarnings("ALL")
public class CustomException extends Exception {

    public enum ErrorCode {
        AUTHENTICATION_DISABLED,
        ID_TAKEN,
        INVALID_ARGUMENTS,
        INVALID_CREDENTIALS,
        INVALID_ID,
        //        PROVIDER_ERROR,
        UNKNOWN_ERROR
    }
}
