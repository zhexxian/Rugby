package com.forofour.game.handlers;

import com.badlogic.gdx.Gdx;

/**
 * Contains all game constants used.
 * Only WIDTH/HEIGHT has to be initialized at the beginning
 */
public class GameConstants {
    public static float GAME_WIDTH = 160;
    public static float GAME_HEIGHT;

    public static float SCALE_POS_X;
    public static float SCALE_POS_Y;

    public static float VIEW2MAP_RATIO = 0.6f;
    public static float CONTROLS_Y_OFFSET = 15f;

    public static void init() {
        GameConstants.GAME_HEIGHT = Gdx.graphics.getHeight() / (Gdx.graphics.getWidth() / GameConstants.GAME_WIDTH);
        GameConstants.SCALE_POS_X = Gdx.graphics.getWidth() / GameConstants.GAME_WIDTH;
        GameConstants.SCALE_POS_Y = Gdx.graphics.getHeight() / GameConstants.GAME_HEIGHT;
    }
}
