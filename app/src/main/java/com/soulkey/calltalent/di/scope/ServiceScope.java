package com.soulkey.calltalent.di.scope;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Created by wangpeng on 16/7/7.
 */
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface ServiceScope {
}
