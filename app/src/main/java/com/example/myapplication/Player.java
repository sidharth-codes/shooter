package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Player extends GameObject {

    private final int screenX;
    private final int screenY;
    private Bitmap bitmap;
    private int health;
    private final int maxHealth;

    public Player(Context context, int screenX, int screenY, int maxHealthLevel) {
        this.screenX = screenX;
        this.screenY = screenY;
        this.maxHealth = 2 + maxHealthLevel;

        width = 150;
        height = 150;

        this.bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.player_ship);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        this.bitmap = scaledBitmap;

        reset();
    }

    @Override
    public void update() {
        if (x < 0) x = 0;
        if (x > screenX - width) x = screenX - width;
    }

    public void reset() {
        x = screenX / 2 - width / 2;
        y = screenY - 200;
        health = this.maxHealth;
    }

    public void takeDamage(int damage) {
        this.health -= damage;
    }

    public void applyPowerUp(PowerUp.PowerUpType type) {
        if (type == PowerUp.PowerUpType.SHIELD) {
            if (health < this.maxHealth) {
                health++;
            }
        }
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getHealth() {
        return health;
    }
}