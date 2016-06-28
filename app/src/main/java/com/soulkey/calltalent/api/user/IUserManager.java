package com.soulkey.calltalent.api.user;

import com.soulkey.calltalent.domain.entity.UserProfile;

import rx.Observable;

/**
 * The interface that encapsulates the user related methods
 * Created by peng on 2016/6/11.
 */
public interface IUserManager {
    Observable<Void> saveUserProfile(UserProfile profile, String uid);

    Observable<UserProfile> getUserProfile(String uid);

    Observable<String> uploadAvatar(byte[] imageData, String uid);
}
