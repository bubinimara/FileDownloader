package io.github.bubinimara.filedownloader;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by davide on 29/05/17.
 */

public class DownloadFileFragment extends Fragment {
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 2;
    public static final String EXTRA_SAVE_URL = "DownloadFileFragment.EXTRA_SAVE_URL";

    public static final int ERROR_PERMISSION_NOT_GRANTED = 1;
    public static final int ERROR_DOWNLOAD_FAIL = 2;
    public static final int ERROR_DOWNLOAD_NOT_STARTED = 3;

    public interface DownloadFileListener{
        void onDownloadFail(String url,int error);
        void onDownloadDone(String url);
    }

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(DownloadFileManager.hasError(intent)){
                onDownloadFail(url,ERROR_DOWNLOAD_FAIL);
            }else {
                onDownloadDone(url);
            }
        }
    };


    private String url;
    private DownloadFileListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof DownloadFileListener){
            listener = (DownloadFileListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        restoreState(savedInstanceState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        restoreState(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(EXTRA_SAVE_URL,url);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(downloadReceiver,DownloadFileManager.createIntentFilter());
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(downloadReceiver);
    }

    private void restoreState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState!=null) {
            url = savedInstanceState.getString(EXTRA_SAVE_URL);
        }
    }

    public void downloadFile(String url) {
        this.url = url;
        downloadFile();
    }

    private void downloadFile() {
        if(!haveStoragePermission())
            return; // request for permission

        if(!DownloadFileManager.downloadFile(getContext(),url)){
            // listener download error
            onDownloadFail(url,ERROR_DOWNLOAD_NOT_STARTED);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode== REQUEST_WRITE_EXTERNAL_STORAGE && grantResults.length >0){
            if(grantResults[0]== PackageManager.PERMISSION_GRANTED) {
                //you have the permission now.
                downloadFile();
            }else{
                onDownloadFail(url,ERROR_PERMISSION_NOT_GRANTED);
            }
        }
    }
    public  boolean haveStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getContext().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
                return false;
            }
        }

        return true;
    }

    protected void onDownloadFail(String url,int error){
        if(listener!=null){
            listener.onDownloadFail(url,error);
        }
    }
    protected void onDownloadDone(String url){
        if(listener!=null){
            listener.onDownloadDone(url);
        }

    }
}
