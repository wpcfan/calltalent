package com.soulkey.calltalent.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.jakewharton.rxbinding.view.RxView;
import com.soulkey.calltalent.R;
import com.soulkey.calltalent.di.component.ApplicationComponent;
import com.soulkey.calltalent.ui.auth.LoginActivity;

import rx.Subscription;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button signoutBtn = (Button) findViewById(R.id.SignoutBtn);
        assert signoutBtn != null;
        getSubsCollector().add(signOut(signoutBtn));
    }

    private Subscription signOut(View signoutBtn) {
        return RxView.clicks(signoutBtn)
                .flatMap(__ -> userModel.signOut())
                .compose(bindToLifecycle())
                .subscribe(aVoid -> {
                    userModel.clearMemoryAndDiskCache();
                    UIHelper.launchActivity(MainActivity.this, LoginActivity.class);
                    finish();
                });
    }


    @Override
    protected void injectComponent(ApplicationComponent component) {
        component.inject(this);
    }

    @Override
    protected Boolean requiredLoggedIn() {
        return true;
    }
}
