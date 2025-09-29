package com.example.myapplication.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface PlayerProfileDao {
    @Query("SELECT * FROM player_profiles WHERE playerName = :name LIMIT 1")
    PlayerProfile getPlayerByName(String name);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(PlayerProfile profile);
}