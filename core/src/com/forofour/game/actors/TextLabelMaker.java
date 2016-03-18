package com.forofour.game.actors;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.forofour.game.gameworlds.GameWorld;
import com.forofour.game.handlers.GameConstants;

/**
 * Created by seanlim on 9/3/2016.
 */
public class TextLabelMaker {
    // X,Y origin is at bottom left of the screen.
    // Position of Global Time Label
    private static final float POS_X1 = GameConstants.GAME_WIDTH/2;
    private static final float POS_Y1 = 5;

    // Position of Team Time Label
    private static final float POS_X2 = GameConstants.GAME_WIDTH/2;
    private static final float POS_Y2 = 12;

    private static final float SIZE_SCALE1 = 0.25f;
    private static final float SIZE_SCALE2 = 0.35f;

    private static BitmapFont textFont;
    private static Label.LabelStyle textStyle;
    private static Label textLabel;

    public static Label getTimeLabel(final GameWorld world) {
        textFont = new BitmapFont(true);

        textStyle = new Label.LabelStyle();
        textStyle.font = textFont;

        textLabel = new Label("", textStyle);
        return textLabel;
    }

    public static Container wrapGlobalTime(Label label) {

        Container wrapper = new Container(label);
        wrapper.setTransform(true);
        wrapper.setPosition(POS_X1 * GameConstants.SCALE_POS, POS_Y1 * GameConstants.SCALE_POS);
        wrapper.setScale(SIZE_SCALE1 * GameConstants.SCALE_POS, -SIZE_SCALE1 * GameConstants.SCALE_POS); // To flip along the horizontal axis

        return wrapper;
    }

    public static Container wrapTeamScore(Label label) {

        Container wrapper = new Container(label);
        wrapper.setTransform(true);
        wrapper.setPosition(POS_X2 * GameConstants.SCALE_POS, POS_Y2 * GameConstants.SCALE_POS);
        wrapper.setScale(SIZE_SCALE2 * GameConstants.SCALE_POS, -SIZE_SCALE2 * GameConstants.SCALE_POS); // To flip along the horizontal axis

        return wrapper;
    }

}
