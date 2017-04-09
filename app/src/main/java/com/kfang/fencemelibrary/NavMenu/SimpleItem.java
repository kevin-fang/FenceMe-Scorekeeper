package com.kfang.fencemelibrary.NavMenu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kfang.fencemelibrary.R;


public class SimpleItem extends DrawerItem<SimpleItem.ViewHolder> {

    private int selectedItemTextTint;
    private int normalItemTextTint;

    private String title;

    public SimpleItem(String title) {
        this.title = title;
    }

    @Override
    public ViewHolder createViewHolder(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_option, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void bindViewHolder(ViewHolder holder) {
        holder.title.setText(title);
        holder.title.setTextColor(isChecked ? selectedItemTextTint : normalItemTextTint);
    }


    public SimpleItem withSelectedTextTint(int selectedItemTextTint) {
        this.selectedItemTextTint = selectedItemTextTint;
        return this;
    }

    public SimpleItem withTextTint(int normalItemTextTint) {
        this.normalItemTextTint = normalItemTextTint;
        return this;
    }

    static class ViewHolder extends DrawerAdapter.ViewHolder {

        private TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
        }
    }
}