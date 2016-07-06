package com.soulkey.calltalent.utils.rx;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 *
 * Created by peng on 2016/6/3.
 */
public interface SchedulerProvider {
    SchedulerProvider DEFAULT = new SchedulerProvider() {
        @Override
        public <T> Observable.Transformer<T, T> applySchedulers() {
            return observable -> observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };

    <T> Observable.Transformer<T, T> applySchedulers();
}
