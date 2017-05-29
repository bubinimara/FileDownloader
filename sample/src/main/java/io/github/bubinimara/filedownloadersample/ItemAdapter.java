package io.github.bubinimara.filedownloadersample;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by davide on 29/05/17.
 */

public class ItemAdapter extends RecyclerView.Adapter<ItemHolder> {
    private final LayoutInflater layoutInflater;
    private final ItemHolder.ItemAdapterListener itemAdapterListener;

    private ArrayList<Item> data = new ArrayList<>();

    public ItemAdapter(@NonNull Context context, ItemHolder.ItemAdapterListener itemAdapterListener) {
        this.layoutInflater = LayoutInflater.from(context);
        this.itemAdapterListener = itemAdapterListener;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemHolder(layoutInflater.inflate(R.layout.row_item,parent,false),itemAdapterListener);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        holder.setItem(getItem(position));
    }

    private Item getItem(int position) {
        return data.get(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setItem(@NonNull  Item[] items){
        data.clear();
        data.addAll(Arrays.asList(items));
    }
}
