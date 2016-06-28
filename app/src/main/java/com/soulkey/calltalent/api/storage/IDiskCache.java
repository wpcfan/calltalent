package com.soulkey.calltalent.api.storage;

import rx.Observable;

/**
 * Created by peng on 2016/6/28.
 */
public interface IDiskCache<T> {
    Observable<T> getEntity();

    Observable<Boolean> saveEntity(T entity);

    void clear();
}
