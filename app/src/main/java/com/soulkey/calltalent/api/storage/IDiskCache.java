package com.soulkey.calltalent.api.storage;

import rx.Observable;

public interface IDiskCache<T> {
    Observable<T> getEntity();

    Observable<Boolean> saveEntity(T entity);

    void clear();
}
