package com.amilek.desktop;

import com.amilek.main.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Asteroids";
        config.width = 500;
        config.height = 400;
        config.useGL30 = false;
        config.resizable = false;
        config.samples = 3;

        new LwjglApplication(new Game(), config);
    }
}
