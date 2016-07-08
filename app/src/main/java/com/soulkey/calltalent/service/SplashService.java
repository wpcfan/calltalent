package com.soulkey.calltalent.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import com.soulkey.calltalent.api.network.IHttpManager;
import com.soulkey.calltalent.di.component.DaggerServiceComponent;
import com.soulkey.calltalent.di.component.ServiceComponent;
import com.soulkey.calltalent.di.module.ServiceModule;
import com.soulkey.calltalent.ui.BaseActivity;
import com.soulkey.calltalent.utils.image.ImageUtil;

import java.io.File;

import javax.inject.Inject;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public final class SplashService extends IntentService {
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_DOWNLOAD = "com.soulkey.calltalent.service.action.DOWNLOAD";
    private static final String PARAM_FILE_NAME = "com.soulkey.calltalent.service.extra.PARAM_FILE_NAME";

    public static final String PARAM_STORED_IMAGE_URI = "com.soulkey.calltalent.service.extra.PARAM_STORED_IMAGE_URI";

    @Inject
    IHttpManager httpManager;

    public SplashService() {
        super("SplashService");
        ServiceComponent component = DaggerServiceComponent
                .builder()
                .serviceModule(new ServiceModule())
                .build();
        component.inject(this);
    }

    public static void startActionDownloadImage(Context context, String filename) {
        Intent intent = new Intent(context, SplashService.class);
        intent.setAction(ACTION_DOWNLOAD);
        intent.putExtra(PARAM_FILE_NAME, filename);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_DOWNLOAD.equals(action)) {
                final String fileName = intent.getStringExtra(PARAM_FILE_NAME);
                final File file = new File(getCacheDir() + fileName);
                final String URL = "http://www.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1&mkt=en-US";
                httpManager
                        .getSplashImageUrl(URL)
                        .flatMap(remote_url -> httpManager.fetchImageByUrl(remote_url))
                        .map(bitmap -> ImageUtil.bitmap2file(bitmap, file, Bitmap.CompressFormat.JPEG))
                        .subscribe(result -> sendOutputResult(file, result));
            }
        }
    }

    private void sendOutputResult(File file, Boolean result) {
        if (result) {
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(BaseActivity.SplashReceiver.PARAM_RECEIVED_STORED_IMAGE_URI);
            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            broadcastIntent.putExtra(PARAM_STORED_IMAGE_URI, Uri.fromFile(file).toString());
            sendBroadcast(broadcastIntent);
        }
    }
}
