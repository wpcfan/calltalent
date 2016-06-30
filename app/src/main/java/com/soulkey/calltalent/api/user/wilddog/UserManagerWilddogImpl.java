package com.soulkey.calltalent.api.user.wilddog;

import android.app.Application;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.soulkey.calltalent.api.user.IUserManager;
import com.soulkey.calltalent.api.wrapper.RxWilddog;
import com.soulkey.calltalent.domain.entity.UserProfile;
import com.wilddog.client.Wilddog;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * User related API service call encapsulated in here
 * Created by peng on 2016/6/11.
 */
public class UserManagerWilddogImpl implements IUserManager {

    private final Wilddog wilddog;
    private final Cloudinary cloudinary;
    private final String Profiles_Path = "userprofiles";

    public UserManagerWilddogImpl(Application application) {
        Wilddog.setAndroidContext(application);
        String AUTH_URL = "https://calltalent.wilddogio.com/";
        this.wilddog = new Wilddog(AUTH_URL);
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "twigcodes");
        config.put("api_key", "929157142385211");
        config.put("api_secret", "Uq_ar0Une3IsUViBB6UFOh0nwio");
        this.cloudinary = new Cloudinary(config);
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

    @Override
    public Observable<String> uploadAvatar(byte[] imageData, String uid) {
        return Observable.fromCallable(() -> {
            cloudinary.uploader().upload(imageData, ObjectUtils.emptyMap());
            return cloudinary.url().generate(uid);
        }).subscribeOn(Schedulers.computation());
    }
}
