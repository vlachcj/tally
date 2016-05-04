package com.example.vlachbear.scorer;

import java.io.Serializable;
import java.util.ArrayList;

public class PlayerData implements Serializable {
    String name;
    Integer total;
    ArrayList<Integer> scores;
    Integer originalIndex;

    public PlayerData() {
        scores = new ArrayList<>();
    }
    public PlayerData(PlayerData pd) {
        scores = new ArrayList<>();
        assign(pd);
        originalIndex = pd.originalIndex;
    }
    public PlayerData(String n, Integer index) {

        name = n;
        originalIndex = index;
        total = 0;
        scores = new ArrayList<>();
    }

    public void assign(PlayerData newer) {
        name = newer.name;
        total = newer.total;
        scores.clear();
        scores.addAll(newer.scores);
    }
    public void addScore(Integer score) {
        total += score;
        scores.add(score);
    }
    public ArrayList<Integer> scoreCopy() {
        ArrayList<Integer> copy = new ArrayList<>();
        for (int i = 0; i < scores.size(); i++) {
            copy.add(scores.get(i));
        }
        return copy;
    }
}
