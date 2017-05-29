package io.github.bubinimara.filedownloadersample;

import android.app.Dialog;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.github.bubinimara.filedownloader.DownloadFileFragment;

/**
 * A placeholder fragment containing a simple view.
 */
public class DownloadFragment extends DownloadFileFragment{

    private Unbinder unbinder;

    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.progressBar) View prgressBar;

    private ItemHolder.ItemAdapterListener itemAdapterListener = new ItemHolder.ItemAdapterListener() {
        @Override
        public void onItemClicked(Item item) {
            showProgress(true);
            downloadFile(item.getUrl());
        }
    };


    private ItemAdapter itemAdapter;
    private Dialog dialog;

    private static final Item[] items = {
            new Item("Octocat Image","https://assets-cdn.github.com/images/modules/logos_page/Octocat.png"),
            new Item("Empty URL",""),
            new Item("Error URL","https://assets-cdn.github.com/images/modules/logos_page/Octocat.png_nofound")

    };

    public DownloadFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        itemAdapter = new ItemAdapter(getContext(),itemAdapterListener);
        itemAdapter.setItem(items);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_download, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this,view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(itemAdapter);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        dismissDialog();
    }

    private void dismissDialog(){
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }

    private void showDialogWarning(){
        dismissDialog();
        MaterialStyledDialog.Builder builder = new MaterialStyledDialog.Builder(getContext());
        builder.setDescription(R.string.error_dialog_download)
                .setIcon(R.drawable.ic_warning)
                .setPositiveText(R.string.dialog_ok);

        dialog = builder.build();
        dialog.show();
    }

    @Override
    protected void onDownloadFail(String url, int error) {
        super.onDownloadFail(url,error);
        showProgress(false);
        showDialogWarning();
    }

    @Override
    protected void onDownloadDone(String url) {
        super.onDownloadDone(url);
        showProgress(false);
    }

    private void showProgress(boolean show){
        if(show){
            prgressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }else{
            prgressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}
