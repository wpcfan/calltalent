package com.soulkey.calltalent.ui;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.soulkey.calltalent.R;
import com.soulkey.calltalent.di.component.BaseActivityComponent;
import com.soulkey.calltalent.di.module.SplashModule;
import com.soulkey.calltalent.domain.model.SplashModel;
import com.soulkey.calltalent.service.SplashService;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import icepick.State;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Provide a splash screen for the app
 * Created by peng on 2016/5/24.
 */
public class SplashActivity extends BaseActivity {

    @Inject
    SplashModel splashModel;

    @BindView(R.id.splash_image)
    ImageView splashImage;

    @BindView(R.id.count_down)
    TextView countDown;

    @State
    String splash_image_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        Observable<Long> observableCountDown = getTimerStream();
        Subscription subscriptionCountDown = dealWithCountDown(observableCountDown);
        getSubsCollector().add(subscriptionCountDown);

        SplashService.startActionDownloadImage(SplashActivity.this, "splash.jpg");
    }

    private Subscription dealWithCountDown(Observable<Long> observableCountDown) {
        return observableCountDown
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(count -> countDown.setText(count.toString()),
                        err -> Toast.makeText(SplashActivity.this, "something wrong", Toast.LENGTH_SHORT).show(),
                        () -> {
                            UIHelper.launchActivity(SplashActivity.this, MainActivity.class);
                            finish();
                        });
    }

    private Observable<Long> getTimerStream() {
        return Observable.interval(1L, TimeUnit.SECONDS)
                .map(aLong -> 5L - aLong)
                .takeUntil(value -> value == 0)
                .publish()
                .refCount();
    }

    @Override
    protected void injectBaseActivityComponent(BaseActivityComponent component) {
        component.inject(this);
        component.plus(new SplashModule()).inject(this);
    }

    @Override
    protected Boolean requiredLoggedIn() {
        return false;
    }
}
