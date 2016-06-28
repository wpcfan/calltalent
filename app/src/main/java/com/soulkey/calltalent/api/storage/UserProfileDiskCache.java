package com.soulkey.calltalent.api.storage;

import android.content.SharedPreferences;

import com.soulkey.calltalent.domain.entity.UserProfile;

import javax.inject.Inject;

import rx.Observable;

/**
 * Disk Cache for UserProfile
 * Created by peng on 2016/6/4.
 */
public class UserProfileDiskCache implements IDiskCache<UserProfile> {

    private final String KEY_UID = "userprofile-key-uid";
    private final String KEY_NAME = "userprofile-key-name";
    private final String KEY_TITLE = "userprofile-key-title";
    private final String KEY_AVATARURL = "userprofile-key-avatarUrl";
    private final String KEY_GENDER = "userprofile-key-gender";
    private final String KEY_DESC = "userprofile-key-desc";
    private final String KEY_TIMESTAMP = "userprofile-key-timestamp";
    private SharedPreferences prefs;

    @Inject
    public UserProfileDiskCache(SharedPreferences sharedPreferences) {
        this.prefs = sharedPreferences;
    }

    @Override
    public Observable<UserProfile> getEntity() {
        return Observable.defer(() -> {
            Observable<UserProfile> result;
            String uid = prefs.getString(KEY_UID, "");
            String name = prefs.getString(KEY_NAME, "");
            String title = prefs.getString(KEY_TITLE, "");
            String avatarUrl = prefs.getString(KEY_AVATARURL, "");
            boolean gender = prefs.getBoolean(KEY_GENDER, false);
            String desc = prefs.getString(KEY_DESC, "");
            long timestamp = prefs.getLong(KEY_TIMESTAMP, System.currentTimeMillis());
            result = Observable.just(UserProfile.create(
                    uid, name, title, avatarUrl, gender, desc, timestamp));
            return result;
        });
    }

    @Override
    public Observable<Boolean> saveEntity(UserProfile userProfile) {
        return Observable.defer(() -> {
            SharedPreferences.Editor editor = prefs.edit();
            if (userProfile.uid() != null) editor.putString(KEY_UID, userProfile.uid());
            if (userProfile.name() != null) editor.putString(KEY_NAME, userProfile.name());
            if (userProfile.title() != null) editor.putString(KEY_TITLE, userProfile.title());
            if (userProfile.avatarUrl() != null)
                editor.putString(KEY_AVATARURL, userProfile.avatarUrl());
            editor.putBoolean(KEY_GENDER, userProfile.gender());
            if (userProfile.desc() != null) editor.putString(KEY_DESC, userProfile.desc());
            editor.putLong(KEY_TIMESTAMP, userProfile.timestamp());
            return Observable.just(editor.commit());
        });
    }

    @Override
    public void clear() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_UID);
        editor.remove(KEY_NAME);
        editor.remove(KEY_TITLE);
        editor.remove(KEY_AVATARURL);
        editor.remove(KEY_GENDER);
        editor.remove(KEY_DESC);
        editor.remove(KEY_TIMESTAMP);
        editor.apply();
    }
}
