package com.amilek.managers;

import com.amilek.gamestates.GameState;
import com.amilek.gamestates.MenuState;
import com.amilek.gamestates.PlayState;

public class GameStateManager {
    //current game state
    private GameState gameState;
    public static final int MENU = 0;
    public static final int PLAY = 100;


    public GameStateManager() {
        setState(MENU);

    }

    public void setState(int state) {
        if (gameState != null) gameState.dispose();
        if (state == MENU) {
            gameState = new MenuState(this);
        }
        if (state == PLAY) {
            gameState = new PlayState(this);
        }

    }

    public void update(float dt) {
        gameState.update(dt);
    }

    public void draw() {
        gameState.draw();
    }
}
