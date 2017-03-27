package com.amilek.gamestates;


import com.amilek.entities.Asteroid;
import com.amilek.managers.GameStateManager;
import com.amilek.main.Game;
import com.amilek.managers.Save;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;

public class MenuState extends GameState {

    private SpriteBatch sb;
    private ShapeRenderer sr;

    private BitmapFont titleFont;
    private BitmapFont font;

    private final String title = "Asteroids";

    private int currentItem;
    private String[] menuItems;

    private ArrayList<Asteroid> asteroids;


    public MenuState(GameStateManager gsm) {
        super(gsm);
    }

    public void init() {
        sb = new SpriteBatch();
        sr = new ShapeRenderer();

        //set font
        /*
        FreeTypeFontGenerator gen =
                new FreeTypeFontGenerator(Gdx.files.internal("fonts/Hyperspace Bold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter =
                new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 56;
        titleFont = gen.generateFont(parameter);
        parameter.size = 20;
        //font using generator - html5 incompatibile
        font = gen.generateFont(parameter);
        gen.dispose();
        */

        titleFont = new BitmapFont(Gdx.files.internal("fonts/hyperspace56px.fnt"));
        titleFont.setColor(Color.WHITE);

        // font using bitmap font - html5 friendly
        font = new BitmapFont(Gdx.files.internal("fonts/hyperspace.fnt"));
        font.setColor(Color.WHITE);

        menuItems = new String[]{
                "Play",
                "Highscores",
                "Quit"
        };

        Save.load();

        asteroids = new ArrayList<Asteroid>();
        for(int i = 0; i < 6; i++){
            asteroids.add(
                    new Asteroid(
                            MathUtils.random(Game.WIDTH),
                            MathUtils.random(Game.HEIGHT),
                            Asteroid.LARGE
                    )
            );
        }
    }

    public void update(float dt) {

        handleInput();

        for(Asteroid a:asteroids){
            a.update(dt);
        }
    }

    public void draw() {

        sb.setProjectionMatrix(Game.camera.combined);
        sr.setProjectionMatrix(Game.camera.combined);


        for(Asteroid a:asteroids){
            a.draw(sr);
        }

        sb.begin();

        GlyphLayout layout = new GlyphLayout();
        layout.setText(titleFont, title);
        float width = layout.width;

        //draw title
        titleFont.draw(
                sb,
                title,
                (Game.WIDTH - width) / 2,
                300
        );

        //draw menu
        for (int i = 0; i < menuItems.length; i++) {
            layout.setText(font, menuItems[i]);
            width = layout.width;
            if (currentItem == i) font.setColor(Color.RED);
            else font.setColor(Color.WHITE);
            font.draw(
                    sb,
                    menuItems[i],
                    (Game.WIDTH - width) / 2,
                    180 - 35 * i
            );
        }

        sb.end();
    }

    public void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            if (currentItem > 0) {
                currentItem--;
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            if (currentItem < menuItems.length - 1) {
                currentItem++;
            }
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)){
            select();
        }
    }

    private void select(){
        //play
        if(currentItem == 0){
            gsm.setState(GameStateManager.PLAY);
        } else if(currentItem == 1){
            gsm.setState(GameStateManager.HIGHSCORE);
        } else if(currentItem == 2){
            Gdx.app.exit();
        }
    }

    public void dispose() {
        sb.dispose();
        sr.dispose();
        titleFont.dispose();
        font.dispose();
    }
}
