package com.amilek.entities;

import com.amilek.main.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class PopupText extends SpaceObject {

    private final float LIFETIME = 2;
    private float lifeTimer = 0;
    private String text;

    private SpriteBatch sb;
    private BitmapFont font;

    private boolean remove;

    public PopupText(float x, float y, String text) {

        this.x = x;
        this.y = y;
        this.text = text;

        sb = new SpriteBatch();
        //set font
        FreeTypeFontGenerator gen =
                new FreeTypeFontGenerator(Gdx.files.internal("fonts/Hyperspace Bold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter =
                new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 20;
        font = gen.generateFont(parameter);
        gen.dispose();
    }

    public boolean shouldRemove() {
        return remove;
    }

    public void update(float dt) {
        //x += dx * dt;
        //y += dy * dt;

        wrap();

        lifeTimer += dt;
        if (lifeTimer > LIFETIME) {
            remove = true;
        }
    }

    public void draw() {
        sb.setProjectionMatrix(Game.camera.combined);
        sb.begin();

        GlyphLayout layout = new GlyphLayout();
        layout.setText(font, text);
        float width = layout.width;

        font.draw(sb, text, x - width / 2, y - 30);
        sb.end();
    }
}
