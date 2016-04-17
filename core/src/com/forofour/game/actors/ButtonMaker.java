package com.forofour.game.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.forofour.game.handlers.AssetLoader;
import com.forofour.game.handlers.GameConstants;
import com.forofour.game.net.GameClient;
import com.forofour.game.tutorialMode.TutorialStates;

/**
 * Make necessary buttons for game play, toss/boost.
 *  Called by the MainOverlay
 */
public class ButtonMaker {

    // X,Y origin is at bottom left of the screen.
    private static float POS_X1 = 160 - 15;
    private static float POS_Y1 = 15;

    // XY1 and XY2 are used for positioning of Toss and Boost Buttons
    private static float POS_X2 = POS_X1;
    private static float POS_Y2 = POS_Y1;

//    private static float POS_X2 = 160 - 23;
//    private static float POS_Y2 = 10;

    private static float SIZE_SCALE = (float) 0.2;

    private static ImageButton boostButton, tossButton;
    private static ImageButton.ImageButtonStyle imageButtonStyle;


    public static ImageButton getBoostButton(final GameClient client, final TutorialStates tutorialStates) {

        imageButtonStyle = new ImageButton.ImageButtonStyle();
        imageButtonStyle.up = new TextureRegionDrawable(AssetLoader.boostRegionUp);
//        imageButtonStyle.down = new TextureRegionDrawable(AssetLoader.boostRegionUp);
        boostButton = new ImageButton(imageButtonStyle);

        boostButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("BoostButton", "Button Pressed");
                if(client.getMap().getPlayer() != null) {
                    Gdx.app.log("BoostButton", "Button Pressed");
                    client.getMap().getPlayer().boost();
                }
                if(tutorialStates != null) {
                    Gdx.app.log("BoostButton", "Button Pressed");
                    tutorialStates.usedBoost();
                }
                AssetLoader.fartBoostSound.play(GameConstants.SOUND_VOLUME);
            }
        });
        return boostButton;
    }

    public static ImageButton getTossButton(final GameClient client) {
        imageButtonStyle = new ImageButton.ImageButtonStyle();
        imageButtonStyle.up = new TextureRegionDrawable(AssetLoader.tossRegionUp);
//        imageButtonStyle.down = new TextureRegionDrawable(AssetLoader.tossRegionUp);
        tossButton = new ImageButton(imageButtonStyle);
        tossButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("Toss");
                if(client.getMap().getPlayer() != null)
                    client.getMap().getPlayer().dropBall();
            }
        });
        return tossButton;
    }

    public static Container wrap1(ImageButton ib) {
        float scalePosX = Gdx.graphics.getWidth()/ GameConstants.GAME_WIDTH;
        float scalePosY = Gdx.graphics.getHeight()/ GameConstants.GAME_HEIGHT;

        Container wrapper = new Container(ib);
        wrapper.setTransform(true);
        wrapper.setPosition(POS_X1 * scalePosX, POS_Y1 * scalePosY);
        wrapper.setScale(SIZE_SCALE * scalePosX, SIZE_SCALE * scalePosY);

        return wrapper;
    }

    public static Container wrap2(ImageButton ib) {
        float scalePosX = Gdx.graphics.getWidth()/ GameConstants.GAME_WIDTH;
        float scalePosY = Gdx.graphics.getHeight()/ GameConstants.GAME_HEIGHT;

        Container wrapper = new Container(ib);
        wrapper.setTransform(true);
        wrapper.setPosition(POS_X2 * scalePosX, POS_Y2 * scalePosY);
        wrapper.setScale(SIZE_SCALE * scalePosX, SIZE_SCALE * scalePosY);

        return wrapper;
    }
}
