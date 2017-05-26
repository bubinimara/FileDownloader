package io.github.bubinimara.filedownloader;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 */
public class DownloadIntentService extends IntentService {
    public DownloadIntentService() {
        super("DownloadIntentService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startDownloadService(Context context, Intent intent) {
        intent.setClass(context,DownloadIntentService.class);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            DownloadFileManager.onReceive(this,intent);
        }
    }
}
