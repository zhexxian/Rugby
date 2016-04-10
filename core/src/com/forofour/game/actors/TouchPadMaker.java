package com.forofour.game.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.forofour.game.gameworlds.GameWorld;
import com.forofour.game.handlers.GameConstants;
import com.forofour.game.net.GameClient;

/**
 * Created by seanlim on 28/2/2016.
 */
public class TouchPadMaker {

    private static float POS_X = 20;
    private static float POS_Y = 20;
    private static float SIZE_SCALE = 0.2f;
    private static Touchpad touchpad;
    private static Touchpad.TouchpadStyle touchpadStyle;
    private static Skin touchpadSkin;
    private static Drawable touchBackground;
    private static Drawable touchKnob;

/*    public static Touchpad make(GameWorld world) {
        //Create a touchpad skin
        touchpadSkin = new Skin();
        //Set background image
        touchpadSkin.add("touchBackground", new Texture("sprites/buttons/joystick-out-black.png"));
        //Set knob image
        touchpadSkin.add("touchKnob", new Texture("sprites/buttons/joystick-in-black.png"));
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
    }*/

    public static Touchpad make(GameClient client) {
        //Create a touchpad skin
        touchpadSkin = new Skin();
        //Set background image
        touchpadSkin.add("touchBackground", new Texture("sprites/buttons/joystick-out-black.png"));
        //Set knob image
        touchpadSkin.add("touchKnob", new Texture("sprites/buttons/joystick-in-black.png"));
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

        touchpad.setDebug(false);
        touchpad.setBounds(50, 50, 150, 150); // Initial position and bounds. Will be overwritten by wrapper

        return touchpad;
    }

    public static Container wrap(Touchpad tp) {

        /* Required wrapper to allow item to scale
         */
        Container wrapper = new Container(tp);
        wrapper.setTransform(true);
        wrapper.setPosition(POS_X * GameConstants.SCALE_POS, POS_Y * GameConstants.SCALE_POS);
        wrapper.setScale(SIZE_SCALE * GameConstants.SCALE_POS, SIZE_SCALE * GameConstants.SCALE_POS);

        return wrapper;
    }
}
