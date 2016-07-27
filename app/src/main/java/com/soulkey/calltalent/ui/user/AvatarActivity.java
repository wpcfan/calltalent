package com.soulkey.calltalent.ui.user;

import android.Manifest;
import android.app.ProgressDialog;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.ragnarok.rxcamera.RxCamera;
import com.ragnarok.rxcamera.RxCameraData;
import com.ragnarok.rxcamera.config.RxCameraConfig;
import com.soulkey.calltalent.R;
import com.soulkey.calltalent.di.component.BaseActivityComponent;
import com.soulkey.calltalent.ui.BaseActivity;
import com.soulkey.calltalent.ui.UIHelper;
import com.soulkey.calltalent.ui.auth.LoginParams;
import com.soulkey.calltalent.utils.image.CameraUtil;
import com.soulkey.calltalent.utils.image.ImageUtil;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.yalantis.ucrop.UCrop;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import icepick.State;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx_activity_result.Result;
import rx_activity_result.RxActivityResult;

public final class AvatarActivity extends BaseActivity {
    private final String TAG = AvatarActivity.class.getSimpleName();
    @State
    byte[] cameraData;
    @State
    String uid;
    Matrix rotateMatrix;
    @State
    Uri tempFileUri;

    @BindView(R.id.open_camera)
    Button captureBtn;
    @BindView(R.id.close_camera)
    Button saveBtn;
    @BindView(R.id.edit_image)
    Button editBtn;
    @BindView(R.id.recapture)
    Button recaptureBtn;
    @BindView(R.id.preview_surface)
    TextureView textureView;
    @BindView(R.id.image_preview)
    ImageView imagePreview;
    @BindView(R.id.avatar_activity_progress_bar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar);
        ButterKnife.bind(this);
        ProgressDialog dialog = configProgressDialog();

        uid = receiveParams(LoginParams.PARAM_KEY_UID.getValue());

        //Make the camera preview a hot observable
        Observable<RxCamera> observable = checkPermissionAndLaunchCamera()
                .publish()
                .refCount();

        initCameraPreview(observable);
        dealWithCapture(observable);
        dealWithRecapture(observable);
        dealWithEdit();
        dealWithSave(dialog);
    }

    @Override
    protected void injectBaseActivityComponent(BaseActivityComponent component) {
        component.inject(this);
    }

    public Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        params.put(LoginParams.PARAM_KEY_UID.getValue(), uid);
        params.put(LoginParams.PARAM_KEY_AVATAR_URI.getValue(), tempFileUri.getLastPathSegment());
        return params;
    }

    @NonNull
    private ProgressDialog configProgressDialog() {
        ProgressDialog dialog = new ProgressDialog(AvatarActivity.this);
        dialog.setIndeterminate(true);
        dialog.setTitle(getResources().getString(R.string.progress_bar_title_saving));
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    private void initCameraPreview(Observable<RxCamera> observable) {
        Subscription subPreview = observable.compose(bindToLifecycle())
                .subscribe(__ -> captureBtn.setEnabled(true));
        getSubsCollector().add(subPreview);
    }

    private void dealWithSave(ProgressDialog dialog) {
        Subscription subSave = getSaveImageStream(dialog)
                .subscribe(__ -> {
                    dialog.dismiss();
                    progressBar.setVisibility(View.GONE);
                    UIHelper.launchActivity(
                            AvatarActivity.this, CreateUserProfileActivity.class, getParams());
                    finish();
                });
        getSubsCollector().add(subSave);
    }

    private void dealWithEdit() {
        Subscription subEdit = getEditImageStream()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(uri -> {
                    if (uri != null) {
                        tempFileUri = uri;
                    }
                    imagePreview.setImageURI(tempFileUri);
                    editBtn.setEnabled(true);
                });
        getSubsCollector().add(subEdit);
    }

    private void dealWithRecapture(Observable<RxCamera> observable) {
        Subscription subRecapture = getRecaptureStream(observable)
                .subscribe(__ -> {
                    captureBtn.setEnabled(true);
                    captureBtn.setVisibility(View.VISIBLE);
                    textureView.setVisibility(View.VISIBLE);
                    imagePreview.setVisibility(View.GONE);
                    imagePreview.setImageBitmap(null);
                    imagePreview.setImageDrawable(null);
                    recaptureBtn.setVisibility(View.GONE);
                    editBtn.setVisibility(View.GONE);
                    saveBtn.setVisibility(View.GONE);
                });
        getSubsCollector().add(subRecapture);
    }

    private void dealWithCapture(Observable<RxCamera> observable) {
        Subscription subCapture = getCaptureStream(observable)
                .flatMap(rxCameraData -> {
                    cameraData = rxCameraData.cameraData;
                    rotateMatrix = rxCameraData.rotateMatrix;
                    return ImageUtil.createBitmap(cameraData, rotateMatrix);
                })
                .flatMap(bitmap -> ImageUtil.saveImageToCache(this, bitmap))
                .subscribe(uri -> {
                    tempFileUri = uri;
                    imagePreview.setImageURI(uri);
                    captureBtn.setVisibility(View.GONE);
                    textureView.setVisibility(View.GONE);
                    imagePreview.setVisibility(View.VISIBLE);
                    recaptureBtn.setVisibility(View.VISIBLE);
                    editBtn.setVisibility(View.VISIBLE);
                    saveBtn.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                });
        getSubsCollector().add(subCapture);
    }

    private Observable<Void> getSaveImageStream(ProgressDialog dialog) {
        return RxView.clicks(saveBtn)
                .doOnNext(__ -> {
                    saveBtn.setEnabled(false);
                    recaptureBtn.setEnabled(false);
                    dialog.show();
                })
                .compose(bindToLifecycle());
    }

    private Observable<Uri> getEditImageStream() {
        return RxView.clicks(editBtn)
                .doOnNext(__ -> editBtn.setEnabled(false))
                .flatMap(__ -> getCropImageStream(tempFileUri.toString()))
                .map(result -> {
                    int resultCode = result.resultCode();
                    if (resultCode == RESULT_OK) {
                        return UCrop.getOutput(result.data());
                    }
                    return null;
                })
                .compose(bindToLifecycle());
    }

    private Observable<? extends Result<AvatarActivity>> getCropImageStream(String uri) {
        UCrop.Options options = new UCrop.Options();
        options.setToolbarColor(ContextCompat.getColor(this, R.color.primary));
        options.setShowCropFrame(true);
        options.setMaxBitmapSize(1024 * 1024 * 1024);
        options.setHideBottomControls(true);
        options.withAspectRatio(1, 1);
        UCrop uCrop = UCrop.of(
                Uri.parse(uri),
                tempFileUri)
                .withOptions(options);
        return RxActivityResult.on(this)
                .startIntent(uCrop.getIntent(getApplicationContext()));
    }

    private Observable<RxCamera> getRecaptureStream(Observable<RxCamera> observable) {
        return RxView.clicks(recaptureBtn)
                .flatMap(__ -> observable)
                .compose(bindToLifecycle());
    }

    private Observable<RxCameraData> getCaptureStream(Observable<RxCamera> observable) {
        return RxView.clicks(captureBtn)
                .doOnNext(__ -> {
                    captureBtn.setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);
                })
                .flatMap(__ -> observable)
                .flatMap(rxCamera ->
                        rxCamera.request().takePictureRequest(
                                false,
                                () -> Log.d(TAG, "captured"),
                                ImageUtil.getScreenDimens(this)[0],
                                ImageUtil.getScreenDimens(this)[1],
                                ImageFormat.JPEG,
                                true).subscribeOn(Schedulers.io()))
                .compose(bindToLifecycle());
    }

    private Observable<RxCamera> checkPermissionAndLaunchCamera() {
        return RxPermissions.getInstance(this)
                .request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .doOnNext(granted -> {
                    if (!granted)
                        Toast.makeText(AvatarActivity.this, "", Toast.LENGTH_SHORT).show();
                })
                .filter(granted -> granted)
                .flatMap(__ -> getCameraPreviewObservable(CameraUtil.getRxCameraConfig(true, this)));
    }

    private Observable<RxCamera> getCameraPreviewObservable(RxCameraConfig config) {
        return RxCamera.open(this, config)
                .flatMap(rxCamera -> rxCamera.bindTexture(textureView))
                .flatMap(RxCamera::startPreview);
    }

    @Override
    protected Boolean requiredLoggedIn() {
        return true;
    }

}