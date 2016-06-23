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
import com.soulkey.calltalent.R;
import com.soulkey.calltalent.di.component.ApplicationComponent;
import com.soulkey.calltalent.ui.MainActivity;
import com.soulkey.calltalent.ui.UIHelper;
import com.soulkey.calltalent.utils.validation.ValidationUtils;

import java.util.HashMap;
import java.util.Map;

import rx.Subscription;

/**
 * The LoginActivity defines the login-related behaviors
 */
public class LoginActivity extends EmailAutoCompleteActivity {

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

        //parameters to be passed to the login screen if the linktoSignin is clicked
        Map<String, String> params = new HashMap<>();
        usernameText.setText(receiveParams(LoginParams.PARAM_KEY_USERNAME.getValue()));

        getSubsCollector().add(dealWithEmailTextChanges(usernameText, params));

        getSubsCollector().add(dealWithSignin(
                signinBtn, usernameText, passwordText, usernameWrapper, passwordWrapper));

        getSubsCollector().add(dealWithRegister(registerBtn, params));

        getSubsCollector().add(switchPasswordVisibility(showHideSwitch, passwordText));
    }

    private Subscription dealWithSignin(
            Button button,
            TextView usernameText,
            TextView passwordText,
            TextInputLayout usernameWrapper,
            TextInputLayout passwordWrapper) {
        return RxView.clicks(button)
                .filter(aVoid1 -> {
                    if (!ValidationUtils.validateRequiredField(
                            usernameText.getText().toString()).isValid()) {
                        usernameWrapper.setError(
                                getResources().getString(R.string.validation_username_not_empty));
                        return false;
                    }
                    if (!ValidationUtils.validateRequiredField(
                            passwordText.getText().toString()).isValid()) {
                        passwordWrapper.setError(
                                getResources().getString(R.string.validation_password_not_empty));
                        return false;
                    }

                    if (!ValidationUtils.isValidEmailAddress(
                            usernameText.getText().toString()).isValid()) {
                        usernameWrapper.setError(
                                getResources().getString(R.string.validation_email_not_valid));
                        return false;
                    }
                    usernameWrapper.setErrorEnabled(false);
                    passwordWrapper.setErrorEnabled(false);
                    return true;
                })
                .doOnNext(aVoid -> {
                    button.setEnabled(false);
                    UIHelper.hideKeyboard(this, button);
                })
                .flatMap(aVoid -> login(
                        usernameText.getText().toString(), passwordText.getText().toString()))
                .doOnNext(u -> {
                    if (u == null) button.setEnabled(true);
                })
                .filter(user1 -> user1 != null)
                .compose(bindToLifecycle())
                .subscribe(user -> launchActivity(MainActivity.class));
    }

    private Subscription dealWithRegister(View view, Map<String, String> params) {
        return RxView.clicks(view)
                .compose(bindToLifecycle())
                .subscribe(
                        ev -> launchActivity(RegisterActivity.class, params)
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
