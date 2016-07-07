package com.soulkey.calltalent.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import com.soulkey.calltalent.api.network.IHttpManager;
import com.soulkey.calltalent.di.component.DaggerServiceComponent;
import com.soulkey.calltalent.di.component.ServiceComponent;
import com.soulkey.calltalent.di.module.HttpModule;
import com.soulkey.calltalent.utils.image.ImageUtil;

import java.io.File;

import javax.inject.Inject;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class SplashService extends IntentService {
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_DOWNLOAD = "com.soulkey.calltalent.service.action.DOWNLOAD";

    private static final String PARAM_FILE_NAME = "com.soulkey.calltalent.service.extra.PARAM_FILE_NAME";
    private static final String PARAM_REMOTE_IMAGE_URI = "com.soulkey.calltalent.service.extra.PARAM_REMOTE_IMAGE_URI";

    @Inject
    IHttpManager httpManager;

    public SplashService() {
        super("SplashService");
        ServiceComponent component = DaggerServiceComponent.builder().httpModule(new HttpModule()).build();
        component.inject(this);
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionDownloadImage(Context context, String filename, String remoteUrl) {
        Intent intent = new Intent(context, SplashService.class);
        intent.setAction(ACTION_DOWNLOAD);
        intent.putExtra(PARAM_FILE_NAME, filename);
        intent.putExtra(PARAM_REMOTE_IMAGE_URI, remoteUrl);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_DOWNLOAD.equals(action)) {
                final String fileName = intent.getStringExtra(PARAM_FILE_NAME);
                final String remote_url = intent.getStringExtra(PARAM_REMOTE_IMAGE_URI);
                httpManager.fetchImageByUrl(remote_url).subscribe(bitmap -> {
                    ImageUtil.bitmap2file(bitmap, new File(getCacheDir() + fileName), Bitmap.CompressFormat.JPEG);
                });
            }
        }
    }

}
