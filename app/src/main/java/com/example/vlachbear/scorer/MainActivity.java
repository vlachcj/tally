package com.example.vlachbear.scorer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.google.gson.Gson;

public class MainActivity extends ActionBarActivity {

    MenuItem clearScores;
    MenuItem clearAll;
    MenuItem AZ;
    MenuItem HighLow;
    MenuItem Undo;

    //TOTALS DISPLAY
    ScoreData scoreData;
    ScoreAdapter scoreHolder;
    ListView scoreView;
    Button updateScoresButton;

    //UPDATE SCORES
    Integer index = 0;
    Integer updateIndex = 0;
    TextView playerName;
    TextView addedScore;

    LayoutInflater inflater;
    ViewSwitcher viewSwitcher;

    Integer points = 0;
    Boolean isPos = true;

    static final String STATE_SCOREDATA = "scoreData";
    static final String STATE_INDEX = "index";
    static final String STATE_ISAZ = "isAZ";
    static final String STATE_ISHIGHLOW = "isHighLow";
//    Boolean instanceStateSaved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.switch_layout);

        scoreHolder = new ScoreAdapter(this);
        scoreData = new ScoreData();

        if (savedInstanceState != null) {
//            if (savedInstanceState.getBoolean(Utilities.FROM_EDIT_KEY, false)) {
//                Log.d("app","from edit key is true");
//                //Main Activity being started after PlayerDetails were edited
//                Integer playerIndex = savedInstanceState.getInt(Utilities.INDEX_KEY);
//                if (savedInstanceState.getBoolean(Utilities.DELETED_KEY, false)) {
//                    //player was deleted
//                    scoreData.playerDatas.remove(playerIndex);
//                    scoreHolder.remove(scoreHolder.getItem(playerIndex));
//                    scoreHolder.notifyDataSetChanged();
//                } else {
//                    PlayerData pd = scoreData.playerDatas.get(playerIndex);
//                    pd.name = savedInstanceState.getString(Utilities.NAME_KEY);
//                    pd.total = savedInstanceState.getInt(Utilities.TOTAL_KEY);
//                    pd.scores = savedInstanceState.getIntegerArrayList(Utilities.SCORE_ARRAY_KEY);
//                    scoreHolder.replaceItem(playerIndex, pd);
//                    scoreData.playerDatas.get(playerIndex).assign(pd);
//                }
//            } else {
                //Main Activity started after screen rotation
                index = savedInstanceState.getInt(STATE_INDEX);
                scoreHolder.isAZ = savedInstanceState.getBoolean(STATE_ISAZ);
                scoreHolder.isHighLow = savedInstanceState.getBoolean(STATE_ISHIGHLOW);
                scoreData = new Gson().fromJson(savedInstanceState.getString(STATE_SCOREDATA), ScoreData.class);
                scoreHolder.addAll(scoreData.playerDatas);
                scoreHolder.checkSort();
//            }
        }

/*
* See below
 */
// else {
//            SharedPreferences prefs = getPreferences(MODE_PRIVATE);
//            if (prefs != null){
//                Log.d("app", "restoring prefs");
//                index = prefs.getInt(STATE_INDEX, 0);
//                scoreHolder.isAZ = prefs.getBoolean(STATE_ISAZ, false);
//                scoreHolder.isHighLow = prefs.getBoolean(STATE_ISHIGHLOW, false);
//                String json = prefs.getString(STATE_SCOREDATA,"");
//                if (!json.equals("null") && json != null && json.length() > 0 && json != "") {
//                    scoreData = new Gson().fromJson(json, ScoreData.class);
//                    scoreHolder.addAll(scoreData.playerDatas);
//                    scoreHolder.checkSort();
//                };
//            }
//        }
        inflater = LayoutInflater.from(this);

        scoreView = (ListView) findViewById(R.id.scoreList);
        scoreView.setAdapter(scoreHolder);

        updateScoresButton = (Button) findViewById(R.id.updateButton);
        if (scoreHolder.getCount()<=0)
            updateScoresButton.setVisibility(View.GONE);

        viewSwitcher = (ViewSwitcher) findViewById(R.id.switchLayout);

        playerName = (TextView) findViewById(R.id.updatePlayerName);
        addedScore = (TextView) findViewById(R.id.updatePlayerScore);

        (findViewById(R.id.updatePlayerScore)).setOnClickListener(updateScoresListener);
        (findViewById(R.id.backspace)).setOnClickListener(updateScoresListener);
        (findViewById(R.id.backspace)).setOnLongClickListener(longBackClickListener);
        (findViewById(R.id.one)).setOnClickListener(updateScoresListener);
        (findViewById(R.id.two)).setOnClickListener(updateScoresListener);
        (findViewById(R.id.three)).setOnClickListener(updateScoresListener);
        (findViewById(R.id.four)).setOnClickListener(updateScoresListener);
        (findViewById(R.id.five)).setOnClickListener(updateScoresListener);
        (findViewById(R.id.six)).setOnClickListener(updateScoresListener);
        (findViewById(R.id.seven)).setOnClickListener(updateScoresListener);
        (findViewById(R.id.eight)).setOnClickListener(updateScoresListener);
        (findViewById(R.id.nine)).setOnClickListener(updateScoresListener);
        (findViewById(R.id.zero)).setOnClickListener(updateScoresListener);
        (findViewById(R.id.minusButton)).setOnClickListener(updateScoresListener);
        (findViewById(R.id.plusButton)).setOnClickListener(updateScoresListener);
        (findViewById(R.id.nextButton)).setOnClickListener(updateScoresListener);
    }

    @Override
    protected void onNewIntent (Intent intent) {
        Log.d("app", "on new intent");
        Bundle bundle = intent.getExtras();
        if (bundle==null)
            return;
        if (bundle.getBoolean(Utilities.FROM_EDIT_KEY, false)) {
            //Main Activity being started after PlayerDetails were edited
            Integer playerIndex = bundle.getInt(Utilities.INDEX_KEY);
            if (bundle.getBoolean(Utilities.DELETED_KEY, false)) {
                //player was deleted
                scoreData.playerDatas.remove(playerIndex);
                scoreHolder.remove(scoreHolder.getItem(playerIndex));
                scoreHolder.notifyDataSetChanged();
                if (scoreHolder.getCount() <= 1) {
                    disableSortOptions();
                    if (scoreHolder.getCount() <= 0) {
                        disableClearOptions();
                        updateScoresButton.setVisibility(View.GONE);
                    }
                }
            } else {
                PlayerData pd = scoreData.playerDatas.get(playerIndex);
                pd.name = bundle.getString(Utilities.NAME_KEY);
                pd.total = bundle.getInt(Utilities.TOTAL_KEY);
                pd.scores = bundle.getIntegerArrayList(Utilities.SCORE_ARRAY_KEY);
            }
            scoreHolder.notifyDataSetChanged();
        }
    }

//    @Override
//    public void onResume() {
//        instanceStateSaved = false;
//        Log.d("app", "onRESUME (instanceStateSaved==FALSE)");
//        super.onResume();
//    }

    @Override
    public void onBackPressed() {
        // behaves as Home key -> saves instance state, calls onStop
        // state saved on resume, but not if app is swiped from recent apps
        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // needed for screen orientation change
        savedInstanceState.putInt(STATE_INDEX, index);
        savedInstanceState.putBoolean(STATE_ISAZ, scoreHolder.isAZ);
        savedInstanceState.putBoolean(STATE_ISHIGHLOW, scoreHolder.isHighLow);
        savedInstanceState.putString(STATE_SCOREDATA, new Gson().toJson(scoreData, ScoreData.class));

        // uncomment if using Preferences and savedInstanceState
//        instanceStateSaved = true;
        Log.d("app", "onSaveInstanceState called, instanceStateSaved==true");
        super.onSaveInstanceState(savedInstanceState);
    }

/*
* Current behavior has Back and Home behave as Home. Data is saved via onSaveInstanceState.
* If app is swiped from recent apps, data is lost.
* Using Preferences retains data even if app is swiped from recent apps, but not if user forces end.
* Using combination of saveInstanceState and Preferences has different behavior for Home and Back (not ideal)
*/
//
//    @Override
//    protected void onStop(){
//        super.onStop();
//        saveToPrefs();
//    }
//
//    private void saveToPrefs() {
//        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
//        SharedPreferences.Editor editor = prefs.edit();
//        Log.d("app", "in onStop");
//        if (!instanceStateSaved) {
//            Log.d("app", "saving to prefs (instanceSaved==false)");
//            editor.putInt(STATE_INDEX, index);
//            editor.putBoolean(STATE_ISAZ, scoreHolder.isAZ);
//            editor.putBoolean(STATE_ISHIGHLOW, scoreHolder.isHighLow);
//            editor.putString(STATE_SCOREDATA, new Gson().toJson(scoreData, ScoreData.class));
//            editor.commit();
//        } else {
//            editor.clear();
//            editor.commit();
//            Log.d("app", "clearing prefs");
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_totals_display, menu);
        clearScores = menu.getItem(0);
        clearAll = menu.getItem(1);
        AZ = menu.getItem(2);
        HighLow = menu.getItem(3);
        Undo = menu.getItem(4);

        checkMenu();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_clearScores) {
            clearScores();
            scoreHolder.notifyDataSetChanged();
        } else if (id == R.id.action_clearAll) {
            clearAll();
        } else if (id == R.id.action_sortAZ) {
            scoreHolder.sortAZ();
            AZ.setEnabled(false);
            HighLow.setEnabled(true);
            Undo.setEnabled(true);
        } else if (id == R.id.action_sortHighLow) {
            scoreHolder.sortHighLow();
            AZ.setEnabled(true);
            HighLow.setEnabled(false);
            Undo.setEnabled(true);
        } else if (id == R.id.action_sortUndo) {
            scoreHolder.sortUndo();
            AZ.setEnabled(true);
            HighLow.setEnabled(true);
            Undo.setEnabled(false);
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkMenu() {
        if (scoreHolder.getCount() <= 0) {
            disableSortOptions();
            disableClearOptions();
        } else {
            enableClearOptions();
            if (scoreHolder.getCount() > 1) {
                if (scoreHolder.isAZ) {
                    AZ.setEnabled(false);
                    HighLow.setEnabled(true);
                    Undo.setEnabled(true);
                } else if (scoreHolder.isHighLow) {
                    AZ.setEnabled(true);
                    HighLow.setEnabled(false);
                    Undo.setEnabled(true);
                } else {
                    AZ.setEnabled(true);
                    HighLow.setEnabled(true);
                    Undo.setEnabled(false);
                }
            }
        }
    }

    private void disableSortOptions() {
        AZ.setEnabled(false);
        HighLow.setEnabled(false);
        Undo.setEnabled(false);
    }
    private void disableClearOptions() {
        clearScores.setEnabled(false);
        clearAll.setEnabled(false);
    }
    private void enableClearOptions() {
        clearScores.setEnabled(true);
        clearAll.setEnabled(true);
    }

    public void clearScores() {
        scoreHolder.clearAllScores();
        scoreData.clearAllScores();
    }

    public void clearAll() {
        scoreHolder.clear();
        scoreData.playerDatas.clear();
        index = 0;
        updateScoresButton.setVisibility(View.GONE);
        checkMenu();
    }

    //DISPLAY TOTALS VIEW

    public void addPlayerClick(View v) {
        PlayerAdder adder = new PlayerAdder();
        adder.getNewPlayerName();
    }
    private class PlayerAdder {
        String newName;
        View nameDialog;
        EditText input;

        void getNewPlayerName() {
            nameDialog = inflater.inflate(R.layout.name_dialog, null);
            input = (EditText) nameDialog.findViewById(R.id.nameInput);

            final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
            alertDialogBuilder.setView(nameDialog);
            alertDialogBuilder
                    .setPositiveButton("Done",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    newName = input.getText().toString();

                                    imm.hideSoftInputFromWindow(input.getWindowToken(),0);
                                    addPlayer();
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {

                                    imm.hideSoftInputFromWindow(input.getWindowToken(),0);

                                    dialog.cancel();
                                }
                            });

            final AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

            // open keyboard
//            input.requestFocus();
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

            // name cannot be empty
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
            input.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before,
                                          int count) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // Check if edittext is empty
                    if (TextUtils.isEmpty(s)) {
                        // Disable done button
                        alertDialog.getButton(
                                AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    } else {
                        // Enable
                        alertDialog.getButton(
                                AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    }

                }
            });
        }

        void addPlayer() {
            PlayerData pd = new PlayerData(newName, index);
            scoreData.playerDatas.add(pd);
            scoreHolder.add(pd);
            scoreHolder.checkSort();
            index++;
            //once we have 1 player, enable clear and sort and update
            if (scoreHolder.getCount() > 0 && scoreHolder.getCount() < 3) {
                checkMenu();
                updateScoresButton.setVisibility(View.VISIBLE);
            }
        }
    }

    public void updateScores(View v) {
        updateIndex = 0;
        points = 0;
        isPos = true;
        playerName.setText(scoreHolder.getItem(updateIndex).name);
        addedScore.setTextColor(getResources().getColor(R.color.disabled_text));
        addedScore.setText("0");
        viewSwitcher.showNext();
        getSupportActionBar().hide();
    }

    //UPDATE SCORES VIEW
    View.OnClickListener updateScoresListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.backspace) {
                points /= 10;
                if (points > 0) {
                    refreshScore();
                } else {
                    isPos = true;
                    addedScore.setText("0");
                    addedScore.setTextColor(getResources().getColor(R.color.disabled_text));
                }
            } else if (v.getId() == R.id.one) {
                addNum(1);
                refreshScore();
            } else if (v.getId() == R.id.two) {
                addNum(2);
                refreshScore();
            } else if (v.getId() == R.id.three) {
                addNum(3);
                refreshScore();
            } else if (v.getId() == R.id.four) {
                addNum(4);
                refreshScore();
            } else if (v.getId() == R.id.five) {
                addNum(5);
                refreshScore();
            } else if (v.getId() == R.id.six) {
                addNum(6);
                refreshScore();
            } else if (v.getId() == R.id.seven) {
                addNum(7);
                refreshScore();
            } else if (v.getId() == R.id.eight) {
                addNum(8);
                refreshScore();
            } else if (v.getId() == R.id.nine) {
                addNum(9);
                refreshScore();
            } else if (v.getId() == R.id.zero) {
                addNum(0);
                refreshScore();
            } else if (v.getId() == R.id.minusButton) {
                isPos = false;
                if (points > 0)
                    refreshScore();
            } else if (v.getId() == R.id.plusButton) {
                isPos = true;
                if (points > 0)
                    refreshScore();
            } else if (v.getId() == R.id.nextButton) {
                PlayerData pd = scoreHolder.getItem(updateIndex);
                points *= isPos ? 1 : -1;
                pd.addScore(points);
                updateIndex++;
                if (updateIndex < scoreHolder.getCount()) {
                    points = 0;
                    isPos = true;
                    addedScore.setText("");
                    playerName.setText(scoreHolder.getItem(updateIndex).name);
                } else {
                    //done
                    scoreHolder.checkSort();
                    scoreHolder.notifyDataSetChanged();
                    viewSwitcher.showPrevious();
                    getSupportActionBar().show();
                }
            }
        }
    };

    View.OnLongClickListener longBackClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            points = 0;
            isPos = true;
            addedScore.setText("0");
            addedScore.setTextColor(getResources().getColor(R.color.disabled_text));
            return true;
        }
    };

    public void addNum(Integer val) {
        // overflow prevention
        if (points >= Integer.MAX_VALUE / 10)
            points = Integer.MAX_VALUE;
        else {
            points *= 10;
            points += val;
        }

    }
    public void refreshScore() {
        addedScore.setTextColor(getResources().getColor(R.color.primary_text));
        if (points == 0) {
            addedScore.setText("0");
        } else {
            if (isPos) {
                addedScore.setText("+" + Integer.toString(points));
            } else {
                addedScore.setText("-" + Integer.toString(points));
            }
        }
    }
}