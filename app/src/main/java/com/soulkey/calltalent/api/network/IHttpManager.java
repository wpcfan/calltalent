package com.soulkey.calltalent.api.network;

import android.graphics.Bitmap;

import okhttp3.Response;
import rx.Observable;

public interface IHttpManager {

    Observable<Bitmap> fetchImageByUrl(String url);

    Observable<Response> getHttpResponse(String url);
}
