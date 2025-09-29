package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.example.myapplication.database.PlayerProfile;
import com.example.myapplication.database.PlayerProfileDao;

import java.util.ArrayList;
import java.util.Iterator;
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
    private Player player;
    private final List<Bullet> bullets;
    private final List<Enemy> enemies;
    private final List<PowerUp> powerUps;
    private int score = 0;
    private boolean isGameOver = false;
    private final ExecutorService executorService;
    private final String playerName;
    private final Random random = new Random();
    private final Rect backButton;
    private final GameActivity gameActivity;
    private int coinsCollectedThisRun = 0;
    private final PlayerProfileDao playerProfileDao;
    private final AppDatabase db;
    private PlayerProfile playerProfile;
    private final Object listLock = new Object(); // <<< FIX: Add a lock object

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

        executorService = Executors.newSingleThreadExecutor();
        db = AppDatabase.getDatabase(context);
        playerProfileDao = db.playerProfileDao();

        loadPlayerProfileBlocking();

        screenX = getResources().getDisplayMetrics().widthPixels;
        screenY = getResources().getDisplayMetrics().heightPixels;
        paint = new Paint();
        player = new Player(context, screenX, screenY, playerProfile.maxHealthLevel);
        bullets = new ArrayList<>();
        enemies = new ArrayList<>();
        powerUps = new ArrayList<>();

        backButton = new Rect(screenX - 250, 50, screenX - 50, 150);
    }

    private void loadPlayerProfileBlocking() {
        playerProfile = playerProfileDao.getPlayerByName(playerName);
        if (playerProfile == null) {
            playerProfile = new PlayerProfile();
            playerProfile.playerName = playerName;
            playerProfile.totalCoins = 0;
            playerProfile.maxHealthLevel = 1;
            playerProfile.bulletDamageLevel = 1;

            final PlayerProfile newProfile = playerProfile;
            executorService.execute(() -> playerProfileDao.insertOrUpdate(newProfile));
        }
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

        synchronized (listLock) { // <<< FIX: Synchronize all list modifications
            Iterator<Bullet> bulletIterator = bullets.iterator();
            while (bulletIterator.hasNext()) {
                Bullet bullet = bulletIterator.next();
                bullet.update();
                if (bullet.getY() < 0) bulletIterator.remove();
            }
            if (random.nextInt(100) < 5) enemies.add(new Enemy(screenX, screenY));
            if (random.nextInt(500) < 2) powerUps.add(new PowerUp(screenX, screenY));

            Iterator<Enemy> enemyIterator = enemies.iterator();
            while (enemyIterator.hasNext()) {
                Enemy enemy = enemyIterator.next();
                enemy.update();
                if (Rect.intersects(player.getCollisionShape(), enemy.getCollisionShape()) || enemy.getY() > screenY) {
                    player.takeDamage(1);
                    enemyIterator.remove();
                    if (player.getHealth() <= 0) {
                        isGameOver = true;
                        saveSessionData();
                    }
                }
            }

            bulletIterator = bullets.iterator();
            while (bulletIterator.hasNext()) {
                Bullet bullet = bulletIterator.next();
                enemyIterator = enemies.iterator();
                while (enemyIterator.hasNext()) {
                    Enemy enemy = enemyIterator.next();
                    if(Rect.intersects(bullet.getCollisionShape(), enemy.getCollisionShape())) {
                        enemy.takeDamage(bullet.getDamage());
                        bulletIterator.remove();
                        if (enemy.getHealth() <= 0) {
                            enemyIterator.remove();
                            score += 10;
                            coinsCollectedThisRun++;
                        }
                        break;
                    }
                }
            }
            Iterator<PowerUp> powerUpIterator = powerUps.iterator();
            while (powerUpIterator.hasNext()) {
                PowerUp powerUp = powerUpIterator.next();
                powerUp.update();
                if (Rect.intersects(player.getCollisionShape(), powerUp.getCollisionShape())) {
                    player.applyPowerUp(powerUp.getType());
                    powerUpIterator.remove();
                } else if (powerUp.getY() > screenY) {
                    powerUpIterator.remove();
                }
            }
        }
    }

    private void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            Canvas canvas = surfaceHolder.lockCanvas();
            if (canvas == null) return;
            canvas.drawColor(Color.BLACK);

            synchronized (listLock) { // <<< FIX: Synchronize all list reading for drawing
                canvas.drawBitmap(player.getBitmap(), player.getX(), player.getY(), paint);
                paint.setColor(Color.RED);
                for (Enemy enemy : enemies) canvas.drawRect(enemy.getCollisionShape(), paint);
                paint.setColor(Color.YELLOW);
                for (Bullet bullet : bullets) canvas.drawRect(bullet.getCollisionShape(), paint);
                for (PowerUp powerUp : powerUps) {
                    paint.setColor(powerUp.getColor());
                    canvas.drawRect(powerUp.getCollisionShape(), paint);
                }
            }

            paint.setColor(Color.WHITE);
            paint.setTextSize(50);
            canvas.drawText("Score: " + score, 50, 100, paint);
            canvas.drawText("Health: " + player.getHealth(), 50, 160, paint);
            canvas.drawText("Coins: " + coinsCollectedThisRun, 50, 220, paint);
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
    public void resume() {}
    public void pause() {}

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
        try { gameThread.join(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
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
                    synchronized(listLock) {
                        bullets.add(new Bullet(player.getX() + (player.getWidth() / 2f), player.getY(), playerProfile.bulletDamageLevel));
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isGameOver) {
                    player.setX((int) event.getX() - player.getWidth() / 2);
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
    private void saveSessionData() {
        playerProfile.totalCoins += coinsCollectedThisRun;

        final PlayerProfile profileToSave = playerProfile;

        executorService.execute(() -> {
            playerProfileDao.insertOrUpdate(profileToSave);
            if (score > 0) {
                GameScore gameScore = new GameScore();
                gameScore.playerName = this.playerName;
                gameScore.score = score;
                db.gameScoreDao().insert(gameScore);
            }
        });
    }
    private void resetGame() {
        player.reset();
        synchronized (listLock) {
            enemies.clear();
            bullets.clear();
            powerUps.clear();
        }
        score = 0;
        coinsCollectedThisRun = 0;
        isGameOver = false;
    }
}