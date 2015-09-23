package com.example.vlachbear.scorer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.inputmethodservice.InputMethodService;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.util.ArrayList;


public class PlayerDetailsActivity extends ActionBarActivity {

    EditText nameView;
    TextView totalView;
    Button deleteButton;
    Button undoButton;
    Button editButton;
    Button confirmButton;

    ViewSwitcher viewSwitcher;

    ListView scoresView;
    ListView scoresEditView;

    DetailsAdapter adapter;
    EditAdapter editAdapter;

    PlayerData player;
    Integer index;

    Boolean nameEdited = false;
    Boolean edited = false;
    Boolean deleted = false;

    int focusIndex = Utilities.NO_FOCUS_INDEX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_player_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        player = new PlayerData();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            player.name = bundle.getString(Utilities.NAME_KEY);
            player.total = bundle.getInt(Utilities.TOTAL_KEY);
            player.scores = bundle.getIntegerArrayList(Utilities.SCORE_ARRAY_KEY);
            index = bundle.getInt(Utilities.INDEX_KEY);
        }

        nameView = (EditText) findViewById(R.id.detailsName);
        nameView.setText(player.name);
        nameView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                nameEdited = true;
            }
        });

        totalView = (TextView) findViewById(R.id.detailsTotal);
        totalView.setText(Integer.toString(player.total));

        deleteButton = (Button) findViewById(R.id.detailsDelete);
        editButton = (Button) findViewById(R.id.detailsEdit);
        confirmButton = (Button) findViewById(R.id.detailsConfirm);
        undoButton = (Button) findViewById(R.id.detailsUndo);

        deleteButton.setOnClickListener(buttonListener);
        editButton.setOnClickListener(buttonListener);
        confirmButton.setOnClickListener(buttonListener);
        undoButton.setOnClickListener(buttonListener);

        confirmButton.setVisibility(View.GONE);
        undoButton.setVisibility(View.GONE);

        viewSwitcher = (ViewSwitcher) findViewById(R.id.detailsSwitchLayout);
        scoresView = (ListView) findViewById(R.id.detailsList);
        scoresEditView = (ListView) findViewById(R.id.detailsListEdit);

        adapter = new DetailsAdapter(this);
        adapter.addAll(player.scores);
        scoresView.setAdapter(adapter);

        editAdapter = new EditAdapter(this, totalView);
        editAdapter.addAll(player.scores);
        scoresEditView.setAdapter(editAdapter);

        if (savedInstanceState != null) {
            //screen was rotated
            int childIndex = savedInstanceState.getInt(Utilities.VIEW_DISPLAYED_KEY);
            if (childIndex == Utilities.MAIN_DETAILS_VIEW_INDEX)
                switchViewToMain();
            else {
                if (savedInstanceState.getBoolean(Utilities.NAME_EDITED_KEY, false)) {
                    nameEdited = true;
                    nameView.setText(savedInstanceState.getString(Utilities.NAME_KEY));
                }
                if (savedInstanceState.getBoolean(Utilities.SCORES_EDITED_KEY, false)) {
                    editAdapter.edited = true;
                    ArrayList<Integer> updatedScores = savedInstanceState.getIntegerArrayList(Utilities.SCORE_ARRAY_KEY);
                    editAdapter.clear();
                    editAdapter.addAll(updatedScores);
                    editAdapter.total = savedInstanceState.getInt(Utilities.TOTAL_KEY);
                    totalView.setText(editAdapter.total.toString());
                }
                switchViewToEdit();
                focusIndex = savedInstanceState.getInt(Utilities.FOCUS_KEY, Utilities.NO_FOCUS_INDEX);
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_player_details, menu);
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        int childIndex = viewSwitcher.getDisplayedChild();
        if (childIndex == Utilities.EDIT_DETAILS_VIEW_INDEX) {
            if (nameEdited) {
                savedInstanceState.putBoolean(Utilities.NAME_EDITED_KEY, true);
                savedInstanceState.putString(Utilities.NAME_KEY, nameView.getText().toString());
            }
            if (editAdapter.edited) {
                savedInstanceState.putBoolean(Utilities.SCORES_EDITED_KEY, true);
                ArrayList<Integer> updatedScores = new ArrayList<>();
                for (int i = 0; i < editAdapter.getCount(); i++) {
                    String s = ((EditText)scoresEditView.getChildAt(i).findViewById(R.id.points)).getText().toString();
                    if (s.isEmpty())
                        s = "0";
                    updatedScores.add(Integer.parseInt(s));
                }
                savedInstanceState.putIntegerArrayList(Utilities.SCORE_ARRAY_KEY, updatedScores);
                savedInstanceState.putInt(Utilities.TOTAL_KEY, editAdapter.total);
            }
            // maintains focus on rotation
            if (nameView.hasFocus()) {
                savedInstanceState.putInt(Utilities.FOCUS_KEY, Utilities.NAME_FOCUS_INDEX);
            } else {
                for (int i=0; i<editAdapter.getCount(); i++) {
                    if (scoresEditView.getChildAt(i).findViewById(R.id.points).hasFocus()) {
                        savedInstanceState.putInt(Utilities.FOCUS_KEY, i);
                        break;
                    }
                }
            }
        }
        savedInstanceState.putInt(Utilities.VIEW_DISPLAYED_KEY, childIndex);
    }

    @Override
    public void onWindowFocusChanged (boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        // maintains focus on rotation
        if (hasFocus) {
            if (viewSwitcher.getDisplayedChild() == Utilities.EDIT_DETAILS_VIEW_INDEX) {
                if (focusIndex == Utilities.NO_FOCUS_INDEX) {
                    View wrapper = findViewById(R.id.detailsNameWrapper);
                    wrapper.requestFocus();
                } else if (focusIndex == Utilities.NAME_FOCUS_INDEX) {
                    nameView.requestFocus();
                    nameView.setSelection(nameView.getText().length());
                } else {
                    EditText focusedView = (EditText) scoresEditView.getChildAt(focusIndex).findViewById(R.id.points);
                    focusedView.requestFocus();
                    focusedView.setSelection(focusedView.getText().length());
                }
            }
        }
    }

    private void switchViewToEdit() {
        editButton.setVisibility(View.GONE);
        confirmButton.setVisibility(View.VISIBLE);
        undoButton.setVisibility(View.VISIBLE);

        viewSwitcher.setDisplayedChild(Utilities.EDIT_DETAILS_VIEW_INDEX);

        nameView.setEnabled(true);
        nameView.setFocusable(true);
        nameView.setFocusableInTouchMode(true);
    }

    private void switchViewToMain() {
        editButton.setVisibility(View.VISIBLE);
        confirmButton.setVisibility(View.GONE);
        undoButton.setVisibility(View.GONE);

        viewSwitcher.setDisplayedChild(Utilities.MAIN_DETAILS_VIEW_INDEX);

        nameView.setEnabled(false);
        nameView.setFocusable(false);
        nameView.setFocusableInTouchMode(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                handleBackUp();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        handleBackUp();
    }

    private void handleBackUp() {
        if (viewSwitcher.getDisplayedChild() == 0) {
            //main view visible
            goToMain();
        } else {
            //edit view visible
            if (editAdapter.edited || nameEdited) {
                //user has made changes. allow to save.
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PlayerDetailsActivity.this);

                alertDialogBuilder.setTitle(R.string.save_changes_title);
                alertDialogBuilder.setMessage(R.string.save_changes_message);
                alertDialogBuilder
                        .setPositiveButton("Save",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if (nameEdited) {
                                            player.name = nameView.getText().toString();
                                        }
                                        if (editAdapter.edited) {
                                            player.total = 0;
                                            for (int i = 0; i < editAdapter.getCount(); i++) {
                                                String s = ((EditText)scoresEditView.getChildAt(i).findViewById(R.id.points)).getText().toString();
                                                if (s.isEmpty())
                                                    s = "0";
                                                Integer updatedScore = Integer.parseInt(s);
                                                player.total += updatedScore;
                                                player.scores.set(i, updatedScore);
                                            }
                                        }
                                        edited = true;
                                        goToMain();
                                    }
                                })
                        .setNegativeButton("Ignore",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                        goToMain();
                                    }
                                });

                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            } else {
                goToMain();
            }
        }
    }

    View.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.detailsDelete) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PlayerDetailsActivity.this);

                alertDialogBuilder.setTitle(R.string.edit_delete_title);
                alertDialogBuilder.setMessage(R.string.edit_delete_message);
                alertDialogBuilder
                        .setPositiveButton("Delete",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        deleted = true;
                                        goToMain();
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {

                                        dialog.cancel();
                                    }
                                });

                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            } else if (v.getId() == R.id.detailsEdit) {
                switchViewToEdit();
            } else if (v.getId() == R.id.detailsConfirm) {
                if (nameEdited) {
                    player.name = nameView.getText().toString();
                    edited = true;
                    nameEdited = false;
                }
                if (editAdapter.edited) {
                    edited = true;
                    editAdapter.edited = false;
                    player.total = 0;
                    for (int i = 0; i < editAdapter.getCount(); i++) {
                        String s = ((EditText)scoresEditView.getChildAt(i).findViewById(R.id.points)).getText().toString();
                        if (s.isEmpty())
                            s = "0";
                        Integer updatedScore = Integer.parseInt(s);
                        player.total += updatedScore;
                        player.scores.set(i, updatedScore);
                    }
                    adapter.clear();
                    adapter.addAll(player.scores);
                    adapter.notifyDataSetChanged();

                    totalView.setText(player.total.toString());
                }
                switchViewToMain();
            } else if (v.getId() == R.id.detailsUndo) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PlayerDetailsActivity.this);

                alertDialogBuilder.setTitle(R.string.edit_undo_title);
                alertDialogBuilder.setMessage(R.string.edit_undo_message);
                alertDialogBuilder
                        .setPositiveButton("Continue",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        nameView.setText(player.name);
                                        totalView.setText(player.total.toString());
                                        nameEdited = false;
                                        editAdapter.edited = false;

                                        switchViewToMain();
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {

                                        dialog.cancel();
                                    }
                                });

                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        }
    };

    public void goToMain() {
        Intent i = NavUtils.getParentActivityIntent(this);

        if (edited || deleted) {
            i.putExtra(Utilities.FROM_EDIT_KEY, true);
            i.putExtra(Utilities.INDEX_KEY, index);
            if (!deleted) {
                i.putExtra(Utilities.DELETED_KEY, false);
                i.putExtra(Utilities.NAME_KEY, player.name);
                i.putExtra(Utilities.TOTAL_KEY, player.total);
                i.putIntegerArrayListExtra(Utilities.SCORE_ARRAY_KEY, player.scores);
                i.putExtra(Utilities.INDEX_KEY, index);
            } else {
                i.putExtra(Utilities.DELETED_KEY, true);
            }

        }
        NavUtils.navigateUpTo(this, i);
    }
}
