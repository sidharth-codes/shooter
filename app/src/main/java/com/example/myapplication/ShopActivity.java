package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.database.AppDatabase;
import com.example.myapplication.database.PlayerProfile;
import com.example.myapplication.database.PlayerProfileDao;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ShopActivity extends AppCompatActivity {

    private TextView coinBalanceTextView;
    private PlayerProfileDao playerProfileDao;
    private ExecutorService executorService;
    private PlayerProfile playerProfile;
    private String playerName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        playerName = getIntent().getStringExtra("PLAYER_NAME");
        if (playerName == null || playerName.isEmpty()) {
            Toast.makeText(this, "Player not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        executorService = Executors.newSingleThreadExecutor();
        playerProfileDao = AppDatabase.getDatabase(this).playerProfileDao();

        coinBalanceTextView = findViewById(R.id.coinBalanceTextView);
        Button buyHealthButton = findViewById(R.id.buyHealthButton);
        Button buyDamageButton = findViewById(R.id.buyDamageButton);

        loadPlayerProfile();

        buyHealthButton.setOnClickListener(v -> {
            int cost = 50;
            if (playerProfile != null && playerProfile.totalCoins >= cost) {
                playerProfile.totalCoins -= cost;
                playerProfile.maxHealthLevel++;
                updatePlayerProfile();
                Toast.makeText(this, "Health upgrade purchased!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Not enough coins!", Toast.LENGTH_SHORT).show();
            }
        });

        buyDamageButton.setOnClickListener(v -> {
            int cost = 100;
            if (playerProfile != null && playerProfile.totalCoins >= cost) {
                playerProfile.totalCoins -= cost;
                playerProfile.bulletDamageLevel++;
                updatePlayerProfile();
                Toast.makeText(this, "Damage upgrade purchased!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Not enough coins!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPlayerProfile() {
        executorService.execute(() -> {
            playerProfile = playerProfileDao.getPlayerByName(playerName);
            runOnUiThread(this::updateUI);
        });
    }

    private void updatePlayerProfile() {
        executorService.execute(() -> {
            playerProfileDao.insertOrUpdate(playerProfile);
            runOnUiThread(this::updateUI);
        });
    }

    private void updateUI() {
        if (playerProfile != null) {
            coinBalanceTextView.setText(getString(R.string.coin_balance_format, playerProfile.totalCoins));
        }
    }
}