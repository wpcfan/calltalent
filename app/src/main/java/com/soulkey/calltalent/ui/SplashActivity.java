package com.soulkey.calltalent.ui;

import android.os.Bundle;

import com.soulkey.calltalent.di.component.ApplicationComponent;

/**
 * Provide a splash screen for the app
 * Created by peng on 2016/5/24.
 */
public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UIHelper.launchActivity(SplashActivity.this, MainActivity.class);
        finish();
    }

    @Override
    protected void injectComponent(ApplicationComponent component) {
        component.inject(this);
    }

    @Override
    protected Boolean requiredLoggedIn() {
        return false;
    }
}
