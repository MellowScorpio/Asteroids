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

public class HighScoreState extends GameState {

    private SpriteBatch sb;
    private BitmapFont font;

    private long[] highScores;
    private String[] names;

    public HighScoreState(GameStateManager gsm) {
        super(gsm);

    }

    @Override
    public void init() {

        sb = new SpriteBatch();

        //set font
        FreeTypeFontGenerator gen =
                new FreeTypeFontGenerator(Gdx.files.internal("fonts/Hyperspace Bold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter =
                new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 20;
        font = gen.generateFont(parameter);
        gen.dispose();

        Save.load();
        highScores = Save.gd.getHighScores();
        names = Save.gd.getNames();
    }

    @Override
    public void update(float dt) {
        handleInput();
    }

    @Override
    public void draw() {

        sb.setProjectionMatrix(Game.camera.combined);
        sb.begin();

        //draw title
        String text = "High Scores";
        GlyphLayout layout = new GlyphLayout();
        layout.setText(font, text);
        float width = layout.width;

        font.draw(sb, text, (Game.WIDTH - width) / 2, 300);

        //draw high scores
        for (int i = 0; i < highScores.length; i++) {
            text = String.format(
                    "%2d. %7s %s",
                    i + 1,
                    highScores[i],
                    names[i]
            );
            layout.setText(font, text);
            width = layout.width;
            font.draw(sb, text, (Game.WIDTH - width) / 2, 270 - 20 * i);
        }
        sb.end();
    }

    @Override
    public void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) ||
                Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            gsm.setState(GameStateManager.MENU);
        }
    }

    @Override
    public void dispose() {
        sb.dispose();
        font.dispose();

    }
}
