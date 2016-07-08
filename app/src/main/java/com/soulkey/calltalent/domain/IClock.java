package com.soulkey.calltalent.domain;

/**
 * The IClock interface
 * Created by peng on 2016/6/4.
 */
public interface IClock {
    IClock REAL = System::currentTimeMillis;

    long millis();
}
