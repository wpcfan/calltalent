package com.soulkey.calltalent.api.storage;

import android.content.SharedPreferences;

import com.soulkey.calltalent.domain.entity.Avatar;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by peng on 2016/6/28.
 */
public class AvatarDiskCache implements IDiskCache<Avatar> {
    private final String TAG = AvatarDiskCache.class.getSimpleName();
    private final String KEY_UID = "avatar-key-uid";
    private final String KEY_AVATAR_DATA = "avatar-key-avatar_data";
    private SharedPreferences prefs;

    @Inject
    public AvatarDiskCache(SharedPreferences prefs) {
        this.prefs = prefs;
    }


    @Override
    public Observable<Avatar> getEntity() {
        return null;
    }

    @Override
    public Observable<Boolean> saveEntity(Avatar entity) {
        return null;
    }

    @Override
    public void clear() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_UID);
        editor.remove(KEY_AVATAR_DATA);
        editor.apply();
    }
}
