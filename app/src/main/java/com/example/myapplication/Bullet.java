package com.example.myapplication;

public class Bullet extends GameObject {

    private final int damage;

    public Bullet(float x, float y) {
        this.x = (int) (x - (10f / 2));
        this.y = (int) y;
        this.width = 10;
        this.height = 30;
        this.speed = 40;
        this.damage = 1; // Each bullet does 1 damage
    }

    @Override
    public void update() {
        y -= speed;
    }

    public int getDamage() {
        return damage;
    }
}