package com.soulkey.calltalent.db;

import rx.Observable;

public interface ISettingDao {
    Observable<String> getSettingValueByName(String settingName);

    boolean updateSetting(String name, String value);

    boolean createSetting(String name, String value);
}
