package com.soulkey.calltalent.api.auth;

import com.soulkey.calltalent.exception.CustomException;

/**
 * Helpers to make authentication easier
 * Created by peng on 2016/5/31.
 */
@SuppressWarnings("ALL")
final class AuthHelpers {
    public static CustomException.ErrorCode mapWildDogAuthErrToOurs(int wilddogErrCode) {
        switch (wilddogErrCode) {
            case -12:
                return CustomException.ErrorCode.AUTHENTICATION_DISABLED;
            case -15:
                return CustomException.ErrorCode.INVALID_ID;
            case -16:
                return CustomException.ErrorCode.INVALID_CREDENTIALS;
            case -18:
                return CustomException.ErrorCode.ID_TAKEN;
            case -21:
                return CustomException.ErrorCode.INVALID_ARGUMENTS;
            default:
                return CustomException.ErrorCode.UNKNOWN_ERROR;
        }
    }
}
