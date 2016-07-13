package com.soulkey.calltalent.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import com.soulkey.calltalent.App;
import com.soulkey.calltalent.api.network.INetworkManager;
import com.soulkey.calltalent.di.component.ApplicationComponent;
import com.soulkey.calltalent.di.component.BaseActivityComponent;
import com.soulkey.calltalent.di.component.DaggerBaseActivityComponent;
import com.soulkey.calltalent.di.module.DomainModule;
import com.soulkey.calltalent.di.module.StorageModule;
import com.soulkey.calltalent.domain.entity.User;
import com.soulkey.calltalent.domain.model.UserModel;
import com.soulkey.calltalent.service.SplashService;
import com.soulkey.calltalent.ui.auth.LoginActivity;
import com.soulkey.calltalent.utils.animation.AnimationUtil;
import com.soulkey.calltalent.utils.memory.Reflector;
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
    private AlertDialog mAlertDialog;

    /**
     * The subscription to hold all subscriptions and will be cleared when Activity is destroyed
     * Every subscription MUST be added to it to avoid MEMORY LEAK
     */
    private final CompositeSubscription _subscription = new CompositeSubscription();
    @Inject
    protected UserModel userModel;

    private SplashReceiver receiver;

    protected long getDebounceTime() {
        return 400L;
    }
    protected CompositeSubscription getSubsCollector() {
        return _subscription;
    }

    protected abstract void injectBaseActivityComponent(BaseActivityComponent component);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationComponent component = App.from(this).getAppComponent();
        BaseActivityComponent activityComponent = DaggerBaseActivityComponent.builder()
                .applicationComponent(component)
                .storageModule(new StorageModule())
                .domainModule(new DomainModule())
                .build();
        injectBaseActivityComponent(activityComponent);
        _subscription.add(accessDeniedFallback());
        AnimationUtil.setupWindowAnimations(getWindow());
        Icepick.restoreInstanceState(this, savedInstanceState);
        registerSplashReceiver();
    }

    private void registerSplashReceiver() {
        IntentFilter filter = new IntentFilter(SplashReceiver.PARAM_RECEIVED_STORED_IMAGE_URI);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new SplashReceiver();
        registerReceiver(receiver, filter);
    }

    public class SplashReceiver extends BroadcastReceiver {
        public static final String PARAM_RECEIVED_STORED_IMAGE_URI = "com.soulkey.calltalent.service.extra.PARAM_IMAGE_STORED_RESULT";

        @Override
        public void onReceive(Context context, Intent intent) {
            Boolean result = intent.getBooleanExtra(SplashService.PARAM_IMAGE_STORED_RESULT, false);
            Log.d("Receiver: ", "onReceive: " + result);
        }
    }

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
        //fix for memory leak: http://code.google.com/p/android/issues/detail?id=34731
        fixInputMethodManager();
    }

    /**
     * When Activity is destroyed, clear all the subscriptions inside the CompositeSubscription
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
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
                password);
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
        return userModel.observeNetworkChange()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(networkStatus -> {
                    if (networkStatus == INetworkManager.NetworkStatus.OFFLINE ||
                            networkStatus == INetworkManager.NetworkStatus.UNKNOWN)
                        showSnackBar(findViewById(android.R.id.content), "no network", "Close");
                });
    }

    private void fixInputMethodManager() {
        final Object imm = getSystemService(Context.INPUT_METHOD_SERVICE);
        final Reflector.TypedObject windowToken
                = new Reflector.TypedObject(getWindow().getDecorView().getWindowToken(), IBinder.class);
        Reflector.invokeMethodExceptionSafe(imm, "windowDismissed", windowToken);
        final Reflector.TypedObject view
                = new Reflector.TypedObject(null, View.class);
        Reflector.invokeMethodExceptionSafe(imm, "startGettingWindowFocus", view);
    }
}
