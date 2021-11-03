package com.syahrul.taskreminder;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends ArrayAdapter<Item> {

    public ItemAdapter(Context context, List<Item> items) {
        super(context, 0, items);
    }

    private class ItemViewHolder {
        public TextView title;
        public TextView text;
        public TextView dateHour;
        public TextView dateYear;
        public TextView dateMonth;
        public TextView category;
        public LinearLayout back;
    }
    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_item, parent, false);
        }

        ItemViewHolder viewHolder = (ItemViewHolder)  convertView.getTag();
        if (viewHolder == null){
            viewHolder = new ItemViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.text = (TextView) convertView.findViewById(R.id.text);
            viewHolder.dateHour = (TextView) convertView.findViewById(R.id.dateHour);
            viewHolder.dateMonth = (TextView) convertView.findViewById(R.id.dateMonth);
            viewHolder.dateYear = (TextView) convertView.findViewById(R.id.dateYear);
            viewHolder.category = (TextView) convertView.findViewById(R.id.category);
            viewHolder.back = (LinearLayout) convertView.findViewById(R.id.back);
            convertView.setTag(viewHolder);
        }

        Item item = getItem(position);
        final ArrayList<Category> cat = MainActivity.getCat();
        viewHolder.back.setBackgroundColor(Color.WHITE);
        boolean found = false;
        int i = 0;
        while (i < cat.size()){
            if (item.getCategory().equals(cat.get(i).getName())){
                found = true;
                int color = cat.get(i).getColor();
                String lighter = "#15" + Integer.toHexString(color).substring(2);
                viewHolder.category.setBackgroundColor(cat.get(i).getColor());
                if (item.getStatus() == Item.Status.DONE){
                    viewHolder.back.setBackgroundColor(Color.parseColor(lighter));
                }
                viewHolder.category.setBackgroundColor(cat.get(0).getColor());
            }
            i++;
        }
        if (!found){
            item.setCategory("none");
            int color = cat.get(0).getColor();
            String lighter = "#15" + Integer.toHexString(color).substring(2);
            if (item.getStatus() == Item.Status.DONE)
                viewHolder.back.setBackgroundColor(Color.parseColor(lighter));
            viewHolder.category.setBackgroundColor(cat.get(0).getColor());
        }
        viewHolder.title.setText(item.getTitle());
        viewHolder.dateHour.setTextColor(Color.parseColor(item.getDateColor()));
        viewHolder.dateMonth.setTextColor(Color.parseColor(item.getDateColor()));
        viewHolder.dateYear.setTextColor(Color.parseColor(item.getDateColor()));
        viewHolder.text.setText(item.getText());
        viewHolder.dateMonth.setText(item.getMonth());
        viewHolder.dateYear.setText(item.getYear());
        viewHolder.dateHour.setText(item.getTime());

        return convertView;

    }
}
