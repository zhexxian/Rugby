package com.forofour.game.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.forofour.game.handlers.GameConstants;

/**
 * Helper class to Generate the Images and Buttons for GameEnd state
 *  Required containers to position and size the actors in varying screen sizes
 */
public class GameOverMaker {

    private BitmapFont endFont;

    public static Container wrapBlackLayer(Image bg) {
        // X,Y origin is at bottom left of the screen.
        float POS_X1 = GameConstants.GAME_WIDTH/2;
        float POS_Y1 = GameConstants.GAME_HEIGHT/2;

        float scalePosX = Gdx.graphics.getWidth()/ GameConstants.GAME_WIDTH;
        float scalePosY = Gdx.graphics.getHeight()/ GameConstants.GAME_HEIGHT;



        Container wrapper = new Container(bg);
        wrapper.setTransform(true);
        // set the width of overlay to be 1.2 times of the height; height follows screen height
        wrapper.setSize(GameConstants.GAME_HEIGHT * 1.2f, GameConstants.GAME_HEIGHT);

        // centralize the overlay
        wrapper.setPosition(0.5f*scalePosX*(GameConstants.GAME_WIDTH-GameConstants.GAME_HEIGHT*1.2f), 0);
        wrapper.setScale(scalePosX, scalePosY);

        return wrapper;
    }

    public static Container wrapRematchButton(Image rm) {
        // X,Y origin is at bottom left of the screen.
        float POS_X1 = GameConstants.GAME_WIDTH/2;
        float POS_Y1 = GameConstants.GAME_HEIGHT/2 + rm.getHeight();

        float scalePosX = Gdx.graphics.getWidth()/ GameConstants.GAME_WIDTH;
        float scalePosY = Gdx.graphics.getHeight()/ GameConstants.GAME_HEIGHT;

        Container wrapper = new Container(rm);
        wrapper.setTransform(true);

        // set button size
        wrapper.setSize(0.25f*GameConstants.GAME_WIDTH, 0.15f*GameConstants.GAME_HEIGHT);

        // set button position (centralized horizontally)
        wrapper.setPosition(0.5f*scalePosX*(0.75f*GameConstants.GAME_WIDTH), scalePosY*0.27f*GameConstants.GAME_HEIGHT);
        wrapper.setScale(scalePosX, scalePosY);

        return wrapper;
    }

    public static Container wrapMenuButton(Image mn) {
        // X,Y origin is at bottom left of the screen.
        float POS_X2 = GameConstants.GAME_WIDTH/2;
        float POS_Y2 = GameConstants.GAME_HEIGHT/2 - mn.getHeight();

        float scalePosX = Gdx.graphics.getWidth()/ GameConstants.GAME_WIDTH;
        float scalePosY = Gdx.graphics.getHeight()/ GameConstants.GAME_HEIGHT;

        Container wrapper = new Container(mn);
        wrapper.setTransform(true);

        // set button size
        wrapper.setSize(0.25f*GameConstants.GAME_WIDTH, 0.15f*GameConstants.GAME_HEIGHT);

        // set button position (centralized horizontally)
        wrapper.setPosition(0.5f*scalePosX*(0.75f*GameConstants.GAME_WIDTH), scalePosY*0.1f*GameConstants.GAME_HEIGHT);
        wrapper.setScale(scalePosX, scalePosY);

        return wrapper;
    }
}
