package com.soulkey.calltalent.domain.entity;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;

/**
 *
 * Created by peng on 2016/6/28.
 */
@SuppressWarnings("ALL")
@AutoValue
public abstract class Avatar implements Parcelable {
    //Please note that the order of params defined in the constructor
    // must be the same as the definition order in the class
    public static Avatar create(final String uid, String mediaUri) {
        return new AutoValue_Avatar(uid, mediaUri);
    }

    public abstract String uid();

    public abstract String mediaUri();
}
