package com.amilek.main;

import com.amilek.managers.GameStateManager;
import com.amilek.managers.Jukebox;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class Game extends ApplicationAdapter {
    public static int WIDTH;
    public static int HEIGHT;

    public static OrthographicCamera camera;

    private GameStateManager gsm;

    @Override
    public void create() {
        WIDTH = Gdx.graphics.getWidth();
        HEIGHT = Gdx.graphics.getHeight();

        camera = new OrthographicCamera(WIDTH, HEIGHT);
        camera.translate(WIDTH / 2, HEIGHT / 2);
        camera.update();//commits translate changes to camera object

        Jukebox.load("sounds/explode.ogg", "explode");
        //Jukebox.load("sounds/player_dead.wav", "player_dead");
        Jukebox.load("sounds/shoot.ogg", "shoot");
        Jukebox.load("sounds/thruster.ogg", "thruster");
        Jukebox.load("sounds/saucershoot.ogg", "saucershoot");
        Jukebox.load("sounds/pulselow.ogg", "pulselow");
        Jukebox.load("sounds/pulsehigh.ogg", "pulsehigh");
        Jukebox.load("sounds/extralife.ogg", "extralife");
        Jukebox.load("sounds/largesaucer.ogg", "largesaucer");
        Jukebox.load("sounds/smallsaucer.ogg", "smallsaucer");


        gsm = new GameStateManager();

    }

    // sort of game loop
    @Override
    public void render() {

        //clears screen black
        Gdx.gl.glClearColor(0, 0, 0, 1);
        //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));

        gsm.update(Gdx.graphics.getDeltaTime());//passes time since last render to state update
        gsm.draw();
    }

    @Override
    public void dispose() {
    }

    public void resize(int width, int height) {
    }

    public void pause() {
    }

    public void resume() {
    }
}
