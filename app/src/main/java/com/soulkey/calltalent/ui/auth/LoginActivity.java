package com.soulkey.calltalent.ui.auth;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * The LoginActivity defines the login-related behaviors
 */
public class LoginActivity extends EmailAutoCompleteActivity {
    private boolean shouldFinish;
    @BindView(R.id.link_to_register)
    TextView registerBtn;
    @BindView(R.id.signinBtn)
    Button signinBtn;
    @BindView(R.id.usernameWrapper)
    TextInputLayout usernameWrapper;
    @BindView(R.id.passwordWrapper)
    TextInputLayout passwordWrapper;
    @BindView(R.id.usernameText)
    AutoCompleteTextView usernameText;
    @BindView(R.id.showHidePasswordSwitch)
    Switch showHideSwitch;
    @BindView(R.id.passwordText)
    EditText passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        shouldFinish = false;
        //parameters to be passed to the login screen if the linktoSignin is clicked
        Map<String, String> params = new HashMap<>();
        usernameText.setText(receiveParams(LoginParams.PARAM_KEY_USERNAME.getValue()));

        Observable<CharSequence> observable = getUsernameTextChangeStream(usernameText);

        getSubsCollector().add(dealWithEmailTextChanges(observable, usernameText, params));
        getSubsCollector().add(observable
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(charSequence -> {
                    validateRequiredField(usernameText, usernameWrapper);
                    if (usernameText.length() > 0)
                        validateEmail(usernameText, usernameWrapper);
                }));

        getSubsCollector().add(dealWithSignin());

        getSubsCollector().add(dealWithRegister(getResources().getString(R.string.link_to_register_transition), params));

        getSubsCollector().add(switchPasswordVisibility(showHideSwitch, passwordText));

        getSubsCollector().add(getPasswordTextChangeStream()
                .subscribe(charSequence1 -> {
                    validateRequiredField(passwordText, passwordWrapper);
                }));

    }

    private Observable<CharSequence> getPasswordTextChangeStream() {
        return RxTextView.textChanges(passwordText)
                .debounce(getDebounceTime(), TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle());
    }

    @Override
    protected void onStop() {
        super.onStop();
        // in order to eliminate the flicker effect when activity transitions
        if (shouldFinish)
            finish();
    }

    private Subscription dealWithSignin() {
        return RxView.clicks(signinBtn)
                .filter(__ -> validateRequiredField(usernameText, usernameWrapper) &&
                        validateRequiredField(passwordText, passwordWrapper) &&
                        validateEmail(usernameText, usernameWrapper))
                .doOnNext(__ -> signinBtn.setEnabled(false))
                .flatMap(__ -> login(
                        usernameText.getText().toString(), passwordText.getText().toString()))
                .doOnNext(u -> {
                    if (u == null) signinBtn.setEnabled(true);
                })
                .filter(user1 -> user1 != null)
                .compose(bindToLifecycle())
                .subscribe(user -> {
                    UIHelper.launchActivity(LoginActivity.this, MainActivity.class);
                    finish();
                });
    }

    private Subscription dealWithRegister(String transitionName, Map<String, String> params) {
        return RxView.clicks(registerBtn)
                .compose(bindToLifecycle())
                .subscribe(
                        ev -> {
                            UIHelper.launchActivityWithTransition(LoginActivity.this,
                                    RegisterActivity.class, registerBtn, transitionName, params);
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
