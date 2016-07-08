package com.soulkey.calltalent.utils.rx;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public interface ISchedulerProvider {
    ISchedulerProvider DEFAULT = new ISchedulerProvider() {
        @Override
        public <T> Observable.Transformer<T, T> applySchedulers() {
            return observable -> observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };

    <T> Observable.Transformer<T, T> applySchedulers();
}
