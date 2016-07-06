package com.soulkey.calltalent.api.network.processor;

import com.soulkey.calltalent.domain.data.BingBackground;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;

import okhttp3.Response;
import rx.exceptions.Exceptions;

/**
 * Created by wangpeng on 16/7/6.
 */
public class BingImageProcessor {
    public static String getImageUri(Response response) {
        try {
            final String body = response.body().string();
            Moshi moshi = new Moshi.Builder().build();
            JsonAdapter<BingBackground> jsonAdapter = moshi.adapter(BingBackground.class);
            BingBackground background = jsonAdapter.fromJson(body);
            return background.images.get(0).url;
        } catch (IOException e) {
            throw Exceptions.propagate(e);
        }
    }
}
