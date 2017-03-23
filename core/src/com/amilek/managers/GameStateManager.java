package com.amilek.managers;

import com.amilek.gamestates.*;

public class GameStateManager {
    //current game state
    private GameState gameState;
    public static final int MENU = 0;
    public static final int PLAY = 100;
    public static final int HIGHSCORE = 555;
    public static final int GAMEOVER = 666;


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
        if (state == HIGHSCORE) {
            gameState = new HighScoreState(this);
        }
        if (state == GAMEOVER) {
            gameState = new GameOverState(this);
        }

    }

    public void update(float dt) {
        gameState.update(dt);
    }

    public void draw() {
        gameState.draw();
    }
}
