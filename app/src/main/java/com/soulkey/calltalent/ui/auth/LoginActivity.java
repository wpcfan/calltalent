package com.soulkey.calltalent.ui.auth;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.soulkey.calltalent.R;
import com.soulkey.calltalent.di.component.ApplicationComponent;
import com.soulkey.calltalent.ui.MainActivity;
import com.soulkey.calltalent.ui.UIHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * The LoginActivity defines the login-related behaviors
 */
public class LoginActivity extends EmailAutoCompleteActivity {
    private boolean shouldFinish;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final TextView registerBtn = (TextView) findViewById(R.id.link_to_register);
        final Button signinBtn = (Button) findViewById(R.id.signinBtn);
        final TextInputLayout usernameWrapper = (TextInputLayout) findViewById(R.id.usernameWrapper);
        final TextInputLayout passwordWrapper = (TextInputLayout) findViewById(R.id.passwordWrapper);
        final AutoCompleteTextView usernameText = (AutoCompleteTextView) findViewById(R.id.usernameText);
        final Switch showHideSwitch = (Switch) findViewById(R.id.showHidePasswordSwitch);
        final EditText passwordText = (EditText) findViewById(R.id.passwordText);
        assert registerBtn != null;
        assert signinBtn != null;
        assert usernameText != null;
        assert passwordText != null;
        assert usernameWrapper != null;
        assert passwordWrapper != null;
        assert showHideSwitch != null;

        shouldFinish = false;
        //parameters to be passed to the login screen if the linktoSignin is clicked
        Map<String, String> params = new HashMap<>();
        usernameText.setText(receiveParams(LoginParams.PARAM_KEY_USERNAME.getValue()));


        getSubsCollector().add(dealWithEmailTextChanges(usernameText, params));

        getSubsCollector().add(dealWithSignin(
                signinBtn, usernameText, passwordText, usernameWrapper, passwordWrapper));

        getSubsCollector().add(dealWithRegister(
                registerBtn, getResources().getString(R.string.link_to_register_transition), params));

        getSubsCollector().add(switchPasswordVisibility(showHideSwitch, passwordText));

        getSubsCollector().add(RxTextView.textChanges(passwordText)
                .debounce(getDebounceTime(), TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .filter(charSequence -> charSequence.length()>getThrottleCount())
                .compose(bindToLifecycle())
                .subscribe(charSequence1 -> {
                    validateRequiredField(passwordText, passwordWrapper);
                }));

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (shouldFinish)
            finish();
    }

    private Subscription dealWithSignin(
            Button button,
            TextView usernameText,
            TextView passwordText,
            TextInputLayout usernameWrapper,
            TextInputLayout passwordWrapper) {
        return RxView.clicks(button)
                .filter(__ -> validateRequiredField(usernameText, usernameWrapper) &&
                        validateRequiredField(passwordText, passwordWrapper) &&
                        validateEmail(usernameText, usernameWrapper))
                .doOnNext(__ -> button.setEnabled(false))
                .flatMap(__ -> login(
                        usernameText.getText().toString(), passwordText.getText().toString()))
                .doOnNext(u -> {
                    if (u == null) button.setEnabled(true);
                })
                .filter(user1 -> user1 != null)
                .compose(bindToLifecycle())
                .subscribe(user -> {
                    UIHelper.launchActivity(LoginActivity.this, MainActivity.class);
                    finish();
                });
    }

    private Subscription dealWithRegister(View view, String transitionName, Map<String, String> params) {
        return RxView.clicks(view)
                .compose(bindToLifecycle())
                .subscribe(
                        ev -> {
                            UIHelper.launchActivityWithTransition(LoginActivity.this,
                                    RegisterActivity.class, view, transitionName, params);
                            shouldFinish = true;
                        }
                );
    }


    @Override
    protected void injectComponent(ApplicationComponent component) {
        component.inject(this);
    }

    /**
     * Should always return false as users need to access to the login page regardless of rights
     *
     * @return false
     */
    @Override
    protected Boolean requiredLoggedIn() {
        return false;
    }

}
