package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class Player {
    public int x, y, width, height;
    private final int screenX;
    private final Bitmap bitmap;

    public Player(int screenX, int screenY) {
        this.screenX = screenX;
        width = 100;
        height = 100;
        x = screenX / 2 - width / 2;
        y = screenY - 200;

        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        canvas.drawRect(0, 0, width, height, paint);
    }

    public void update() {
        if (x < 0) {
            x = 0;
        }
        if (x > screenX - width) {
            x = screenX - width;
        }
    }

    public Rect getCollisionShape() {
        return new Rect(x, y, x + width, y + height);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}