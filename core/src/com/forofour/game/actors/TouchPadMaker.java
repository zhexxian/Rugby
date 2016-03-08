package com.forofour.game.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.forofour.game.gameworlds.GameWorld;
import com.forofour.game.handlers.GameConstants;

/**
 * Created by seanlim on 28/2/2016.
 */
public class TouchPadMaker {

    private static float POS_X = 15;
    private static float POS_Y = 15;
    private static float SIZE_SCALE = (float) 0.6;
    private static Touchpad touchpad;
    private static Touchpad.TouchpadStyle touchpadStyle;
    private static Skin touchpadSkin;
    private static Drawable touchBackground;
    private static Drawable touchKnob;

    public static Touchpad make(GameWorld world) {
        //Create a touchpad skin
        touchpadSkin = new Skin();
        //Set background image
        touchpadSkin.add("touchBackground", new Texture("data/touchBackground.png"));
        //Set knob image
        touchpadSkin.add("touchKnob", new Texture("data/touchKnob.png"));
        //Create TouchPad Style
        touchpadStyle = new Touchpad.TouchpadStyle();
        //Create Drawable's from TouchPad skin
        touchBackground = touchpadSkin.getDrawable("touchBackground");
        touchKnob = touchpadSkin.getDrawable("touchKnob");
        //Apply the Drawables to the TouchPad Style
        touchpadStyle.background = touchBackground;
        touchpadStyle.knob = touchKnob;
        //Create new TouchPad with the created style
        touchpad = new Touchpad(10, touchpadStyle);

        touchpad.setDebug(true);
        touchpad.setBounds(50, 50, 150, 150); // Initial position and bounds. Will be overwritten by wrapper

        return touchpad;
    }

    public static Container wrap(Touchpad tp) {

        /* Required wrapper to allow item to scale
         */
        Container wrapper = new Container(tp);
        wrapper.setTransform(true);
        wrapper.setPosition(POS_X * GameConstants.SCALE_POS_X, POS_Y * GameConstants.SCALE_POS_Y);
        wrapper.setScale(SIZE_SCALE * GameConstants.SCALE_POS_X/6, SIZE_SCALE * GameConstants.SCALE_POS_Y/6);

        return wrapper;
    }
}
