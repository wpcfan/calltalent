package com.soulkey.calltalent.api.user.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.soulkey.calltalent.api.user.IUserManager;
import com.soulkey.calltalent.api.wrapper.RxFirebase;
import com.soulkey.calltalent.domain.entity.UserProfile;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;

public class UserManagerFirebaseImpl implements IUserManager {
    private final String TAG = UserManagerFirebaseImpl.class.getSimpleName();

    private FirebaseDatabase database;

    public UserManagerFirebaseImpl() {
        this.database = FirebaseDatabase.getInstance();
    }

    @Override
    public Observable<Void> saveUserProfile(UserProfile profile, String uid) {
        Map<String, UserProfile> userProfileMap = new HashMap<>();
        userProfileMap.put(uid, profile);
        DatabaseReference myRef = database.getReference("profiles");
        return RxFirebase.setValue(myRef, userProfileMap);
    }

    @Override
    public Observable<UserProfile> getUserProfile(String uid) {
        return null;
    }

    @Override
    public Observable<String> uploadAvatar(byte[] imageData, String uid) {
        return null;
    }
}
