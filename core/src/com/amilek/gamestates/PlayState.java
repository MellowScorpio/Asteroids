package com.amilek.gamestates;

import com.amilek.entities.Asteroid;
import com.amilek.entities.Bullet;
import com.amilek.entities.Particle;
import com.amilek.entities.Player;
import com.amilek.main.Game;
import com.amilek.managers.GameStateManager;
import com.amilek.managers.Jukebox;
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

    }

    private void createParticles(float x, float y) {
        for (int i = 0; i < 6; i++) {
            particles.add(new Particle(x, y));
        }
    }

    private void splitAsteroids(Asteroid asteroid) {

        createParticles(asteroid.getX(), asteroid.getY());

        numAsteroidsLeft--;
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
                gsm.setState(GameStateManager.MENU);
            }
            player.reset();
            player.looseLive();
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

    }


    public void draw() {

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
        player.setLeft(Gdx.input.isKeyPressed(Input.Keys.LEFT));
        player.setRight(Gdx.input.isKeyPressed(Input.Keys.RIGHT));
        player.setUp(Gdx.input.isKeyPressed(Input.Keys.UP));
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            player.shoot();
        }
    }

    public void dispose() {
    }
}
