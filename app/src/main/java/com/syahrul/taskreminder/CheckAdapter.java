package com.syahrul.taskreminder;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.List;

public class CheckAdapter extends ArrayAdapter<Category> {
    private class checkHolder {
        public CheckBox name;
    }
    public CheckAdapter(Context context, List<Category> categories){
        super(context, 0, categories);
    }

    @NonNull
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_check_category, parent, false);
        }
        checkHolder viewHolder = (checkHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new checkHolder();
            viewHolder.name = (CheckBox) convertView.findViewById(R.id.checkCat2);
            convertView.setTag(viewHolder);
        }

        final Category cat = getItem(position);
        viewHolder.name.setText(cat.getName());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            viewHolder.name.setButtonTintList(ColorStateList.valueOf(cat.getColor()));
        }
        return convertView;
    }
}
