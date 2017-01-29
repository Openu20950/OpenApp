package com.openu.a2017_app1.screens;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.openu.a2017_app1.R;
import com.openu.a2017_app1.models.Place;
import com.openu.a2017_app1.models.Recommendation;

import java.util.List;

/**
 * Created by Emil on 1/29/2017.
 */

public class RecommendationsListViewAdapter  extends ArrayAdapter<Recommendation> {
    // Declare Variables
    Context context;
    LayoutInflater inflater;
    List<Recommendation> reclist;
    //private SparseBooleanArray mSelectedItemsIds;

    public RecommendationsListViewAdapter(Context context, int resourceId,
                                    List<Recommendation> recommendationList) {
        super(context, resourceId, recommendationList);
        //mSelectedItemsIds = new SparseBooleanArray();
        this.context = context;
        this.reclist = recommendationList;
        inflater = LayoutInflater.from(context);
    }

    private class ViewHolder {
        //TextView recnum;
        TextView title;
        TextView description;
        ImageView flag;
    }

    public View getView(int position, View view, ViewGroup parent) {
        final RecommendationsListViewAdapter.ViewHolder holder;
        if (view == null) {
            holder = new RecommendationsListViewAdapter.ViewHolder();
            view = inflater.inflate(R.layout.recommendation_listview_item, null);
            // Locate the TextViews in listview_item.xml
            //holder.recnum = (TextView) view.findViewById(R.id.recnum);
            holder.title = (TextView) view.findViewById(R.id.title);
            holder.description = (TextView) view.findViewById(R.id.description);
            // Locate the ImageView in listview_item.xml
            //holder.flag = (ImageView) view.findViewById(R.id.flag);
            view.setTag(holder);
        } else {
            holder = (RecommendationsListViewAdapter.ViewHolder) view.getTag();
        }
        // Capture position and set to the TextViews

        holder.title.setText(reclist.get(position).getTitle());
        holder.description.setText(reclist.get(position).getDescription());
        //holder.recnum.setText("" + reclist.get(position).getRecommendations().size());
        // Capture position and set to the ImageView
        //holder.flag.setImageResource(placelist.get(position).getFlag());
        return view;
    }

    /*@Override
    public void remove(Place object) {
        placelist.remove(object);
        notifyDataSetChanged();
    }*/

    public List<Recommendation> getPlaces() {
        return reclist;
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
