package com.example.vlachbear.scorer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.vlachbear.scorer.PlayerData;

import java.util.Comparator;

/**
 * Created by vlachbear on 8/20/15.
 */
public class ScoreAdapter extends ArrayAdapter<PlayerData> {
    Boolean isAZ = false;
    Boolean isHighLow = false;

    public ScoreAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_1);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view =  LayoutInflater.from(getContext()).inflate(R.layout.list_item_name_score, parent, false);
        }
        else {
            view = convertView;
        }

        final PlayerData pd = getItem(position);
        ((TextView)view.findViewById(R.id.listPlayerName)).setText(pd.name);
        ((TextView)view.findViewById(R.id.listPlayerScore)).setText(Integer.toString(pd.total));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), PlayerDetailsActivity.class);
                i.putExtra(Utilities.NAME_KEY, pd.name);
                i.putExtra(Utilities.TOTAL_KEY, pd.total);
                i.putExtra(Utilities.INDEX_KEY,position);
                i.putIntegerArrayListExtra(Utilities.SCORE_ARRAY_KEY, pd.scores);
                getContext().startActivity(i);
            }
        });

        return view;
    }

    public void replaceItem(Integer index, PlayerData newer) {
        PlayerData former = getItem(index);
        former.assign(newer);
    }

    public void clearAllScores(){
        for (int i = 0; i < getCount(); i++) {
            PlayerData pd = getItem(i);
            pd.total = 0;
            pd.scores.clear();
        }
    }

    public void sortAZ() {
        sort(new Comparator<PlayerData>() {
            @Override
            public int compare(PlayerData lhs, PlayerData rhs) {
                return lhs.name.compareTo(rhs.name);
            }
        });
        isAZ = true;
        isHighLow = false;
        notifyDataSetChanged();
    }

    public void sortHighLow() {
        sort(new Comparator<PlayerData>() {
            @Override
            public int compare(PlayerData lhs, PlayerData rhs) {
                return rhs.total.compareTo(lhs.total);
            }
        });
        isAZ = false;
        isHighLow = true;
        notifyDataSetChanged();
    }

    public void sortUndo() {
        sort(new Comparator<PlayerData>() {
            @Override
            public int compare(PlayerData lhs, PlayerData rhs) {
                return lhs.originalIndex.compareTo(rhs.originalIndex);
            }
        });
        isAZ = false;
        isHighLow = false;
        notifyDataSetChanged();
    }

    public void checkSort() {
        if (isAZ) {
            sortAZ();
        } else if (isHighLow) {
            sortHighLow();
        }
    }
}
