package com.soulkey.calltalent.api.storage;

import android.content.SharedPreferences;

import com.soulkey.calltalent.domain.entity.Avatar;

import javax.inject.Inject;

import rx.Observable;

/**
 *
 * Created by peng on 2016/6/28.
 */
public class AvatarDiskCache implements IDiskCache<Avatar> {
    private final String TAG = AvatarDiskCache.class.getSimpleName();
    private final String KEY_UID = "avatar-key-uid";
    private final String KEY_MEDIA_URI = "avatar-key-media-uri";
    private SharedPreferences prefs;

    @Inject
    public AvatarDiskCache(SharedPreferences prefs) {
        this.prefs = prefs;
    }


    @Override
    public Observable<Avatar> getEntity() {
        return Observable.defer(() -> {
            Observable<Avatar> result;
            String uid = prefs.getString(KEY_UID, "");
            String mediaUri = prefs.getString(KEY_MEDIA_URI, "");
            result = Observable.just(Avatar.create(uid, mediaUri));
            return result;
        });
    }

    @Override
    public Observable<Boolean> saveEntity(Avatar entity) {
        return Observable.defer(() -> {
            SharedPreferences.Editor editor = prefs.edit();
            if (entity.uid() != null) editor.putString(KEY_UID, entity.uid());
            if (entity.mediaUri() != null) editor.putString(KEY_MEDIA_URI, entity.mediaUri());
            return Observable.just(editor.commit());
        });
    }

    @Override
    public void clear() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_UID);
        editor.remove(KEY_MEDIA_URI);
        editor.apply();
    }
}
