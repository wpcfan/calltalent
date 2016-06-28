package com.soulkey.calltalent.domain.entity;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;

import solid.collections.SolidList;

import static solid.collectors.ToSolidList.toSolidList;
import static solid.stream.Primitives.box;

/**
 * Created by peng on 2016/6/28.
 */
@AutoValue
public abstract class Avatar implements Parcelable {
    private final String TAG = Avatar.class.getSimpleName();

    //Please note that the order the params in the constructor
    // must be the same as the definition order in the class
    public static Avatar create(final String uid, final byte[] avatarData) {
        return new AutoValue_Avatar(uid, box(avatarData).collect(toSolidList()));
    }

    public abstract String uid();

    public abstract SolidList<Byte> avatarData();
}
