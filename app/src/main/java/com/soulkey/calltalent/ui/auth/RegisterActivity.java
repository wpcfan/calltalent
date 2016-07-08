package com.soulkey.calltalent.ui.auth;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.soulkey.calltalent.R;
import com.soulkey.calltalent.di.component.BaseActivityComponent;
import com.soulkey.calltalent.ui.UIHelper;
import com.soulkey.calltalent.ui.user.CreateUserProfileActivity;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;

@SuppressWarnings("ALL")
public final class RegisterActivity extends EmailAutoCompleteActivity {

    @BindView(R.id.registerBtn)
    Button registerBtn;
    @BindView(R.id.linktoSignin)
    TextView signinBtn;
    @BindView(R.id.showHidePasswordSwitch)
    Switch showHideSwitch;
    @BindView(R.id.usernameText)
    AutoCompleteTextView usernameText;
    @BindView(R.id.passwordText)
    EditText passwordText;
    @BindView(R.id.repeatPasswordText)
    EditText repeatPasswordText;
    @BindView(R.id.usernameWrapper)
    TextInputLayout usernameWrapper;
    @BindView(R.id.passwordWrapper)
    TextInputLayout passwordWrapper;
    @BindView(R.id.repeatPasswordWrapper)
    TextInputLayout repeatPasswordWrapper;
    @BindView(R.id.component_repeat_password_icon)
    ImageView repeatPasswordIcon;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        repeatPasswordIcon.setVisibility(View.VISIBLE);
        repeatPasswordWrapper.setVisibility(View.VISIBLE);

        //parameters to be passed to the login screen if the linktoSignin is clicked
        Map<String, String> params = new HashMap<>();
        usernameText.setText(receiveParams(LoginParams.PARAM_KEY_USERNAME.getValue()));

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> {
            UIHelper.launchActivity(this, LoginActivity.class, params);
            finish();
        });
        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        Observable<CharSequence> observable = getUsernameTextChangeStream(usernameText);

        getSubsCollector().add(dealWithEmailTextChanges(observable, usernameText, params));

        getSubsCollector().add(dealWithRegister(params));

        getSubsCollector().add(dealWithSignin(params));

        getSubsCollector().add(switchPasswordVisibility(showHideSwitch, passwordText));

        getSubsCollector().add(checkNetworkStatus());
    }

    @Override
    protected void injectBaseActivityComponent(BaseActivityComponent component) {
        component.inject(this);
    }

    @Override
    protected Boolean requiredLoggedIn() {
        return false;
    }

    @NonNull
    private Subscription dealWithRegister(final Map<String, String> params) {
        return RxView.clicks(registerBtn)
                .filter(__ -> validateForm())
                .doOnNext(__ -> registerBtn.setEnabled(false))
                .flatMap(__ -> registerUser(
                        usernameText.getText().toString(),
                        passwordText.getText().toString()))
                .doOnNext(isRegistered -> {
                    if (!isRegistered)
                        registerBtn.setEnabled(true);
                })
                .filter(isRegistered -> isRegistered)
                .flatMap(isRegistered -> login(
                        usernameText.getText().toString(),
                        passwordText.getText().toString()))
                .compose(bindToLifecycle())
                .subscribe(user -> {
                    if (user != null) {
                        params.put(LoginParams.PARAM_KEY_UID.getValue(), user.uid());
                        UIHelper.launchActivity(this, CreateUserProfileActivity.class, params);
                        finish();
                    }
                });
    }

    private boolean validateForm() {
        return validateRequiredField(usernameText, usernameWrapper) &&
                validateRequiredField(passwordText, passwordWrapper) &&
                validateRequiredField(repeatPasswordText, repeatPasswordWrapper) &&
                validateEmail(usernameText, usernameWrapper) &&
                validateIdenticalPasswords(passwordText, repeatPasswordText, repeatPasswordWrapper);
    }

    @NonNull
    private Observable<Boolean> registerUser(final String username, final String password) {
        return userModel.register(
                username,
                password)
                .doOnError(throwable ->
                        Toast.makeText(
                                this,
                                throwable.getMessage(),
                                Toast.LENGTH_SHORT)
                                .show())
                .onErrorResumeNext(throwable -> Observable.just(false))
                .compose(bindToLifecycle());
    }

    @NonNull
    private Subscription dealWithSignin(final Map<String, String> params) {
        return RxView.clicks(signinBtn)
                .compose(bindToLifecycle())
                .subscribe(
                        ev -> {
                            UIHelper.launchActivity(this, LoginActivity.class, params);
                            finish();
                        }
                );
    }

}
