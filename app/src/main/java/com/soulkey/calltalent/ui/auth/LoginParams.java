package com.soulkey.calltalent.ui.auth;

/**
 * The login parameters to pass between LoginActivity and RegisterActivity
 * Created by peng on 2016/6/5.
 */
@SuppressWarnings("ALL")
public enum LoginParams {
    PARAM_KEY_USERNAME("intent-params-key-username"),
    PARAM_KEY_PASSWORD("intent-params-key-password"),
    PARAM_KEY_UID("intent-params-key-uid"),
    PARAM_KEY_AVATAR_URI("intent-params-key-avatar_uri");

    private final String value;

    LoginParams(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
