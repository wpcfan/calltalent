package com.soulkey.calltalent.domain.model;

import com.soulkey.calltalent.api.network.IHttpManager;
import com.soulkey.calltalent.utils.rx.ISchedulerProvider;

import rx.Observable;

@SuppressWarnings("ALL")
public final class SplashModel {
    private final ISchedulerProvider schedulerProvider;
    private final IHttpManager httpManager;

    public SplashModel(ISchedulerProvider schedulerProvider, IHttpManager httpManager) {
        this.schedulerProvider = schedulerProvider;
        this.httpManager = httpManager;
    }

    public Observable<String> getSplashImage(String url) {
        return httpManager.getSplashImageUrl(url).compose(schedulerProvider.applySchedulers());
    }
}
