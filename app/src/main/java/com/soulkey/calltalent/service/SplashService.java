package com.soulkey.calltalent.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import com.soulkey.calltalent.App;
import com.soulkey.calltalent.api.network.IHttpManager;
import com.soulkey.calltalent.domain.model.SettingModel;
import com.soulkey.calltalent.ui.BaseActivity;
import com.soulkey.calltalent.utils.image.ImageUtil;

import java.io.File;

import javax.inject.Inject;

import rx.Subscription;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public final class SplashService extends IntentService {
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_DOWNLOAD = "com.soulkey.calltalent.service.action.DOWNLOAD";
    private static final String PARAM_IMAGE_URI = "com.soulkey.calltalent.service.extra.PARAM_IMAGE_URI";

    public static final String PARAM_IMAGE_STORED_RESULT = "com.soulkey.calltalent.service.extra.PARAM_IMAGE_STORED_RESULT";

    @Inject
    IHttpManager httpManager;
    @Inject
    SettingModel settingModel;
    String currUri;

    public SplashService() {
        super("SplashService");
    }

    public static void startActionDownloadImage(Context context, String uri) {
        Intent intent = new Intent(context, SplashService.class);
        intent.setAction(ACTION_DOWNLOAD);
        intent.putExtra(PARAM_IMAGE_URI, uri);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        injectComponent();
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_DOWNLOAD.equals(action)) {
                final String imageUri = intent.getStringExtra(PARAM_IMAGE_URI);
                final File file = new File(getCacheDir() + "/splash.jpg");

                Subscription subscription = dealWithSplashImage(imageUri, file);
            }
        }
    }

    private Subscription dealWithSplashImage(String imageUri, File file) {
        return
                httpManager.getSplashImageUrl(imageUri).withLatestFrom(
                        settingModel.getLastSavedSplashRemoteUri(), (curr, prev) -> {
                            Boolean same = curr.equals(prev);
                            if (!same) {
                                settingModel.saveSplashRemoteUri(curr);
                                currUri = curr;
                                return true;
                            }
                            return false;
                        })
                        .flatMap(needChange -> {
                            if (needChange) {
                                return httpManager.fetchImageByUrl(currUri);
                            } else {
                                throw new AssertionError("no need to change splash");
                            }
                        })
                        .map(bitmap -> ImageUtil.bitmap2file(bitmap, file, Bitmap.CompressFormat.JPEG))
                        .subscribe(this::sendOutputResult, err -> sendOutputResult(false));
    }


    private void injectComponent() {
        App.from(this).getAppComponent().inject(this);
    }

    private void sendOutputResult(Boolean result) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(BaseActivity.SplashReceiver.PARAM_RECEIVED_STORED_IMAGE_URI);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        if (result) {
            broadcastIntent.putExtra(PARAM_IMAGE_STORED_RESULT, true);
        } else {
            broadcastIntent.putExtra(PARAM_IMAGE_STORED_RESULT, false);
        }
        sendBroadcast(broadcastIntent);
    }
}
