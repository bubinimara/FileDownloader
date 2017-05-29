package io.github.bubinimara.filedownloadersample;

import android.support.annotation.NonNull;

/**
 * Created by davide on 29/05/17.
 */

public class Item {
    private String title;
    private String url;

    public Item(@NonNull String title, @NonNull String url) {
        this.title = title;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
