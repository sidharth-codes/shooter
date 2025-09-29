package com.example.myapplication;

import android.graphics.Rect;

public abstract class GameObject {
    protected int x, y, width, height, speed;

    public abstract void update();

    public Rect getCollisionShape() {
        return new Rect(x, y, x + width, y + height);
    }

    // Getters
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    // Setters
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
}