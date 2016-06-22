package com.soulkey.calltalent.api.user.wilddog;

import android.app.Application;

import com.soulkey.calltalent.api.user.IUserManager;
import com.soulkey.calltalent.api.wrapper.RxWilddog;
import com.soulkey.calltalent.domain.entity.UserProfile;
import com.wilddog.client.Wilddog;

import rx.Observable;

/**
 * User related API service call encapsulated in here
 * Created by peng on 2016/6/11.
 */
public class UserManagerWilddogImpl implements IUserManager {

    private final Wilddog wilddog;
    private final String Profiles_Path = "userprofiles";

    public UserManagerWilddogImpl(Application application) {
        Wilddog.setAndroidContext(application);
        String AUTH_URL = "https://calltalent.wilddogio.com/";
        this.wilddog = new Wilddog(AUTH_URL);
    }

    @Override
    public Observable<Void> saveUserProfile(UserProfile profile, String uid) {
        Wilddog userProfileRef = wilddog.child(Profiles_Path).child(uid);
        return RxWilddog.setValue(userProfileRef, profile);
    }

    @Override
    public Observable<UserProfile> getUserProfile(String uid) {
        Wilddog userProfileRef = wilddog.child(Profiles_Path);
        return RxWilddog.fetchByPath(userProfileRef, uid).map(dataSnapshot -> {
            if (dataSnapshot.getValue() != null) {
                return ((UserProfile) dataSnapshot.getValue(UserProfile.class));
            }
            return null;
        });
    }
}
