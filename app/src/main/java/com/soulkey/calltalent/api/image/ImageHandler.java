package com.soulkey.calltalent.api.image;

import android.app.Application;
import android.net.Uri;
import android.widget.ImageView;

import com.soulkey.calltalent.utils.memory.SystemUtil;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import rx.Observable;

/**
 *
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

    public Observable<Void> loadImage(Uri uri, ImageView imageView) {
        return Observable.fromCallable(() -> {
            picasso.load(uri).noFade().fit().into(imageView);
            return null;
        });
    }

    public Observable<Void> loadImage(
            Uri uri, ImageView imageView, int targetWidthRes, int targetHeightRes) {
        return Observable.fromCallable(() -> {
            picasso.load(uri).noFade().resizeDimen(targetWidthRes, targetHeightRes).into(imageView);
            return null;
        });
    }
}
