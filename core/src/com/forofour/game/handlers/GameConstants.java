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

    public static final float VIEW2MAP_RATIO = 0.6f;
    public static final float CONTROLS_Y_OFFSET = 15f;

    public static final int MAX_PLAYERS = 4;

    public static final int GAME_DURATION = 60;
    public static final int GAME_DURATION_MS = GAME_DURATION*1000;
    public static final int GAME_DURATION_NS = GAME_DURATION_MS*1000000;

    public static final int DEFAULT_SCORE = 10;
    public static final int DEFAULT_SCORE_PENALTY = 3;

    public static void init() {
        GameConstants.GAME_HEIGHT = Gdx.graphics.getHeight() / (Gdx.graphics.getWidth() / GameConstants.GAME_WIDTH);
        GameConstants.SCALE_POS_X = Gdx.graphics.getWidth() / GameConstants.GAME_WIDTH;
        GameConstants.SCALE_POS_Y = Gdx.graphics.getHeight() / GameConstants.GAME_HEIGHT;
    }
}
