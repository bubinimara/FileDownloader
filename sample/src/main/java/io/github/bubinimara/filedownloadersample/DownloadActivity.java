package io.github.bubinimara.filedownloadersample;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.bubinimara.filedownloader.DownloadFileManager;

/**
 * Demo: how to use DownloadFileManager.class
 */
public class DownloadActivity extends AppCompatActivity {
    private static final String URL_DOWNLOAD_OCTOCAT = "https://assets-cdn.github.com/images/modules/logos_page/Octocat.png";

    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1;


    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.tvMain) TextView tvMain;
    @BindView(R.id.progressBar) View progressBar;

    private String url = URL_DOWNLOAD_OCTOCAT;

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(tvMain==null)// sanity check
                return;
            showProgress(false);
            if(DownloadFileManager.hasError(intent)){
                tvMain.setText(R.string.download_failed);
            }else {
                tvMain.setText(R.string.download_done);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(downloadReceiver,DownloadFileManager.createIntentFilter());
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(downloadReceiver);
    }

    private void downloadFile() {
        showProgress(true);
        if(!DownloadFileManager.downloadFile(this,url)){
            Snackbar.make(fab,R.string.snackbar_download_not_started,Snackbar.LENGTH_LONG).show();
            showProgress(false);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_WRITE_EXTERNAL_STORAGE && grantResults.length >0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
            //you have the permission now.
            downloadFile();
        }
    }

    @OnClick(R.id.fab) void updateClick(){
        if(haveStoragePermission()) {
            downloadFile();
        }
    }
    public  boolean haveStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
                return false;
            }
        }

        return true;
    }

    private void showProgress(boolean isSHow) {
        if(tvMain == null)
            return;

        if(isSHow){
            progressBar.setVisibility(View.VISIBLE);
            tvMain.setVisibility(View.GONE);
            fab.hide();
        }else{
            fab.show();
            progressBar.setVisibility(View.GONE);
            tvMain.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_download, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
