package com.example.myapplication.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {GameScore.class, PlayerProfile.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract GameScoreDao gameScoreDao();
    public abstract PlayerProfileDao playerProfileDao();
    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "game_database")
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries() // Use with caution
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}