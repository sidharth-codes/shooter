package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.database.AppDatabase;
import com.example.myapplication.database.GameScore;
import com.example.myapplication.database.GameScoreDao;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private ExecutorService executorService;
    private GameScoreDao gameScoreDao;
    private TextView highScoreTextView;
    private TextView recentScoresTextView;
    private EditText playerNameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        executorService = Executors.newSingleThreadExecutor();
        AppDatabase db = AppDatabase.getDatabase(this);
        gameScoreDao = db.gameScoreDao();

        highScoreTextView = findViewById(R.id.highScoreTextView);
        recentScoresTextView = findViewById(R.id.recentScoresTextView);
        playerNameEditText = findViewById(R.id.playerNameEditText);
        Button startButton = findViewById(R.id.startButton);

        startButton.setOnClickListener(v -> {
            String playerName = playerNameEditText.getText().toString().trim();
            if (playerName.isEmpty()) {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            intent.putExtra("PLAYER_NAME", playerName);
            startActivity(intent);
        });

        loadScores();
    }

    private void loadScores() {
        executorService.execute(() -> {
            final GameScore highScore = gameScoreDao.getHighScore();
            final List<GameScore> recentScores = gameScoreDao.getRecentScores();

            runOnUiThread(() -> {
                if (highScore != null) {
                    highScoreTextView.setText("All-Time High: " + highScore.playerName + " - " + highScore.score);
                } else {
                    highScoreTextView.setText("All-Time High: N/A");
                }

                StringBuilder recentScoresText = new StringBuilder("Recent Scores:\n");
                if (recentScores.isEmpty()) {
                    recentScoresText.append("No scores yet!");
                } else {
                    for (GameScore score : recentScores) {
                        recentScoresText.append(score.playerName).append(": ").append(score.score).append("\n");
                    }
                }
                recentScoresTextView.setText(recentScoresText.toString());
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadScores();
    }
}