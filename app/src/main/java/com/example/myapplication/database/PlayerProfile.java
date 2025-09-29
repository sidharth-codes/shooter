package com.example.myapplication.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "player_profiles")
public class PlayerProfile {
    @PrimaryKey
    @NonNull
    public String playerName = "";
    public int totalCoins;
    public int maxHealthLevel;
    public int bulletDamageLevel;
}