package com.soulkey.calltalent.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.soulkey.calltalent.App;
import com.soulkey.calltalent.api.network.INetworkService;
import com.soulkey.calltalent.api.storage.IStorageService;
import com.soulkey.calltalent.di.component.BaseActivityComponent;
import com.soulkey.calltalent.di.component.DaggerBaseActivityComponent;
import com.soulkey.calltalent.di.module.NetworkModule;
import com.soulkey.calltalent.di.module.UserModule;
import com.soulkey.calltalent.domain.entity.User;
import com.soulkey.calltalent.domain.model.UserModel;
import com.soulkey.calltalent.ui.auth.LoginActivity;
import com.soulkey.calltalent.utils.animation.AnimationUtil;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import javax.inject.Inject;

import icepick.Icepick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * The base Activity for all the activities
 * Created by peng on 2016/5/23.
 */
public abstract class BaseActivity extends RxAppCompatActivity {
    private final String TAG = BaseActivity.class.getSimpleName();
    private AlertDialog mAlertDialog;

    /**
     * The subscription to hold all subscriptions and will be cleared when Activity is destroyed
     * Every subscription MUST be added to it to avoid MEMORY LEAK
     */
    private final CompositeSubscription _subscription = new CompositeSubscription();
    @Inject
    protected UserModel userModel;
    @Inject
    protected INetworkService networkService;
    @Inject
    protected IStorageService storageService;

    protected long getDebounceTime() {
        return 400L;
    }

    protected CompositeSubscription getSubsCollector() {
        return _subscription;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseActivityComponent component = DaggerBaseActivityComponent
                .builder()
                .applicationComponent(App.from(this).getAppComponent())
                .networkModule(new NetworkModule())
                .userModule(new UserModule())
                .build();
        injectBaseActivityComponent(component);
        _subscription.add(accessDeniedFallback());
        AnimationUtil.setupWindowAnimations(getWindow());
        Icepick.restoreInstanceState(this, savedInstanceState);
    }

    protected abstract void injectBaseActivityComponent(BaseActivityComponent component);

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    /**
     * Hide alert dialog if any.
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
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
                        result -> {
                            UIHelper.launchActivity(this, LoginActivity.class);
                            finish();
                        },
                        err -> {
                            Log.d("accessDeniedFallback: ", err.getMessage());
                        }
                );
    }

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

    /**
     * This method shows dialog with given title & message.
     * Also there is an option to pass onClickListener for positive & negative button.
     *
     * @param title                         - dialog title
     * @param message                       - dialog message
     * @param onPositiveButtonClickListener - listener for positive button
     * @param positiveText                  - positive button text
     * @param onNegativeButtonClickListener - listener for negative button
     * @param negativeText                  - negative button text
     */
    protected void showAlertDialog(@Nullable String title, @Nullable String message,
                                   @Nullable DialogInterface.OnClickListener onPositiveButtonClickListener,
                                   @NonNull String positiveText,
                                   @Nullable DialogInterface.OnClickListener onNegativeButtonClickListener,
                                   @NonNull String negativeText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveText, onPositiveButtonClickListener);
        builder.setNegativeButton(negativeText, onNegativeButtonClickListener);
        mAlertDialog = builder.show();
    }

    protected void showSnackBar(View view, String message, String actionText) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
        snackbar.setAction(actionText, v -> snackbar.dismiss());
        snackbar.show();
    }

    protected void showSnackBar(String message, String actionText) {
        showSnackBar(getWindow().getDecorView().getRootView(), message, actionText);
    }

    protected Subscription checkNetworkStatus() {
        return networkService.observeNetworkChange()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(networkStatus -> {
                    if (networkStatus == INetworkService.NetworkStatus.OFFLINE ||
                            networkStatus == INetworkService.NetworkStatus.UNKNOWN)
                        showSnackBar(findViewById(android.R.id.content), "no network", "Close");
                });
    }

}
