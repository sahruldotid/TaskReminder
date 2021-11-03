package com.syahrul.taskreminder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class CategoryAdapter extends ArrayAdapter<Category> {

    private class CatHolder {
        public TextView name;
        public TextView color;
    }
    public CategoryAdapter(Context context, List<Category> categories){
        super(context, 0, categories);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
       if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_category, parent, false);
        }
        CategoryAdapter.CatHolder viewHolder = (CatHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new CategoryAdapter.CatHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.title);
            viewHolder.color = (TextView) convertView.findViewById(R.id.color);
            convertView.setTag(viewHolder);
        }
        Category cat = getItem(position);
        if (cat != null) {
            viewHolder.name.setText(cat.getName());
            viewHolder.color.setBackgroundColor(cat.getColor());
        }
        return convertView;
    }
}
