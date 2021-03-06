package com.soulkey.calltalent.api.network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;

public final class HttpManager implements IHttpManager {
    private final OkHttpClient client;

    public HttpManager() {
        client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor()).build();
    }

    @Override
    public Observable<Bitmap> fetchImageByUrl(String url) {
        return getHttpResponse(url).map(response -> response.body().byteStream()).map(BitmapFactory::decodeStream);
    }

    @Override
    public Observable<Response> getHttpResponse(String url) {
        return Observable.fromCallable(() -> client.newCall(new Request.Builder().url(url).build()).execute());
    }
}
