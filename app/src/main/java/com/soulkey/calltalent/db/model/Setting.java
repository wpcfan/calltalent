package com.soulkey.calltalent.db.model;

import com.google.auto.value.AutoValue;
import com.squareup.sqldelight.RowMapper;

@AutoValue
public abstract class Setting implements SettingModel {
    public enum PARAM {
        DOWNLOADED_SPLASH_URI("downloaded_splash_uri"),
        SPLASH_REMOTE_URI("splash_remote_uri");
        private String value;

        PARAM(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
    public static final Factory<Setting> FACTORY = new Factory<>(AutoValue_Setting::new);
    public static final RowMapper<Setting> ALL_MAPPER = FACTORY.select_allMapper();
    public static final RowMapper<Setting> NAME_MAPPER = FACTORY.select_by_setting_nameMapper();
}
