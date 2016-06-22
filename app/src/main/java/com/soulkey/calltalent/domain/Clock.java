package com.soulkey.calltalent.domain;

/**
 * The Clock interface
 * Created by peng on 2016/6/4.
 */
public interface Clock {
    Clock REAL = System::currentTimeMillis;

    long millis();
}
