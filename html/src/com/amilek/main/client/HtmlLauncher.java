package com.amilek.main.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.amilek.main.Game;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(Game.WIDTH, Game.HEIGHT);
        }

        @Override
        public ApplicationListener createApplicationListener () {
                return new Game();
        }
}