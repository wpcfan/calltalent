package com.soulkey.calltalent.ui.user;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.soulkey.calltalent.R;
import com.soulkey.calltalent.di.component.BaseActivityComponent;
import com.soulkey.calltalent.domain.entity.UserProfile;
import com.soulkey.calltalent.exception.RequireFieldNotSetException;
import com.soulkey.calltalent.ui.BaseActivity;
import com.soulkey.calltalent.ui.UIHelper;
import com.soulkey.calltalent.ui.auth.LoginParams;
import com.soulkey.calltalent.utils.image.ImageUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import icepick.State;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * CreateUserProfileActivity is the UI for users to create his/her profile
 */
public final class CreateUserProfileActivity extends BaseActivity {

    private final String TEMP_PROFILE_NAME = "temp_profile_name";
    private final String TEMP_PROFILE_TITLE = "temp_profile_title";
    private final String TEMP_PROFILE_DESC = "temp_profile_desc";
    private final String TEMP_PROFILE_GENDER = "temp_profile_gender";

    @BindView(R.id.nameInput)
    EditText nameInput;
    @BindView(R.id.titleInput)
    EditText titleInput;
    @BindView(R.id.descInput)
    EditText descInput;
    @BindView(R.id.completeUserProfileBtn)
    Button completeUserProfile;
    @BindView(R.id.showUserProfileBtn)
    Button showUserProfileBtn;
    @BindView(R.id.take_photo_btn)
    ImageView avatarImage;
    @BindView(R.id.gender_radio_group)
    RadioGroup genderRadioGroup;
    @BindView(R.id.gender_male_selected)
    RadioButton maleChecked;
    @BindView(R.id.gender_female_selected)
    RadioButton femaleChecked;
    @State
    String localUri;
    @State
    String uid;
    @State
    String remoteUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user_profile);
        ButterKnife.bind(this);

        uid = receiveParams(LoginParams.PARAM_KEY_UID.getValue());
        Subscription subReceiveParam = getParams()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(path -> {
                    String dirname = ImageUtil.getApplicationName(this);
                    File dir = ImageUtil.getPublicDir(null, dirname, this);
                    localUri = dir.getAbsolutePath() + "/" + path;
                    RoundedBitmapDrawable dr = RoundedBitmapDrawableFactory.create(getResources(), localUri);
                    dr.setCornerRadius(5);
                    avatarImage.setImageDrawable(dr);
                    nameInput.setText(userModel.readString(TEMP_PROFILE_NAME));
                    titleInput.setText(userModel.readString(TEMP_PROFILE_TITLE));
                    descInput.setText(userModel.readString(TEMP_PROFILE_DESC));
                    if (userModel.readBoolean(TEMP_PROFILE_GENDER))
                        maleChecked.setChecked(true);
                    else
                        femaleChecked.setChecked(true);
                });

        getSubsCollector().add(subReceiveParam);

        //parameters to be passed to the login screen if the linktoSignin is clicked
        Map<String, String> params = new HashMap<>();
        params.put(LoginParams.PARAM_KEY_UID.getValue(), uid);
        getSubsCollector().add(maleCheckedSubscription());
        getSubsCollector().add(femaleCheckedSubscription());

        getSubsCollector().add(dealWithSubmit());
        getSubsCollector().add(RxView.clicks(showUserProfileBtn)
                .flatMap(aVoid -> userModel.getUserProfile(uid))
                .compose(bindToLifecycle())
                .subscribe(profile -> Toast.makeText(this, profile.name(), Toast.LENGTH_SHORT).show()));
        getSubsCollector().add(dealWithAvatar(params));
    }

    @Override
    protected void injectBaseActivityComponent(BaseActivityComponent component) {
        component.inject(this);
    }

    private Observable<String> getParams() {
        return Observable.just(receiveParams(LoginParams.PARAM_KEY_AVATAR_URI.getValue()))
                .filter(code -> code != null)
                .compose(bindToLifecycle());
    }

    private Subscription dealWithAvatar(Map<String, String> params) {
        return RxView.clicks(avatarImage)
                .compose(bindToLifecycle())
                .subscribe(aVoid -> {
                    userModel.writeString(TEMP_PROFILE_NAME, nameInput.getText().toString());
                    userModel.writeString(TEMP_PROFILE_TITLE, titleInput.getText().toString());
                    userModel.writeString(TEMP_PROFILE_DESC, descInput.getText().toString());
                    userModel.writeBoolean(TEMP_PROFILE_GENDER, genderRadioGroup.getCheckedRadioButtonId() == R.id.gender_male_selected);
                    UIHelper.launchActivity(this, AvatarActivity.class, params);
                    finish();
                });
    }

    private Subscription dealWithSubmit() {
        return getSubmitProfileStream()
                .subscribe(aVoid1 -> Log.d("Haha", "onCreate: "));
    }

    private Observable<Boolean> getSubmitProfileStream() {
        return RxView.clicks(completeUserProfile)
                .flatMap(__ -> {
                    if (TextUtils.isEmpty(localUri))
                        return Observable.error(
                                new RequireFieldNotSetException(
                                        getResources()
                                                .getString(R.string.validation_avatar_not_empty)))
                                .materialize();
                    byte[] data = new byte[0];
                    try {
                        data = ImageUtil.getBytes(this, Uri.parse(localUri));
                    } catch (IOException e) {
                        return Observable.error(e).materialize();
                    }
                    return userModel.uploadAvatar(data, uid).materialize();
                })
                .doOnNext(notice -> {
                    if (notice.hasThrowable())
                        Toast.makeText(
                                this, notice.getThrowable().getMessage(), Toast.LENGTH_SHORT).show();
                })
                .doOnNext(__ -> {
                    userModel.remove(TEMP_PROFILE_NAME);
                    userModel.remove(TEMP_PROFILE_TITLE);
                    userModel.remove(TEMP_PROFILE_GENDER);
                    userModel.remove(TEMP_PROFILE_DESC);
                })
                .flatMap(notification -> {
                    if (notification.hasValue()) {
                        remoteUri = (String) notification.getValue();
                        UserProfile userProfile = UserProfile.create(
                                uid,
                                nameInput.getText().toString(),
                                titleInput.getText().toString(),
                                remoteUri,
                                genderRadioGroup.getCheckedRadioButtonId() == R.id.gender_male_selected,
                                descInput.getText().toString());
                        return userModel.saveUserProfile(userProfile, uid);
                    } else {
                        return Observable.just(false);
                    }
                })
                .doOnError(err -> Toast.makeText(
                        this, err.getMessage(), Toast.LENGTH_SHORT).show()
                )
                .onErrorResumeNext(throwable -> Observable.just(false))
                .compose(bindToLifecycle());
    }

    private Subscription femaleCheckedSubscription() {
        return RxView.clicks(femaleChecked)
                .subscribe(__ -> {
                    femaleChecked.setTextColor(
                            ContextCompat.getColor(this, R.color.gender_female));
                    maleChecked.setTextColor(ContextCompat.getColor(this, R.color.secondary_text));
                });
    }

    private Subscription maleCheckedSubscription() {
        return RxView.clicks(maleChecked)
                .subscribe(__ -> {
                    maleChecked.setTextColor(
                            ContextCompat.getColor(this, R.color.gender_male));
                    femaleChecked.setTextColor(ContextCompat.getColor(this, R.color.secondary_text));
                });
    }

    @Override
    protected Boolean requiredLoggedIn() {
        return true;
    }
}