package com.example.vlachbear.scorer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by vlachbear on 8/28/15.
 */
public class DetailsAdapter extends ArrayAdapter<Integer> {

    Integer partialTotal = 0;
    public DetailsAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_1);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_item_score_detail, parent, false);
        } else {
            view = convertView;
        }

        if (position == 0) {
            view.findViewById(R.id.addedScore).setVisibility(View.GONE);
            view.findViewById(R.id.horizontalLine).setVisibility(View.GONE);
            partialTotal = getItem(position);
            ((TextView)view.findViewById(R.id.newTotal)).setText(Integer.toString(partialTotal));
        } else {
            Integer addedScore = getItem(position);
            String addString;
            if (addedScore < 0) {
                addString = "- " + (addedScore*-1);
            } else {
                addString = "+ " + addedScore;
            }
            ((TextView)view.findViewById(R.id.addedScore)).setText(addString);
            partialTotal += addedScore;
            ((TextView)view.findViewById(R.id.newTotal)).setText(Integer.toString(partialTotal));
        }

        return view;
    }
}
