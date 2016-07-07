package com.soulkey.calltalent.domain.model;

import com.soulkey.calltalent.api.network.IHttpManager;
import com.soulkey.calltalent.utils.rx.SchedulerProvider;

import rx.Observable;

/**
 * Created by wangpeng on 16/7/6.
 */
public class SplashModel {
    private final SchedulerProvider schedulerProvider;
    private final IHttpManager httpManager;

    public SplashModel(SchedulerProvider schedulerProvider, IHttpManager httpManager) {
        this.schedulerProvider = schedulerProvider;
        this.httpManager = httpManager;
    }

    public Observable<String> getSplashImage(String url) {
        return httpManager.getSplashImageUrl(url).compose(schedulerProvider.applySchedulers());
    }
}
