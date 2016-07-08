package com.soulkey.calltalent.domain.entity;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;

/**
 * User model
 * Created by peng on 2016/5/25.
 */
@SuppressWarnings("ALL")
@AutoValue
public abstract class User implements Parcelable, IUser {

    //Please note that the order the params in the constructor
    // must be the same as the definition order in the class
    public static User create(String uid, boolean isAnonymous) {
        return new AutoValue_User(uid, isAnonymous);
    }

    public abstract String uid();

    public abstract boolean isAnonymous();

}
