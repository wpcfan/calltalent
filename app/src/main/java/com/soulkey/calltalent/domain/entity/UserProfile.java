package com.soulkey.calltalent.domain.entity;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.shaded.fasterxml.jackson.annotation.JsonCreator;
import com.shaded.fasterxml.jackson.annotation.JsonProperty;

/**
 * The user profile including name, gender, avatar etc.
 * Created by peng on 2016/6/8.
 */
@AutoValue
public abstract class UserProfile implements Parcelable {

    @JsonCreator
    public static UserProfile create(
            @JsonProperty("uid") String uid,
            @JsonProperty("name") String name,
            @JsonProperty("title") String title,
            @JsonProperty("avatarUrl") String avatarUrl,
            @JsonProperty("gender") boolean gender,
            @JsonProperty("desc") String desc) {
        return new AutoValue_UserProfile(uid, name, title, avatarUrl, gender, desc, System.currentTimeMillis());
    }

    @JsonCreator
    public static UserProfile create(
            @JsonProperty("uid") String uid,
            @JsonProperty("name") String name,
            @JsonProperty("title") String title,
            @JsonProperty("avatarUrl") String avatarUrl,
            @JsonProperty("gender") boolean gender,
            @JsonProperty("desc") String desc,
            @JsonProperty("timestamp") long timestamp) {
        return new AutoValue_UserProfile(uid, name, title, avatarUrl, gender, desc, timestamp);
    }

    @JsonProperty("uid")
    public abstract String uid();

    @JsonProperty("name")
    public abstract String name();

    @JsonProperty("title")
    public abstract String title();

    @JsonProperty("avatarUrl")
    public abstract String avatarUrl();

    @JsonProperty("gender")
    public abstract boolean gender();

    @JsonProperty("desc")
    public abstract String desc();

    @JsonProperty("timestamp")
    public abstract long timestamp();
}
