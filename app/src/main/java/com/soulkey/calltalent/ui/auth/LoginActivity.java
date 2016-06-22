package com.soulkey.calltalent.ui.auth;

import android.app.Activity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.jakewharton.rxbinding.view.RxView;
import com.soulkey.calltalent.R;
import com.soulkey.calltalent.di.component.ApplicationComponent;
import com.soulkey.calltalent.ui.MainActivity;

import java.util.HashMap;
import java.util.Map;

import rx.Subscription;
import rx.observables.ConnectableObservable;

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

        final AutoCompleteTextView usernameText = (AutoCompleteTextView) findViewById(R.id.usernameText);
        final EditText passwordText = (EditText) findViewById(R.id.passwordText);
        assert registerBtn != null;
        assert signinBtn != null;
        assert usernameText != null;
        assert passwordText != null;
        //parameters to be passed to the login screen if the linktoSignin is clicked
        Map<String, String> params = new HashMap<>();
        usernameText.setText(receiveParams(LoginParams.PARAM_KEY_USERNAME.getValue()));

        validateForm(this);

        ConnectableObservable<CharSequence> emailStream = getEmailTextChangeStream(usernameText);
        emailStream.connect();
        getSubsCollector().add(dealWithEmailTextChanges(emailStream, usernameText, params));

        getSubsCollector().add(dealWithSignin(signinBtn, usernameText, passwordText));

        getSubsCollector().add(dealWithRegister(registerBtn, params));

    }

    private void validateForm(final Activity activity) {
        mAwesomeValidation.addValidation(
                activity,
                R.id.usernameText,
                Patterns.EMAIL_ADDRESS,
                R.string.validation_email_not_valid);
        mAwesomeValidation.addValidation(
                activity,
                R.id.usernameText,
                RegexTemplate.NOT_EMPTY,
                R.string.validation_username_not_empty);
        mAwesomeValidation.addValidation(
                activity,
                R.id.passwordText,
                RegexTemplate.NOT_EMPTY,
                R.string.validation_password_not_empty);
    }

    private Subscription dealWithSignin(
            Button button, TextView usernameText, TextView passwordText) {
        return RxView.clicks(button)
                .filter(aVoid1 -> mAwesomeValidation.validate())
                .doOnNext(aVoid -> button.setEnabled(false))
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
