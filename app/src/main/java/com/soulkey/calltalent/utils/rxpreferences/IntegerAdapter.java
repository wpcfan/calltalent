package com.soulkey.calltalent.utils.rxpreferences;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

@SuppressWarnings("ALL")
final class IntegerAdapter implements Preference.Adapter<Integer> {
    static final IntegerAdapter INSTANCE = new IntegerAdapter();

    @Override
    public Integer get(@NonNull String key, @NonNull SharedPreferences preferences) {
        return preferences.getInt(key, 0);
    }

    @Override
    public void set(@NonNull String key, @NonNull Integer value,
                    @NonNull SharedPreferences.Editor editor) {
        editor.putInt(key, value);
    }
}
