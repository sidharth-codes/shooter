package com.example.myapplication;

import android.graphics.Rect;

public class Bullet {
    public float x, y;
    public final float width = 10f;
    public final float height = 30f;
    public final int speed = 40;

    public Bullet(float x, float y) {
        this.x = x - (width / 2); // Center the bullet on the x-coordinate
        this.y = y;
    }

    public void update() {
        y -= speed;
    }

    public Rect getCollisionShape() {
        return new Rect((int)x, (int)y, (int)(x + width), (int)(y + height));
    }
}

