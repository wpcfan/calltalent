package com.soulkey.calltalent.ui.auth;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.soulkey.calltalent.R;
import com.soulkey.calltalent.ui.BaseActivity;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.observables.ConnectableObservable;

/**
 * To reuse the common logic between RegisterActivity and LoginActivity
 * Created by peng on 2016/6/11.
 */
public abstract class EmailAutoCompleteActivity extends BaseActivity {

    protected AwesomeValidation mAwesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

    ConnectableObservable<CharSequence> getEmailTextChangeStream(AutoCompleteTextView emailView) {
        return RxTextView.textChanges(emailView)
                .debounce(getDebounceTime(), TimeUnit.MICROSECONDS)
                .publish();
    }

    Subscription dealWithEmailTextChanges(
            ConnectableObservable<CharSequence> stream,
            AutoCompleteTextView emailAutoComplete,
            Map<String, String> params) {
        return stream
                .doOnNext(s -> params.put(LoginParams.PARAM_KEY_USERNAME.getValue(), s.toString()))
                .filter(charSequence ->
                        charSequence.length() > 3 &&
                                (charSequence.charAt(charSequence.length() - 1) == '@'))
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(
                        ev -> showAutoCompleteDropdown(emailAutoComplete),
                        err -> {
                            Toast.makeText(this, err.getMessage(), Toast.LENGTH_SHORT).show();
                            emailAutoComplete.dismissDropDown();
                        });
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
