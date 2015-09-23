package com.example.vlachbear.scorer;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by vlachbear on 9/7/15.
 */
public class EditAdapter extends ArrayAdapter<Integer> {

    Boolean edited = false;
    Context context;
    TextView totalView;
    Integer total;

    public EditAdapter(Context c, TextView tv) {
        super(c, android.R.layout.simple_list_item_1);
        context = c;
        totalView = tv;
        total = Integer.parseInt(tv.getText().toString());
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_item_score_edit, parent, false);
        } else {
            view = convertView;
        }

        TextView roundView = (TextView) view.findViewById(R.id.round);
        roundView.setText("Round " + Integer.toString(position + 1));

        final EditText pointsView = (EditText) view.findViewById(R.id.points);
        pointsView.setText(Integer.toString(getItem(position)));

        pointsView.addTextChangedListener(new TextWatcher() {
            int prevVal = getItem(position);

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable e) {
                Log.d("edit adapter", "edited");
                edited = true;
                String s = e.toString();
                int val;
                if (s.isEmpty() || s.equals("-")) {
                    val = 0;
                } else
                    val = Integer.parseInt(s);
                total = total + val - prevVal;
                prevVal = val;
                totalView.setText(total.toString());
            }
        });

        pointsView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Log.d("edit adapter", "pointsView click");
                    if (pointsView.getText().toString().equals("0")) {
                        pointsView.setText("");
                    }
                } else {
                    String s = pointsView.getText().toString();
                    if (s.isEmpty() || s.equals("-")) {
                        pointsView.setText("0");
                    }
                }
            }
        });

        roundView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pointsView.requestFocus();
                pointsView.setSelection(pointsView.getText().length());
                final InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(pointsView, InputMethodManager.SHOW_FORCED);
            }
        });

        return view;
    }
}

