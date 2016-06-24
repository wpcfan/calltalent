package com.soulkey.calltalent.ui.auth;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.soulkey.calltalent.R;
import com.soulkey.calltalent.di.component.ApplicationComponent;
import com.soulkey.calltalent.ui.user.CreateUserProfileActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class RegisterActivity extends EmailAutoCompleteActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        final Button registerBtn = (Button) findViewById(R.id.registerBtn);
        final TextView signinBtn = (TextView) findViewById(R.id.linktoSignin);
        final Switch showHideSwitch = (Switch) findViewById(R.id.showHidePasswordSwitch);
        final AutoCompleteTextView usernameText = (AutoCompleteTextView) findViewById(R.id.usernameText);
        final EditText passwordText = (EditText) findViewById(R.id.passwordText);
        final EditText repeatPasswordText = (EditText) findViewById(R.id.repeatPasswordText);
        final TextInputLayout usernameWrapper = (TextInputLayout) findViewById(R.id.usernameWrapper);
        final TextInputLayout passwordWrapper = (TextInputLayout) findViewById(R.id.passwordWrapper);
        final TextInputLayout repeatPasswordWrapper = (TextInputLayout) findViewById(R.id.repeatPasswordWrapper);
        final ImageView repeatPasswordIcon = (ImageView) findViewById(R.id.component_repeat_password_icon);
        assert registerBtn != null;
        assert signinBtn != null;
        assert showHideSwitch != null;
        assert repeatPasswordText != null;
        assert usernameText != null;
        assert passwordText != null;
        assert usernameWrapper != null;
        assert passwordWrapper != null;
        assert repeatPasswordWrapper != null;
        assert repeatPasswordIcon != null;

        repeatPasswordIcon.setVisibility(View.VISIBLE);
        repeatPasswordWrapper.setVisibility(View.VISIBLE);

        //parameters to be passed to the login screen if the linktoSignin is clicked
        Map<String, String> params = new HashMap<>();
        usernameText.setText(receiveParams(LoginParams.PARAM_KEY_USERNAME.getValue()));

        getSubsCollector().add(dealWithEmailTextChanges(usernameText, params));

        getSubsCollector().add(dealWithRegister(
                registerBtn,
                usernameText,
                passwordText,
                repeatPasswordText,
                usernameWrapper,
                passwordWrapper,
                repeatPasswordWrapper,
                params));

        getSubsCollector().add(dealWithSignin(signinBtn, params));

        getSubsCollector().add(switchPasswordVisibility(showHideSwitch, passwordText));

        getSubsCollector().add(RxTextView.textChanges(passwordText)
                .debounce(getDebounceTime(), TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .filter(charSequence -> charSequence.length()>getThrottleCount())
                .compose(bindToLifecycle())
                .subscribe(charSequence1 -> {
                    validateRequiredField(passwordText, passwordWrapper);
                    if(repeatPasswordText.getText().length()>0)
                        validateIdenticalPasswords(passwordText, repeatPasswordText, repeatPasswordWrapper);
                }));

        getSubsCollector().add(RxTextView.textChanges(repeatPasswordText)
                .debounce(getDebounceTime(), TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .filter(charSequence -> charSequence.length()>getThrottleCount())
                .compose(bindToLifecycle())
                .subscribe(charSequence1 -> {
                    validateRequiredField(repeatPasswordText, repeatPasswordWrapper);
                    if(passwordText.getText().length()>0)
                        validateIdenticalPasswords(passwordText, repeatPasswordText, repeatPasswordWrapper);
                }));
    }
    @Override
    protected void injectComponent(ApplicationComponent component) {
        component.inject(this);
    }

    @Override
    protected Boolean requiredLoggedIn() {
        return false;
    }

    @NonNull
    private Subscription dealWithRegister(
            final View view,
            final TextView usernameText,
            final TextView passwordText,
            final TextView repeatPasswordText,
            final TextInputLayout usernameWrapper,
            final TextInputLayout passwordWrapper,
            final TextInputLayout repeatPasswordWrapper,
            final Map<String, String> params) {
        return RxView.clicks(view)
                .filter(__ -> validateForm(
                        usernameText,
                        passwordText,
                        repeatPasswordText,
                        usernameWrapper,
                        passwordWrapper,
                        repeatPasswordWrapper))
                .doOnNext(__ -> view.setEnabled(false))
                .flatMap(__ -> registerUser(
                        usernameText.getText().toString(),
                        passwordText.getText().toString()))
                .doOnNext(isRegistered -> {
                    if (!isRegistered)
                        view.setEnabled(true);
                })
                .filter(isRegisterd -> isRegisterd)
                .flatMap(isRegisterd -> login(
                        usernameText.getText().toString(),
                        passwordText.getText().toString()))
                .compose(bindToLifecycle())
                .subscribe(user -> {
                    if (user != null) {
                        params.put(LoginParams.PARAM_KEY_UID.getValue(), user.uid());
                        launchActivity(CreateUserProfileActivity.class, params);
                    }
                });
    }

    private boolean validateForm(TextView usernameText, TextView passwordText, TextView repeatPasswordText, TextInputLayout usernameWrapper, TextInputLayout passwordWrapper, TextInputLayout repeatPasswordWrapper) {
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
    private Subscription dealWithSignin(final View view, final Map<String, String> params) {
        return RxView.clicks(view)
                .compose(bindToLifecycle())
                .subscribe(
                        ev -> launchActivity(LoginActivity.class, params)
                );
    }

}
