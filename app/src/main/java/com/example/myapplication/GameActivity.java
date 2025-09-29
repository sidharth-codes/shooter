package com.example.myapplication;

import android.os.Bundle;
import android.view.WindowManager;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

    private GameView gameView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        String playerName = getIntent().getStringExtra("PLAYER_NAME");
        if (playerName == null || playerName.isEmpty()) {
            playerName = "Player"; // Fallback name
        }

        gameView = new GameView(this, playerName);
        setContentView(gameView);
    }

    public void goToMainMenu() {
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }
}