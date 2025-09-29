package com.example.myapplication.upgrades;

import android.content.Context;
import android.content.SharedPreferences;

public class UpgradeManager {

    private static final String PREFS_NAME = "GameUpgrades";
    private static final String KEY_COINS = "total_coins";
    private static final String KEY_MAX_HEALTH = "max_health";
    private static final String KEY_BULLET_DAMAGE = "bullet_damage";

    private final SharedPreferences sharedPreferences;

    public UpgradeManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // --- Coins ---
    public int getCoins() {
        return sharedPreferences.getInt(KEY_COINS, 0);
    }

    public void addCoins(int amount) {
        int currentCoins = getCoins();
        sharedPreferences.edit().putInt(KEY_COINS, currentCoins + amount).apply();
    }

    public boolean spendCoins(int amount) {
        int currentCoins = getCoins();
        if (currentCoins >= amount) {
            sharedPreferences.edit().putInt(KEY_COINS, currentCoins - amount).apply();
            return true;
        }
        return false;
    }

    // --- Health Upgrade ---
    public int getMaxHealth() {
        return sharedPreferences.getInt(KEY_MAX_HEALTH, 3); // Default health is 3
    }

    public void increaseMaxHealth() {
        int currentHealth = getMaxHealth();
        sharedPreferences.edit().putInt(KEY_MAX_HEALTH, currentHealth + 1).apply();
    }

    // --- Damage Upgrade ---
    public int getBulletDamage() {
        return sharedPreferences.getInt(KEY_BULLET_DAMAGE, 1); // Default damage is 1
    }

    public void increaseBulletDamage() {
        int currentDamage = getBulletDamage();
        sharedPreferences.edit().putInt(KEY_BULLET_DAMAGE, currentDamage + 1).apply();
    }
}