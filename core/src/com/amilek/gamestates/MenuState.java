package com.amilek.gamestates;


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

public class MenuState extends GameState {

    private SpriteBatch sb;
    private BitmapFont titleFont;
    private BitmapFont font;

    private final String title = "Asteroids";

    private int currentItem;
    private String[] menuItems;


    public MenuState(GameStateManager gsm) {
        super(gsm);
    }

    public void init() {
        sb = new SpriteBatch();

        //set font
        FreeTypeFontGenerator gen =
                new FreeTypeFontGenerator(Gdx.files.internal("fonts/Hyperspace Bold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter =
                new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 56;
        titleFont = gen.generateFont(parameter);
        parameter.size = 20;
        font = gen.generateFont(parameter);
        gen.dispose();

        menuItems = new String[]{
                "Play",
                "Highscores",
                "Quit"
        };

        Save.load();
    }

    public void update(float dt) {

        handleInput();
    }

    public void draw() {

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
        titleFont.dispose();
        font.dispose();
    }
}
