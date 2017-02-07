package com.openu.a2017_app1.screens;

/**
 * Created by Emil on 1/24/2017.
 */

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.openu.a2017_app1.R;
import com.openu.a2017_app1.models.Place;

public class LocationsListViewAdapter extends ArrayAdapter<Place> {
    // Declare Variables
    Context context;
    LayoutInflater inflater;
    List<Place> placelist;
    //private SparseBooleanArray mSelectedItemsIds;

    public LocationsListViewAdapter(Context context, int resourceId,
                                    List<Place> placelist) {
        super(context, resourceId, placelist);
        //mSelectedItemsIds = new SparseBooleanArray();
        this.context = context;
        this.placelist = placelist;
        inflater = LayoutInflater.from(context);
    }

    private class ViewHolder {
        TextView recnum;
        TextView name;
        TextView category;
        ImageView flag;
    }

    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.location_listview_item, null);
            // Locate the TextViews in listview_item.xml
            holder.recnum = (TextView) view.findViewById(R.id.recnum);
            holder.name = (TextView) view.findViewById(R.id.name);
            holder.category = (TextView) view.findViewById(R.id.category);
            // Locate the ImageView in listview_item.xml
            //holder.flag = (ImageView) view.findViewById(R.id.flag);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Capture position and set to the TextViews

        holder.name.setText(placelist.get(position).getName());
        holder.category.setText(placelist.get(position).getCategory());
        holder.recnum.setText("" + placelist.get(position).getRecommendations().size());
        // Capture position and set to the ImageView
        //holder.flag.setImageResource(placelist.get(position).getFlag());
        return view;
    }

    /*@Override
    public void remove(Place object) {
        placelist.remove(object);
        notifyDataSetChanged();
    }*/

    public List<Place> getPlaces() {
        return placelist;
    }

    /*public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }*/

    /*public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }*/

    /*public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);
        notifyDataSetChanged();
    }*/

    /*public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }*/

/*
    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }
*/
}