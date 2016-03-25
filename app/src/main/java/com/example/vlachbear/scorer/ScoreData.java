package com.example.vlachbear.scorer;

import java.util.ArrayList;

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
