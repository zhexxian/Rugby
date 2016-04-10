/* This contains all game constants used.
 * Only game width and height have to be initialized at the beginning
 * (by initializing SCALE_POS and GAME_HEIGHT with assigned GAME_WIDTH value
 */

package com.forofour.game.handlers;

import com.badlogic.gdx.Gdx;


public class GameConstants {
    //game screen size
    public static float GAME_WIDTH = 160;
    public static float GAME_HEIGHT;

    public static float SCALE_POS;

    public static final float VIEW2MAP_RATIO = 0.6f;
    public static final float CONTROLS_Y_OFFSET = 15f;

    //number of players
    public static final int MAX_PLAYERS = 2;
    public static final int[][] PLAYER_POSITION = new int[4][2];

    //game duration
    public static final int GAME_DURATION = 120;
    public static final int GAME_DURATION_MS = GAME_DURATION*1000;
    public static final int GAME_DURATION_NS = GAME_DURATION_MS*1000000;

    //scores
    public static final int DEFAULT_SCORE = 1;
    public static final int DEFAULT_SCORE_PENALTY = 3;

    public static void init() {
        GameConstants.SCALE_POS = Gdx.graphics.getWidth() / GameConstants.GAME_WIDTH; //scale factor
        GameConstants.GAME_HEIGHT = Gdx.graphics.getHeight() / GameConstants.SCALE_POS; //calculated game height
        PLAYER_POSITION[0][0] = 10; PLAYER_POSITION[0][1] = 10;
        PLAYER_POSITION[1][0] = 150; PLAYER_POSITION[1][1] = 10;
        PLAYER_POSITION[2][0] = 10; PLAYER_POSITION[2][1] = 90;
        PLAYER_POSITION[3][0] = 150; PLAYER_POSITION[3][1] = 90;
    }
}
