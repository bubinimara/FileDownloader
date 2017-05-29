package io.github.bubinimara.filedownloader;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

import java.io.File;

/**
 * Created by davide on 20/01/17.
 */

public class DownloadFileManager {
    /*
     * String action that will be broadcast if the download is done
     */
    public static final String ACTION_DONE = "io.github.bubinimara.filedownloader.ACTION_DONE";

    /*
     * String action that will be broadcast if the download fail
     */
    public static final String ACTION_FAIL = "io.github.bubinimara.filedownloader.ACTION_FAIL";



    /**
     * Useful method to create an intent to receive broadcasts events
     * @return the intentFilter to catch all events fired by the DownlaodFileManager
     */
    public static IntentFilter createIntentFilter(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_DONE);
        intentFilter.addAction(ACTION_FAIL);
        return intentFilter;
    }

    /**
     * The intent received by LocalBroadcast can be use this method to know if the download is done or fail
     *
     * @param intent the Intent returned ny the receiver
     * @return true if the download fail, false otherwise
     */
    public static boolean hasError(@NonNull Intent intent){
        return ACTION_FAIL.equalsIgnoreCase(intent.getAction());
    }


    /**
     * Download the file url passed as parameter
     * Filename will be taken from the url
     *
     * Require permissions:
     *  (Manifest.permission.WRITE_EXTERNAL_STORAGE)
     *
     * @param context the application context
     * @param url the url to download
     * @return true on success, false otherwise
     *
     */
    //@RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public static boolean downloadFile(Context context,String url){
        try {
            Uri uri = Uri.parse(url);
            String path = uri.getPath();
            int indexOf = path.lastIndexOf('/');

            String filename = path.substring(indexOf + 1);

            return downloadFile(context,url,filename);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Download the file url and launch the action view
     * The caller is notified by LocalBroadcast intent.
     *
     * @param context the application context
     * @param url the file url to donwload
     * @param filename the filename where to store the download
     * @return true on success, false otherwise
     *
     * Require permissions:
     *  (Manifest.permission.WRITE_EXTERNAL_STORAGE)
     */
    public static boolean downloadFile(Context context,String url,String filename){
        try {
            Uri destinationUri = DownloadFile.createUri(filename);

            DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationUri(destinationUri);
            request.setTitle(filename);

            long enqueue = dm.enqueue(request);
            DownloadStore.storeInfo(context,new DownloadInfo(enqueue,destinationUri));
        } catch (Exception e) {
            DownloadStore.removeInfo(context);
            return false;
        }
        return true;
    }

    public static void onReceive(Context context, Intent intent) {
        DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        String action = intent.getAction();
        long downloadId = intent.getLongExtra( DownloadManager.EXTRA_DOWNLOAD_ID, 0);
        DownloadInfo downloadInfo = DownloadStore.getInfo(context);

        if (downloadInfo.getId() == downloadId) {
            // handle my download

            DownloadStore.removeInfo(context);
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(downloadId);
                Cursor c = dm.query(query);

                if (c.moveToFirst()) {
                    int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                        openDownloadedFile(context, (downloadInfo.getUri()), dm.getMimeTypeForDownloadedFile(downloadId));
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(ACTION_DONE));
                    } else {
                        // stop to automatically download
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(ACTION_FAIL));
                        dm.remove(downloadInfo.getId());

                    }
                }
            } else {
                // whatever happen stop the download
                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(ACTION_FAIL));
                dm.remove(downloadInfo.getId());
            }
        }
    }

    /**
     * Open the downloaded file
     * @param context the application contex
     * @param uri the file uri
     * @param mimeType the mime type
     */
    private static void openDownloadedFile(@NonNull Context context, Uri uri, String mimeType) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(uri, mimeType);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            openDownloadedFolder(context);
        }
    }

    private static void openDownloadedFolder(@NonNull Context context){
        Intent intent = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    /**
     * File creation
     */
    private static class DownloadFile{
        static final File DIRECTORY = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        private static File createFile(String filename){
            if(!DIRECTORY.exists()) DIRECTORY.mkdir(); // never here

            File file = new File(DIRECTORY,filename);
            if (file.exists())
                file.delete();
            return file;
        }

        private static File getFile(String filename){
            return new File(DIRECTORY,filename);
        }



        static Uri createUri(String filename){
            return Uri.parse("file://"+createFile(filename));
        }

    }


    /**
     * Store data
     */
    private static class DownloadStore {
        private static final String PREF_FILE = "io.github.bubinimara.filedownloader.downloadInfo";
        private static final String KEY_ID = "key_id";
        private static final String KEY_URI = "key_uri";

        static void removeInfo(@NonNull Context context){
            SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE,Context.MODE_PRIVATE);
            sharedPreferences.edit().clear().apply();
        }

        static void storeInfo(@NonNull Context context, DownloadInfo info){
            SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE,Context.MODE_PRIVATE);
            sharedPreferences.edit()
                    .putLong(KEY_ID,info.id)
                    .putString(KEY_URI,info.uri.toString())
                    .apply();
        }

        static DownloadInfo getInfo(@NonNull Context context){
            SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE,Context.MODE_PRIVATE);
            long id = sharedPreferences.getLong(KEY_ID, 0);
            String uri = sharedPreferences.getString(KEY_URI,"");

            return new DownloadInfo(id,Uri.parse(uri));
        }
    }

    /**
     * Data to store
     */
    private static class DownloadInfo{
        private long id;
        private Uri uri;

        DownloadInfo(long id, Uri uri) {
            this.id = id;
            this.uri = uri;
        }

        public long getId() {
            return id;
        }

        public Uri getUri() {
            return uri;
        }
    }
}
