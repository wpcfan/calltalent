package com.soulkey.calltalent.ui;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.soulkey.calltalent.App;
import com.soulkey.calltalent.di.component.ApplicationComponent;
import com.soulkey.calltalent.domain.entity.User;
import com.soulkey.calltalent.domain.model.UserModel;
import com.soulkey.calltalent.ui.auth.LoginActivity;
import com.soulkey.calltalent.utils.animation.AnimationUtil;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.util.Map;

import javax.inject.Inject;

import icepick.Icepick;
import rx.Observable;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * The base Activity for all the activities
 * Created by peng on 2016/5/23.
 */
public abstract class BaseActivity extends RxAppCompatActivity {
    private final String TAG = BaseActivity.class.getSimpleName();
    /**
     * The subscription to hold all subscriptions and will be cleared when Activity is destroyed
     * Every subscription MUST be added to it to avoid MEMORY LEAK
     */
    private final CompositeSubscription _subscription = new CompositeSubscription();
    @Inject
    protected UserModel userModel;

    protected long getDebounceTime() {
        return 400L;
    }

    protected int getThrottleCount() {
        return 3;
    }

    protected CompositeSubscription getSubsCollector() {
        return _subscription;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationComponent component = App.from(this).getAppComponent();
        injectComponent(component);
        _subscription.add(accessDeniedFallback());
        AnimationUtil.setupWindowAnimations(getWindow());
        Icepick.restoreInstanceState(this, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    /**
     * When Activity is destroyed, clear all the subscriptions inside the CompositeSubscription
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (_subscription.hasSubscriptions())
            _subscription.clear();
        if (!_subscription.isUnsubscribed())
            _subscription.unsubscribe();
        userModel = null;
    }

    /**
     * Helper method to launch an Activity
     *
     * @param clazz the Activity class to be navigated to.
     */
    protected void launchActivity(Class<?> clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
        finish();
    }

    protected void launchActivityWithTransition(
            Class<?> clazz, View view, String transitionName, final Map<String, String> params) {
        Intent intent = new Intent(this, clazz);
        for (Map.Entry<String, String> entry :
                params.entrySet()) {
            intent.putExtra(entry.getKey(), entry.getValue());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                    this, view, transitionName);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }

    /**
     * Helper method to launch an Activity with parameters
     *
     * @param clazz  the Activity class to be navigated to
     * @param params the parameters to be received
     */
    protected void launchActivity(final Class<?> clazz, final Map<String, String> params) {
        Intent intent = new Intent(this, clazz);
        for (Map.Entry<String, String> entry :
                params.entrySet()) {
            intent.putExtra(entry.getKey(), entry.getValue());
        }
        startActivity(intent);
        finish();
    }

    /**
     * Define the logic when the access is denied
     *
     * @return the subscription that observes the changes
     */
    private Subscription accessDeniedFallback() {
        return userModel.loginStatus()
                .filter(user ->
                        requiredLoggedIn() &&
                                (user == null || user.isAnonymous()))
                .compose(bindToLifecycle())
                .subscribe(
                        result ->
                                launchActivity(LoginActivity.class),
                        err -> {
                            Log.d("accessDeniedFallback: ", err.getMessage());
                        }
                );
    }

    /**
     * Abstract method to inject the dependency powered by Dagger2
     * All children classes need to implement
     *
     * @param component the component to be injected
     */
    protected abstract void injectComponent(ApplicationComponent component);

    /**
     * Abstract method to determine if the current Activity requires users to be logged in to access
     *
     * @return true if login is required, false otherwise
     */
    protected abstract Boolean requiredLoggedIn();

    /**
     * Helper method to log user in, the method add some exception handling mechanism
     *
     * @param username the username
     * @param password the password
     * @return the User if logged in successfully, null otherwise
     */
    protected Observable<User> login(final String username, final String password) {
        return userModel.login(
                username,
                password)
                .doOnError(throwable ->
                        Toast.makeText(
                                this,
                                throwable.getMessage(),
                                Toast.LENGTH_SHORT)
                                .show())
                .onErrorResumeNext(throwable -> Observable.just(null))
                .compose(bindToLifecycle());
    }

    /**
     * Helper function to receive the parameters between activities
     *
     * @param key the key to the parameter
     * @return the value of the parameter
     */
    protected String receiveParams(@NonNull final String key) {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
            return bundle.getString(key);
        return null;
    }
}
