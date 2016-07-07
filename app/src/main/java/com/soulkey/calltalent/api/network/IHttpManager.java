package com.soulkey.calltalent.api.network;

import android.graphics.Bitmap;

import rx.Observable;

public interface IHttpManager {
    Observable<String> getSplashImageUrl(String url);

    Observable<Bitmap> fetchImageByUrl(String url);
}
