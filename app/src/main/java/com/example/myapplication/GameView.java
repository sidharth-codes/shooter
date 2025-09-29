package com.example.myapplication;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import androidx.annotation.NonNull;
import com.example.myapplication.database.AppDatabase;
import com.example.myapplication.database.GameScore;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameView extends SurfaceView implements Runnable, SurfaceHolder.Callback {

    private Thread gameThread;
    private boolean isPlaying;
    private final int screenX, screenY;
    private final Paint paint;
    private final SurfaceHolder surfaceHolder;
    private final Player player;
    private final List<Bullet> bullets;
    private final List<Enemy> enemies;
    private int score = 0;
    private boolean isGameOver = false;
    private final AppDatabase db;
    private final ExecutorService executorService;
    private final String playerName;
    private final Random random = new Random();
    private final Rect backButton;
    private final GameActivity gameActivity;

    public GameView(GameActivity context, String playerName) {
        this(context, null, playerName);
    }

    @SuppressLint("ViewConstructor")
    public GameView(GameActivity context, AttributeSet attrs, String playerName) {
        super(context, attrs);
        this.gameActivity = context;
        this.playerName = playerName;

        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        screenX = getResources().getDisplayMetrics().widthPixels;
        screenY = getResources().getDisplayMetrics().heightPixels;
        paint = new Paint();
        player = new Player(screenX, screenY);
        bullets = new ArrayList<>();
        enemies = new ArrayList<>();
        db = AppDatabase.getDatabase(getContext()); // Use getContext() for safety
        executorService = Executors.newSingleThreadExecutor();
        backButton = new Rect(screenX - 250, 50, screenX - 50, 150);
    }

    @Override
    public void run() {
        while (isPlaying) {
            update();
            draw();
            sleep();
        }
    }

    private void update() {
        if (isGameOver) return;
        player.update();
        List<Bullet> bulletsToRemove = new ArrayList<>();
        for (Bullet bullet : bullets) {
            bullet.update();
            if (bullet.y < 0) bulletsToRemove.add(bullet);
        }
        bullets.removeAll(bulletsToRemove);

        if (random.nextInt(100) < 5) {
            enemies.add(new Enemy(screenX, screenY));
        }

        List<Enemy> enemiesToRemove = new ArrayList<>();
        for (Enemy enemy : enemies) {
            enemy.update();
            if (enemy.y > screenY || Rect.intersects(player.getCollisionShape(), enemy.getCollisionShape())) {
                isGameOver = true;
                saveScore();
            }
            for (Bullet bullet : bullets) {
                if (Rect.intersects(enemy.getCollisionShape(), bullet.getCollisionShape())) {
                    enemiesToRemove.add(enemy);
                    bulletsToRemove.add(bullet);
                    score += 10;
                }
            }
        }
        enemies.removeAll(enemiesToRemove);
        bullets.removeAll(bulletsToRemove);
    }

    private void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            Canvas canvas = surfaceHolder.lockCanvas();
            if (canvas == null) return;
            canvas.drawColor(Color.BLACK);
            canvas.drawBitmap(player.getBitmap(), player.x, player.y, paint);
            paint.setColor(Color.RED);
            for (Enemy enemy : enemies) canvas.drawRect(enemy.getCollisionShape(), paint);
            paint.setColor(Color.YELLOW);
            for (Bullet bullet : bullets) canvas.drawRect(bullet.getCollisionShape(), paint);
            paint.setColor(Color.WHITE);
            paint.setTextSize(50);
            canvas.drawText("Score: " + score, 50, 100, paint);

            if (!isGameOver) {
                paint.setColor(Color.DKGRAY);
                canvas.drawRect(backButton, paint);
                paint.setColor(Color.WHITE);
                paint.setTextSize(50);
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText("Back", backButton.centerX(), backButton.centerY() + 15, paint);
                paint.setTextAlign(Paint.Align.LEFT);
            }

            if (isGameOver) {
                paint.setTextSize(100);
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText("GAME OVER", screenX / 2f, screenY / 2f, paint);
                canvas.drawText("Tap to Retry", screenX / 2f, (screenY / 2f) + 120, paint);
                paint.setTextAlign(Paint.Align.LEFT);
            }
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void sleep() {
        try { Thread.sleep(17); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    public void resume() {
        // The game thread is now started in surfaceCreated
    }

    public void pause() {
        // The game thread is now stopped in surfaceDestroyed
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        isPlaying = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        isPlaying = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isGameOver) {
                    resetGame();
                } else {
                    if (backButton.contains((int) event.getX(), (int) event.getY())) {
                        goToMainMenu();
                        return true;
                    }
                    bullets.add(new Bullet(player.x + (player.width / 2f), player.y));
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isGameOver) {
                    player.x = (int) event.getX() - player.width / 2;
                }
                break;
        }
        return true;
    }

    private void goToMainMenu() {
        if (gameActivity != null) {
            gameActivity.goToMainMenu();
        }
    }

    private void saveScore() {
        if (score > 0) {
            executorService.execute(() -> {
                GameScore gameScore = new GameScore();
                gameScore.playerName = this.playerName;
                gameScore.score = score;
                db.gameScoreDao().insert(gameScore);
            });
        }
    }

    private void resetGame() {
        player.x = screenX / 2 - player.width / 2;
        player.y = screenY - 200;
        enemies.clear();
        bullets.clear();
        score = 0;
        isGameOver = false;
    }
}