package com.example.vlachbear.scorer;

import java.util.ArrayList;

/**
 * Created by vlachbear on 8/26/15.
 */
public class ScoreData {
    public ArrayList<PlayerData> playerDatas;

    public ScoreData() {
        playerDatas = new ArrayList<>();
    }

    public void clearAllScores() {
        for (int i = 0; i < playerDatas.size(); i++) {
            PlayerData pd = playerDatas.get(i);
            pd.scores.clear();
            pd.total = 0;
        }
    }
}
