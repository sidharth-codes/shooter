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
                Toast.makeText(this, R.string.please_enter_name, Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            intent.putExtra("PLAYER_NAME", playerName);
            startActivity(intent);
        });
    }

    private void loadScores() {
        executorService.execute(() -> {
            final GameScore highScore = gameScoreDao.getHighScore();
            final List<GameScore> recentScores = gameScoreDao.getRecentScores();

            runOnUiThread(() -> {
                if (highScore != null) {
                    String highScoreText = getString(R.string.high_score_format, highScore.playerName, highScore.score);
                    highScoreTextView.setText(highScoreText);
                } else {
                    highScoreTextView.setText(getString(R.string.all_time_high_n_a));
                }

                StringBuilder recentScoresTextBuilder = new StringBuilder();
                recentScoresTextBuilder.append(getString(R.string.recent_scores)).append("\n");

                if (recentScores.isEmpty()) {
                    recentScoresTextBuilder.append(getString(R.string.no_scores_yet));
                } else {
                    for (GameScore score : recentScores) {
                        recentScoresTextBuilder.append(getString(R.string.recent_score_line, score.playerName, score.score)).append("\n");
                    }
                }
                recentScoresTextView.setText(recentScoresTextBuilder.toString());
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadScores();
    }
}