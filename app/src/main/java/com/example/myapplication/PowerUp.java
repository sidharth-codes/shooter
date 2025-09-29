package com.example.myapplication;

import android.graphics.Color;
import java.util.Random;

public class PowerUp extends GameObject {

    public enum PowerUpType {
        SHIELD,
        RAPID_FIRE
    }

    private final PowerUpType type;

    public PowerUp(int screenX, int screenY) {
        width = 50;
        height = 50;
        x = new Random().nextInt(screenX - width);
        y = -height;
        speed = 7;

        // Randomly assign a type
        if (new Random().nextBoolean()) {
            type = PowerUpType.SHIELD;
        } else {
            type = PowerUpType.RAPID_FIRE;
        }
    }

    @Override
    public void update() {
        y += speed;
    }

    public PowerUpType getType() {
        return type;
    }

    public int getColor() {
        if (type == PowerUpType.SHIELD) {
            return Color.CYAN; // Shield power-ups are cyan
        } else {
            return Color.GREEN; // Rapid-fire power-ups are green
        }
    }
}