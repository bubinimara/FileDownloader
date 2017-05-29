package io.github.bubinimara.filedownloadersample;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by davide on 29/05/17.
 */

public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public interface ItemAdapterListener{
        void onItemClicked(Item item);
    }

    private final ItemAdapterListener listener;
    private Item item;

    @BindView(R.id.tvItemTitle) TextView tvItemTitle;

    public ItemHolder(View itemView, ItemAdapterListener listener) {
        super(itemView);
        itemView.setOnClickListener(this);
        this.listener = listener;
        ButterKnife.bind(this, itemView);
    }

    public ItemAdapterListener getListener() {
        return listener;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
        tvItemTitle.setText(item.getTitle());
    }

    @Override
    public void onClick(View v) {
/*
        if(getAdapterPosition() == RecyclerView.NO_POSITION | listener == null)
            return;
*/

        listener.onItemClicked(item);
    }
}
