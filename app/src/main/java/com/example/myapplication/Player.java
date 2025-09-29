package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Player extends GameObject {

    private final int screenX;
    private final int screenY;
    private Bitmap bitmap; // <<< FIX: Removed 'final' keyword
    private int health;
    private int coins;
    private final int MAX_HEALTH = 3;

    public Player(Context context, int screenX, int screenY) {
        this.screenX = screenX;
        this.screenY = screenY;
        width = 150;
        height = 150;

        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.player_ship);
        bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);

        reset();
    }

    @Override
    public void update() {
        if (x < 0) {
            x = 0;
        }
        if (x > screenX - width) {
            x = screenX - width;
        }
    }

    public void reset() {
        x = screenX / 2 - width / 2;
        y = screenY - 200;
        health = MAX_HEALTH;
        coins = 0;
    }

    public void takeDamage(int damage) {
        this.health -= damage;
    }

    public void addCoins(int amount) {
        this.coins += amount;
    }

    public void applyPowerUp(PowerUp.PowerUpType type) {
        if (type == PowerUp.PowerUpType.SHIELD) {
            if (health < MAX_HEALTH) {
                health++;
            }
        }
        // RAPID_FIRE would be handled in GameView's shooting logic
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getHealth() { return health; }
    public int getCoins() { return coins; }
}