package com.soulkey.calltalent.utils.rxpreferences;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

@SuppressWarnings("ALL")
final class StringAdapter implements Preference.Adapter<String> {
    static final StringAdapter INSTANCE = new StringAdapter();

    @Override
    public String get(@NonNull String key, @NonNull SharedPreferences preferences) {
        return preferences.getString(key, null);
    }

    @Override
    public void set(@NonNull String key, @NonNull String value,
                    @NonNull SharedPreferences.Editor editor) {
        editor.putString(key, value);
    }
}
