package com.example.myapplication.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface GameScoreDao {
    @Insert
    void insert(GameScore gameScore);

    @Query("SELECT * FROM scores ORDER BY score DESC LIMIT 1")
    GameScore getHighScore();

    @Query("SELECT * FROM scores ORDER BY id DESC LIMIT 10")
    List<GameScore> getRecentScores();
}