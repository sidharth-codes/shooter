package com.example.myapplication.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "scores")
public class GameScore {

    @PrimaryKey(autoGenerate = true)
    public int id;
    public String playerName;
    public int score;
}