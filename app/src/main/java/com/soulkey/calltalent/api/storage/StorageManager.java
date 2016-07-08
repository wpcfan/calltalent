package com.soulkey.calltalent.api.storage;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

/**
 *
 * Created by peng on 2016/7/3.
 */
@SuppressWarnings("ALL")
public final class StorageManager implements IStorageManager {
    private final SharedPreferences prefs;

    public StorageManager(SharedPreferences prefs) {
        this.prefs = prefs;
    }

    @Override
    public Boolean writeString(@NonNull String key, @NonNull String value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    @Override
    public Boolean writeBoolean(@NonNull String key, @NonNull Boolean value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    @Override
    public String readString(@NonNull String key) {
        return prefs.getString(key, "");
    }

    @Override
    public Boolean readBoolean(@NonNull String key) {
        return prefs.getBoolean(key, true);
    }

    @Override
    public void remove(@NonNull String key) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(key);
        editor.apply();
    }
}
