package com.amilek.gamestates;

import com.amilek.entities.*;
import com.amilek.main.Game;
import com.amilek.managers.GameStateManager;
import com.amilek.managers.Jukebox;
import com.amilek.managers.Save;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;

public class PlayState extends GameState {

    private SpriteBatch sb;
    private ShapeRenderer sr;

    private BitmapFont font;
    private Player hudPlayer;

    private Player player;
    private ArrayList<Bullet> bullets;
    private ArrayList<Asteroid> asteroids;

    private ArrayList<Particle> particles;

    private int level;
    private int totalAsteroids;
    private int numAsteroidsLeft;

    private FlyingSaucer flyingSaucer;
    private ArrayList<Bullet> enemyBullets;
    private float fsTimer;
    private float fsTime;

    private float maxDelay;
    private float minDelay;
    private float currentDelay;
    private float bgTimer;
    private boolean playLowPulse;

    public PlayState(GameStateManager gms) {
        super(gms);
    }

    public void init() {

        sr = new ShapeRenderer();
        sb = new SpriteBatch();

        //set font
        FreeTypeFontGenerator gen =
                new FreeTypeFontGenerator(Gdx.files.internal("fonts/Hyperspace Bold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter =
                new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 20;
        font = gen.generateFont(parameter);
        gen.dispose();

        bullets = new ArrayList<Bullet>();
        player = new Player(bullets);

        asteroids = new ArrayList<Asteroid>();
        particles = new ArrayList<Particle>();

        level = 1;
        spawnAsteroids();

        hudPlayer = new Player(null);

        fsTimer = 0;
        fsTime = 15;
        enemyBullets = new ArrayList<Bullet>();

        // set bg music
        maxDelay = 1;
        minDelay = 0.25f;
        currentDelay = maxDelay;
        bgTimer = maxDelay;
        playLowPulse = true;

    }

    private void createParticles(float x, float y) {
        for (int i = 0; i < 6; i++) {
            particles.add(new Particle(x, y));
        }
    }

    private void splitAsteroids(Asteroid asteroid) {

        createParticles(asteroid.getX(), asteroid.getY());
        numAsteroidsLeft--;
        currentDelay =
                ((maxDelay - minDelay) * numAsteroidsLeft / totalAsteroids) + minDelay;

        if (asteroid.getType() == Asteroid.LARGE) {
            asteroids.add(new Asteroid(asteroid.getX(), asteroid.getY(), Asteroid.MEDIUM));
            asteroids.add(new Asteroid(asteroid.getX(), asteroid.getY(), Asteroid.MEDIUM));
        }
        if (asteroid.getType() == Asteroid.MEDIUM) {
            asteroids.add(new Asteroid(asteroid.getX(), asteroid.getY(), Asteroid.SMALL));
            asteroids.add(new Asteroid(asteroid.getX(), asteroid.getY(), Asteroid.SMALL));
        }

    }

    private void spawnAsteroids() {
        asteroids.clear();

        int numToSpawn = 4 + level - 1;
        totalAsteroids = numToSpawn * 7;
        numAsteroidsLeft = totalAsteroids;
        currentDelay = maxDelay;

        for (int i = 0; i < numToSpawn; i++) {

            float x = MathUtils.random(Game.WIDTH);
            float y = MathUtils.random(Game.HEIGHT);

            float dx = x - player.getX();
            float dy = y - player.getY();
            float dist = (float) Math.sqrt(dx * dx + dy * dy);

            while (dist < 100) {
                x = MathUtils.random(Game.WIDTH);
                y = MathUtils.random(Game.HEIGHT);
                dx = x - player.getX();
                dy = y - player.getY();
                dist = (float) Math.sqrt(dx * dx + dy * dy);
            }

            asteroids.add(new Asteroid(x, y, Asteroid.LARGE));
        }
    }


    public void update(float dt) {

        //get user input
        handleInput();

        //next level
        if (asteroids.size() == 0) {
            level++;
            spawnAsteroids();
        }

        //update player
        player.update(dt);
        if (player.isDead()) {
            if (player.getExtraLives() == 0) {
                Jukebox.stopAll();
                Save.gd.setTenativeScore(player.getScore());
                gsm.setState(GameStateManager.GAMEOVER);
                return;
            }
            player.reset();
            player.looseLive();
            flyingSaucer = null;
            Jukebox.stop("largesaucer");
            Jukebox.stop("smallsaucer");
            return;
        }

        //update player bullets
        for (int i = 0; i < bullets.size(); i++) {
            bullets.get(i).update(dt);
            if (bullets.get(i).shouldRemove()) {
                bullets.remove(i);
                i--;
            }
        }

        //update flying saucer
        if (flyingSaucer == null) {
            fsTimer += dt;
            if (fsTimer >= fsTime) {
                fsTimer = 0;
                int type = MathUtils.random() < 0.5 ?
                        FlyingSaucer.SMALL : FlyingSaucer.LARGE;
                int direction = MathUtils.random() < 0.5 ?
                        FlyingSaucer.RIGHT : FlyingSaucer.LEFT;
                flyingSaucer = new FlyingSaucer(
                        type,
                        direction,
                        player,
                        enemyBullets);
            }
        } else {
            flyingSaucer.update(dt);
            if (flyingSaucer.shouldRemove()) {
                flyingSaucer = null;
                Jukebox.stop("smallsaucer");
                Jukebox.stop("largesaucer");
            }
        }

        //update enemy bullets
        for (int i = 0; i < enemyBullets.size(); i++) {
            enemyBullets.get(i).update(dt);
            if (enemyBullets.get(i).shouldRemove()) {
                enemyBullets.remove(i);
                i--;
            }
        }

        //update asteroids
        for (int i = 0; i < asteroids.size(); i++) {
            asteroids.get(i).update(dt);
            if (asteroids.get(i).shouldRemove()) {
                asteroids.remove(i);
                i--;
            }
        }

        //update particles
        for (int i = 0; i < particles.size(); i++) {
            particles.get(i).update(dt);
            if (particles.get(i).shouldRemove()) {
                particles.remove(i);
                i--;
            }
        }

        //check collisions
        checkCollisions();

        //play bg music
        bgTimer += dt;
        if (!player.isHit() && bgTimer >= currentDelay) {
            if (playLowPulse) {
                Jukebox.play("pulselow");
            } else {
                Jukebox.play("pulsehigh");
            }
            playLowPulse = !playLowPulse;
            bgTimer = 0;
        }
    }

    private void checkCollisions() {

        //player-asteroid collision (poly-vs-poly)
        if (!player.isHit()) {
            for (int i = 0; i < asteroids.size(); i++) {
                Asteroid a = asteroids.get(i);
                if (a.intersects(player)) {
                    player.hit();
                    asteroids.remove(i);
                    splitAsteroids(a);
                    Jukebox.play("player_dead");
                    break;
                }
            }
        }


        //bullet-asteroid collision (poly-vs-point)
        for (int i = 0; i < bullets.size(); i++) {
            Bullet b = bullets.get(i);
            for (int j = 0; j < asteroids.size(); j++) {
                Asteroid a = asteroids.get(j);
                if (a.contains(b.getX(), b.getY())) {
                    bullets.remove(i);
                    i--;
                    asteroids.remove(j);//no need to decrement j cuz of break
                    splitAsteroids(a);
                    //increment player score
                    player.incrementScore(a.getScore());
                    Jukebox.play("explode");
                    break;
                }
            }
        }

        // player vs flying saucer
        if (flyingSaucer != null) {
            if (player.intersects(flyingSaucer)) {
                player.hit();
                createParticles(flyingSaucer.getX(), flyingSaucer.getY());
                flyingSaucer = null;
                Jukebox.stop("smallsaucer");
                Jukebox.stop("largesaucer");
                Jukebox.play("explode");
            }
        }

        //bullet - flying saucer collision
        if (flyingSaucer != null) {
            for (int i = 0; i < bullets.size(); i++) {
                Bullet b = bullets.get(i);
                if (flyingSaucer.contains(b.getX(), b.getY())) {
                    bullets.remove(i);
                    createParticles(flyingSaucer.getX(), flyingSaucer.getY());
                    player.incrementScore(flyingSaucer.getScore());
                    flyingSaucer = null;
                    Jukebox.stop("smallsaucer");
                    Jukebox.stop("largesaucer");
                    Jukebox.play("explode");
                    break;
                }
            }
        }

        //player vs enemy bullets
        if (!player.isHit()) {
            for (int i = 0; i < enemyBullets.size(); i++) {
                Bullet b = enemyBullets.get(i);
                if (player.contains(b.getX(), b.getY())) {
                    player.hit();
                    enemyBullets.remove(i);
                    Jukebox.play("explode");
                    break;
                }
            }
        }

        //flying saucer-asteroid
        if (flyingSaucer != null) {
            for (int i = 0; i < asteroids.size(); i++) {
                Asteroid a = asteroids.get(i);
                if (flyingSaucer.intersects(a)) {
                    splitAsteroids(a);
                    createParticles(a.getX(), a.getY());
                    createParticles(flyingSaucer.getX(), flyingSaucer.getY());
                    flyingSaucer = null;
                    Jukebox.stop("smallsaucer");
                    Jukebox.stop("largesaucer");
                    Jukebox.play("explode");
                    break;
                }
            }
        }


        //asteroid-enemy bullet
        for (int i = 0; i < enemyBullets.size(); i++) {
            Bullet b = enemyBullets.get(i);
            for (int j = 0; j < asteroids.size(); j++) {
                Asteroid a = asteroids.get(j);
                if (a.contains(b.getX(), b.getY())) {
                    enemyBullets.remove(i);
                    i--;
                    asteroids.remove(j);//no need to decrement j cuz of break
                    splitAsteroids(a);
                    //increment player score
                    player.incrementScore(a.getScore());
                    Jukebox.play("explode");
                    break;
                }
            }
        }
    }


    public void draw() {

        sb.setProjectionMatrix(Game.camera.combined);
        sr.setProjectionMatrix(Game.camera.combined);

        //draw player
        player.draw(sr);

        //draw bullets
        for (Bullet bullet : bullets) {
            bullet.draw(sr);
        }

        //draw bullets
        for (Asteroid asteroid : asteroids) {
            asteroid.draw(sr);
        }

        //draw flying saucer
        if (flyingSaucer != null) {
            flyingSaucer.draw(sr);
        }

        //draw enemy bullets
        for (Bullet bullet : enemyBullets) {
            bullet.draw(sr);
        }

        //draw particles
        for (Particle particle : particles) {
            particle.draw(sr);
        }

        //draw score
        sb.setColor(1, 1, 1, 1);
        sb.begin();
        font.draw(sb, Long.toString(player.getScore()), 25, 390);
        sb.end();

        //draw lives
        for (int i = 0; i < player.getExtraLives(); i++) {
            hudPlayer.setPosition(30 + i * 13, 360);
            hudPlayer.draw(sr);
        }

    }

    public void handleInput() {
        if (!player.isHit()) {
            player.setLeft(Gdx.input.isKeyPressed(Input.Keys.LEFT));
            player.setRight(Gdx.input.isKeyPressed(Input.Keys.RIGHT));
            player.setUp(Gdx.input.isKeyPressed(Input.Keys.UP));
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                player.shoot();
            }
        }
    }

    public void dispose() {
        sb.dispose();
        sr.dispose();
        font.dispose();
    }
}
