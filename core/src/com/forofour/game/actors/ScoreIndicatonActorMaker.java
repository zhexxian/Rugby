package com.forofour.game.actors;

import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.forofour.game.handlers.AssetLoader;
import com.forofour.game.handlers.GameConstants;

/**
 * Helper class to generate and manipulate Score Indication System
 */
public class ScoreIndicatonActorMaker {

    private static float SCORE_LINE_WIDTH = 50;
    private static float SCORE_LINE_HEIGHT = 8;
    private static float SCORE_LINE_X = GameConstants.GAME_WIDTH/2 - SCORE_LINE_WIDTH/2;
    private static float SCORE_LINE_Y = 10;
    private static float SCORE_LINE_SCALE = 1f;

    private static float START_LINE = SCORE_LINE_X;
    private static float END_LINE = SCORE_LINE_X + SCORE_LINE_WIDTH - 7;
    private static float SCORE_LENGTH = END_LINE - START_LINE;

    private static float INDICATOR_A_WIDTH = 5;
    private static float INDICATOR_A_HEIGHT = INDICATOR_A_WIDTH;
    private static float INDICATOR_A_X = START_LINE;
    private static float INDICATOR_A_Y = SCORE_LINE_Y + 2.6f;

    private static float INDICATOR_B_WIDTH = 5;
    private static float INDICATOR_B_HEIGHT = INDICATOR_B_WIDTH;
    private static float INDICATOR_B_X = END_LINE;
    private static float INDICATOR_B_Y = SCORE_LINE_Y + 2.6f;

    private static float INDICATOR_SCALE = 1f;

    private static Image scoreLine, scoreA, scoreB;

    public static Image getScoreLine() {
        scoreLine = new Image(AssetLoader.scoreLine);
        scoreLine.setSize(SCORE_LINE_WIDTH, SCORE_LINE_HEIGHT);
        scoreLine.setPosition(SCORE_LINE_X * GameConstants.SCALE_POS, SCORE_LINE_Y * GameConstants.SCALE_POS);
        scoreLine.setScale(SCORE_LINE_SCALE * GameConstants.SCALE_POS);

        return scoreLine;
    }

    public static Image getIndicatorA() {
        scoreA = new Image(AssetLoader.scoreIndicatorA);
        scoreA.setSize(INDICATOR_A_WIDTH, INDICATOR_A_HEIGHT);
        scoreA.setPosition(INDICATOR_A_X * GameConstants.SCALE_POS, INDICATOR_A_Y * GameConstants.SCALE_POS);
        scoreA.setScale(INDICATOR_SCALE * GameConstants.SCALE_POS);

        return scoreA;
    }

    public static Image getIndicatorB() {
        scoreB = new Image(AssetLoader.scoreIndicatorB);
        scoreB.setSize(INDICATOR_B_WIDTH, INDICATOR_B_HEIGHT);
        scoreB.setPosition(INDICATOR_B_X * GameConstants.SCALE_POS, INDICATOR_B_Y * GameConstants.SCALE_POS);
        scoreB.setScale(INDICATOR_SCALE * GameConstants.SCALE_POS);

        return scoreB;
    }

    // Necessary wrapper to scale and position the Actor in different screen sizes
    public static Container wrapScoreLine(Image image) {

        Container wrapper = new Container(image);
        wrapper.setTransform(true);
        wrapper.setSize(SCORE_LINE_WIDTH, SCORE_LINE_HEIGHT);
        wrapper.setPosition(SCORE_LINE_X * GameConstants.SCALE_POS, SCORE_LINE_Y * GameConstants.SCALE_POS);
        wrapper.setScale(SCORE_LINE_SCALE * GameConstants.SCALE_POS, SCORE_LINE_SCALE * GameConstants.SCALE_POS); // To flip along the horizontal axis

        return wrapper;
    }

    // Necessary wrapper to scale and position the Actor in different screen sizes
    public static Container wrapIndicatorA(Image image) {

        Container wrapper = new Container(image);
        wrapper.setTransform(true);
        wrapper.setSize(INDICATOR_A_WIDTH, INDICATOR_A_HEIGHT);
        wrapper.setPosition(INDICATOR_A_X * GameConstants.SCALE_POS, INDICATOR_A_Y * GameConstants.SCALE_POS);
        wrapper.setScale(INDICATOR_SCALE * GameConstants.SCALE_POS, INDICATOR_SCALE * GameConstants.SCALE_POS); // To flip along the horizontal axis

        return wrapper;
    }

    // Necessary wrapper to scale and position the Actor in different screen sizes
    public static Container wrapIndicatorB(Image image) {

        Container wrapper = new Container(image);
        wrapper.setTransform(true);
        wrapper.setSize(INDICATOR_B_WIDTH, INDICATOR_B_HEIGHT);
        wrapper.setPosition(INDICATOR_B_X * GameConstants.SCALE_POS, INDICATOR_B_Y * GameConstants.SCALE_POS);
        wrapper.setScale(INDICATOR_SCALE * GameConstants.SCALE_POS, -INDICATOR_SCALE * GameConstants.SCALE_POS); // To flip along the horizontal axis

        return wrapper;
    }

    // Helper to update the position of TeamIndicator
    public static void updateIndicatorPosition(Image indicatorTeam, float percentage) {
        if(percentage >= 0 && percentage <= 1) {
            float x = START_LINE + SCORE_LENGTH * percentage;
            indicatorTeam.setPosition(x * GameConstants.SCALE_POS, INDICATOR_A_Y * GameConstants.SCALE_POS);
        }
    }
}
