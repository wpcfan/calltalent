package com.soulkey.calltalent.ui.auth;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.soulkey.calltalent.R;
import com.soulkey.calltalent.ui.BaseActivity;
import com.soulkey.calltalent.utils.validation.ValidationUtils;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * To reuse the common logic between RegisterActivity and LoginActivity
 * Created by peng on 2016/6/11.
 */
public abstract class EmailAutoCompleteActivity extends BaseActivity {

    Subscription dealWithEmailTextChanges(
            Observable<CharSequence> observable,
            AutoCompleteTextView view,
            Map<String, String> params) {
        return observable
                .doOnNext(s -> params.put(LoginParams.PARAM_KEY_USERNAME.getValue(), s.toString()))
                .filter(charSequence ->
                        charSequence.length() > 3 &&
                                (charSequence.charAt(charSequence.length() - 1) == '@'))
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(
                        ev -> showAutoCompleteDropdown(view),
                        err -> {
                            Toast.makeText(this, err.getMessage(), Toast.LENGTH_SHORT).show();
                            view.dismissDropDown();
                        });
    }

    @NonNull
    Observable<CharSequence> getUsernameTextChangeStream(AutoCompleteTextView view) {
        return RxTextView.textChanges(view)
                .debounce(getDebounceTime(), TimeUnit.MICROSECONDS)
                .publish()
                .refCount();
    }

    Subscription switchPasswordVisibility(
            final CompoundButton showHideSwitch, final TextView passwordText) {
        return RxCompoundButton.checkedChanges(showHideSwitch)
                .compose(bindToLifecycle())
                .subscribe(checked -> {
                    if (checked)
                        passwordText.setInputType(
                                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    else
                        passwordText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                });
    }

    boolean validateIdenticalPasswords(TextView text, TextView repeatText, TextInputLayout wrapper) {
        boolean repeatValid = repeatText.getText().toString()
                .equals(text.getText().toString());
        if (!repeatValid) {
            wrapper.setError(getResources().getString(
                    R.string.validation_repeat_password_not_same));
            return false;
        } else {
            wrapper.setErrorEnabled(false);
            return true;
        }
    }

    boolean validateEmail(TextView view, TextInputLayout wrapper) {
        if (!ValidationUtils.isValidEmailAddress(
                view.getText().toString()).isValid()) {
            wrapper.setError(
                    getResources().getString(R.string.validation_email_not_valid));
            return false;
        } else {
            wrapper.setErrorEnabled(false);
            return true;
        }
    }

    boolean validateRequiredField(TextView view, TextInputLayout wrapper) {
        if (!ValidationUtils.validateRequiredField(
                view.getText().toString()).isValid()) {
            wrapper.setError(
                    getResources().getString(R.string.validation_required_not_met));
            return false;
        } else {
            wrapper.setErrorEnabled(false);
            return true;
        }
    }

    private void showAutoCompleteDropdown(AutoCompleteTextView emailView) {
        String[] suggestion = getResources().getStringArray(R.array.email_suggestion).clone();
        for (int i = 0; i < suggestion.length; i++)
            suggestion[i] = emailView.getText().toString() + suggestion[i];
        ArrayAdapter<String> suggestionAdapter = new ArrayAdapter<>(
                this,
                R.layout.support_simple_spinner_dropdown_item,
                suggestion);
        emailView.setAdapter(suggestionAdapter);
        emailView.showDropDown();
    }
}
