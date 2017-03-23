package com.amilek.gamestates;

import com.amilek.main.Game;
import com.amilek.managers.GameStateManager;
import com.amilek.managers.Save;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class GameOverState extends GameState {

    private SpriteBatch sb;
    private ShapeRenderer sr;

    private boolean newHighScore;
    private char[] newName;
    private int currentChar;

    private BitmapFont gameOverFont;
    private BitmapFont font;


    public GameOverState(GameStateManager gsm) {
        super(gsm);
    }

    @Override
    public void init() {
        sb = new SpriteBatch();
        sr = new ShapeRenderer();

        newHighScore = Save.gd.isHighScore(Save.gd.getTenativeScore());
        if (newHighScore) {
            newName = new char[]{'A', 'A', 'A'};
        }

        //set font
        FreeTypeFontGenerator gen =
                new FreeTypeFontGenerator(Gdx.files.internal("fonts/Hyperspace Bold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter =
                new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 32;
        gameOverFont = gen.generateFont(parameter);
        parameter.size = 20;
        font = gen.generateFont(parameter);
        gen.dispose();
    }

    @Override
    public void update(float dt) {
        handleInput();
    }

    @Override
    public void draw() {
        sb.setProjectionMatrix(Game.camera.combined);
        sr.setProjectionMatrix(Game.camera.combined);

        sb.begin();

        //draw title
        String text = "Game Over";
        GlyphLayout layout = new GlyphLayout();
        layout.setText(gameOverFont, text);
        float width = layout.width;

        gameOverFont.draw(sb, text, (Game.WIDTH - width) / 2, 220);

        if (!newHighScore) {
            sb.end();
            return;
        } else {
            text = "New High Score: " + Save.gd.getTenativeScore();
            layout.setText(font, text);
            width = layout.width;
            font.draw(sb, text, (Game.WIDTH - width) / 2, 180);

            for (int i = 0; i < newName.length; i++) {
                font.draw(
                        sb,
                        Character.toString(newName[i]),
                        230 + 14 * i,
                        120
                );
            }
            sb.end();
        }

        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.line(
                230 + 14 * currentChar,
                100,
                244 + 14 * currentChar,
                100
                );
        sr.end();


    }

    @Override
    public void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (newHighScore) {
                Save.gd.addHighScore(
                        Save.gd.getTenativeScore(),
                        new String(newName)
                );
                Save.save();
            }
            gsm.setState(GameStateManager.HIGHSCORE);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            if (newName[currentChar] == ' ') {
                newName[currentChar] = 'Z';
            } else {
                newName[currentChar]--;
                if (newName[currentChar] < 'A') {
                    newName[currentChar] = ' ';
                }
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            if (newName[currentChar] == ' ') {
                newName[currentChar] = 'A';
            } else {
                newName[currentChar]++;
                if (newName[currentChar] > 'Z') {
                    newName[currentChar] = ' ';
                }
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            if (currentChar < newName.length - 1) {
                currentChar++;
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            if (currentChar > 0) {
                currentChar--;
            }
        }
    }

    @Override
    public void dispose() {
        sb.dispose();
        sr.dispose();
        gameOverFont.dispose();
        font.dispose();

    }
}
