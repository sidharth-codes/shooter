package com.example.myapplication;

import android.graphics.Rect;
import java.util.Random;

public class Enemy {
    public int x, y, width, height, speed;

    public Enemy(int screenX, int screenY) {
        width = 80;
        height = 80;
        speed = new Random().nextInt(10) + 5;
        x = new Random().nextInt(screenX - width);
        y = -height;
    }

    public void update() {
        y += speed;
    }

    public Rect getCollisionShape() {
        return new Rect(x, y, x + width, y + height);
    }
}