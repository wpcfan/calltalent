package com.soulkey.calltalent.di.component;

import com.soulkey.calltalent.di.module.DomainModule;
import com.soulkey.calltalent.di.module.SplashModule;
import com.soulkey.calltalent.di.scope.ActivityScope;
import com.soulkey.calltalent.ui.BaseActivity;

import dagger.Component;

@ActivityScope
@Component(
        dependencies = {
                ApplicationComponent.class
        },
        modules = {
                DomainModule.class
        })
public interface BaseActivityComponent {
    void inject(BaseActivity activity);
    SplashComponent plus(SplashModule splashModule);
}
