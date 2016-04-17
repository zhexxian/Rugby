package com.forofour.game.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.forofour.game.handlers.GameConstants;

/**
 * Created by Anhnonymouz on 18/4/2016.
 */
public class TutorialMaker {

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



}
