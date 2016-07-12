package com.soulkey.calltalent.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import com.soulkey.calltalent.App;
import com.soulkey.calltalent.di.component.BaseActivityComponent;
import com.soulkey.calltalent.di.component.DaggerBaseActivityComponent;
import com.soulkey.calltalent.di.module.DomainModule;
import com.soulkey.calltalent.di.module.SplashModule;
import com.soulkey.calltalent.di.module.StorageModule;
import com.soulkey.calltalent.domain.model.SplashModel;
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
    SplashModel splashModel;

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
                splashModel.getSplashImageUrl(imageUri).withLatestFrom(
                        splashModel.getLastSavedSplashRemoteUri(), (curr, prev) -> {
                            Boolean same = curr.equals(prev);
                            if (!same) {
                                splashModel.saveSplashRemoteUri(curr);
                                currUri = curr;
                                return true;
                            }
                            return false;
                        })
                        .flatMap(needChange -> {
                            if (needChange) {
                                return splashModel.fetchImageByUrl(currUri);
                            } else {
                                throw new AssertionError("no need to change splash");
                            }
                        })
                        .map(bitmap -> ImageUtil.bitmap2file(bitmap, file, Bitmap.CompressFormat.JPEG))
                        .subscribe(this::sendOutputResult, err -> sendOutputResult(false));
    }


    private void injectComponent() {
        BaseActivityComponent activityComponent = DaggerBaseActivityComponent.builder()
                .applicationComponent(App.from(this).getAppComponent())
                .storageModule(new StorageModule())
                .domainModule(new DomainModule())
                .build();
        activityComponent.plus(new SplashModule()).inject(this);
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
