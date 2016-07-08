package com.soulkey.calltalent.di.component;

import com.soulkey.calltalent.di.module.SplashModule;
import com.soulkey.calltalent.ui.SplashActivity;

import dagger.Subcomponent;

@Subcomponent(modules = SplashModule.class)
public interface SplashComponent {
    void inject(SplashActivity activity);
}
