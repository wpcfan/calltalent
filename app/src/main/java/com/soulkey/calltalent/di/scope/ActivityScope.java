package com.soulkey.calltalent.di.scope;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Custom Scope for Dagger2
 * Created by peng on 2016/5/31.
 */
@SuppressWarnings("ALL")
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface ActivityScope {
}
