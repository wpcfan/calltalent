package com.soulkey.calltalent.api.image;

import android.app.Application;
import android.net.Uri;

import com.soulkey.calltalent.utils.memory.SystemUtil;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.File;

import rx.Observable;

/**
 * Created by wangpeng on 16/6/29.
 */
public class ImageHandler {
    private final Picasso picasso;

    public ImageHandler(Application application) {
        this.picasso = new Picasso.Builder(application)
                .memoryCache(new LruCache(SystemUtil.calculateMemoryCacheSize(application)))
                .build();
        Picasso.setSingletonInstance(this.picasso);
    }

    public Observable<RequestCreator> loadImageFrom(String uri) {
        return Observable.fromCallable(() -> this.picasso.load(uri).noFade().fit());
    }

    public Observable<RequestCreator> loadImageFrom(File file) {
        return Observable.fromCallable(() -> this.picasso.load(file).noFade().fit());
    }

    public Observable<RequestCreator> loadImageFrom(Uri uri) {
        return Observable.fromCallable(() -> this.picasso.load(uri).noFade().fit());
    }
}
