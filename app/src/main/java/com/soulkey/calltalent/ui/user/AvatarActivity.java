package com.soulkey.calltalent.ui.user;

import android.Manifest;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
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
import com.ragnarok.rxcamera.request.Func;
import com.soulkey.calltalent.R;
import com.soulkey.calltalent.di.component.ApplicationComponent;
import com.soulkey.calltalent.ui.BaseActivity;
import com.soulkey.calltalent.ui.UIHelper;
import com.soulkey.calltalent.ui.auth.LoginParams;
import com.soulkey.calltalent.utils.image.CameraUtil;
import com.soulkey.calltalent.utils.image.ImageUtil;
import com.squareup.picasso.RequestCreator;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import icepick.State;
import rx.Observable;
import rx.Subscription;
import rx_activity_result.Result;
import rx_activity_result.RxActivityResult;

import static solid.collectors.ToArray.toArray;
import static solid.collectors.ToArrays.toBytes;
import static solid.stream.Primitives.box;
import static solid.stream.Stream.of;

public class AvatarActivity extends BaseActivity {
    private final String TAG = AvatarActivity.class.getSimpleName();
    @State
    Byte[] cameraData;
    @State
    String uid;
    Matrix rotationMatrix;

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
        Map<String, String> params = new HashMap<>();
        params.put(LoginParams.PARAM_KEY_UID.getValue(), uid);

        //Make the camera preview a hot observable
        Observable<RxCamera> observable = checkPermissionAndLaunchCamera()
                .publish()
                .refCount();

        initCameraPreview(observable);
        dealWithCapture(observable);
        dealWithRecapture(observable);
        dealWithEdit();
        dealWithSave(dialog, params);
    }

    @NonNull
    private ProgressDialog configProgressDialog() {
        ProgressDialog dialog = new ProgressDialog(this);
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

    private void dealWithSave(ProgressDialog dialog, Map<String, String> params) {
        Subscription subSave = getSaveImageStream(dialog)
                .subscribe(remoteUri -> {
                    dialog.dismiss();
                    progressBar.setVisibility(View.GONE);
                    params.put(LoginParams.PARAM_KEY_AVATAR_URI.getValue(), remoteUri);
                    UIHelper.launchActivity(
                            AvatarActivity.this, CreateUserProfileActivity.class, params);
                    finish();
                });
        getSubsCollector().add(subSave);
    }

    private void dealWithEdit() {
        Subscription subEdit = getEditImageStream()
                .subscribe(result -> {
                    editBtn.setEnabled(true);
                    result.into(imagePreview);
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
                .subscribe(rxCameraData -> {
                    cameraData = box(rxCameraData.cameraData).collect(toArray(Byte.class));
                    rotationMatrix = rxCameraData.rotateMatrix;
                    captureBtn.setVisibility(View.GONE);
                    textureView.setVisibility(View.GONE);
                    imagePreview.setVisibility(View.VISIBLE);
                    recaptureBtn.setVisibility(View.VISIBLE);
                    editBtn.setVisibility(View.VISIBLE);
                    saveBtn.setVisibility(View.VISIBLE);
                    imagePreview.setImageBitmap(createBitmap(rxCameraData));
                    progressBar.setVisibility(View.GONE);
                });
        getSubsCollector().add(subCapture);
    }

    private Bitmap createBitmap(RxCameraData data) {
        Bitmap bitmap = ImageUtil.decodeFile(data.cameraData);
        return Bitmap.createBitmap(
                bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), data.rotateMatrix, false);
    }

    private Observable<String> getSaveImageStream(ProgressDialog dialog) {
        return RxView.clicks(saveBtn)
                .doOnNext(__ -> {
                    saveBtn.setEnabled(false);
                    recaptureBtn.setEnabled(false);
                    dialog.show();
                })
                .flatMap(__ -> userModel.uploadAvatar(
                        of(cameraData).collect(toBytes()), this.uid))
                .compose(bindToLifecycle());
    }

    private Observable<RequestCreator> getEditImageStream() {
        return RxView.clicks(editBtn)
                .doOnNext(__ -> editBtn.setEnabled(false))
                .flatMap(__ -> ImageUtil.saveImageToGallery(
                        getContentResolver(), of(cameraData).collect(toBytes()), rotationMatrix))
                .flatMap(this::getCropImageStream)
                .map(result -> {
                    int resultCode = result.resultCode();
                    if (resultCode == RESULT_OK) {
                        return UCrop.getOutput(result.data());
                    }
                    return null;
                })
                .flatMap(uri -> userModel.loadImageFrom(uri))
                .compose(bindToLifecycle());
    }

    private Observable<? extends Result<AvatarActivity>> getCropImageStream(String uri) {
        UCrop.Options options = new UCrop.Options();
        options.setToolbarColor(ContextCompat.getColor(this, R.color.primary));
        options.setShowCropFrame(true);
        options.setMaxBitmapSize(1024 * 1024 * 1024);
        UCrop uCrop = UCrop.of(
                Uri.parse(uri),
                Uri.fromFile(new File(getCacheDir(), "avatar.jpg")))
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
                                (Func) () -> Log.d(TAG, "captured"),
                                CameraUtil.X_AXIS,
                                CameraUtil.Y_AXIS,
                                ImageFormat.JPEG,
                                true))
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
                .flatMap(__ -> getCameraPreviewObservable(CameraUtil.getRxCameraConfig(true)));
    }

    private Observable<RxCamera> getCameraPreviewObservable(RxCameraConfig config) {
        return RxCamera.open(this, config)
                .flatMap(rxCamera -> rxCamera.bindTexture(textureView))
                .flatMap(RxCamera::startPreview);
    }

    @Override
    protected void injectComponent(ApplicationComponent component) {
        component.inject(this);
    }

    @Override
    protected Boolean requiredLoggedIn() {
        return true;
    }

}