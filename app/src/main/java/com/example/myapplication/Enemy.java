package com.example.myapplication;

import java.util.Random;

public class Enemy extends GameObject {

    private int health;

    public Enemy(int screenX, int screenY) {
        width = 80;
        height = 80;
        speed = new Random().nextInt(10) + 5;
        x = new Random().nextInt(screenX - width);
        y = -height;
        health = 1;
    }

    @Override
    public void update() {
        y += speed;
    }

    public void takeDamage(int damage) {
        this.health -= damage;
    }

    public int getHealth() {
        return health;
    }
}