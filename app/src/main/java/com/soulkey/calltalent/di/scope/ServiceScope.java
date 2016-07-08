package com.soulkey.calltalent.di.scope;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

@SuppressWarnings("ALL")
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface ServiceScope {
}
