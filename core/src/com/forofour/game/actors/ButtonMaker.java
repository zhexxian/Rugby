package com.forofour.game.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.forofour.game.gameworlds.GameWorld;
import com.forofour.game.handlers.AssetLoader;
import com.forofour.game.handlers.GameConstants;

/**
 * Make necessary buttons for game play, toss/boost.
 *
 */
public class ButtonMaker {

    // X,Y origin is at bottom left of the screen.
    private static float POS_X1 = 160 - 10;
    private static float POS_Y1 = 13;

    // XY1 and XY2 are used for positioning of Toss and Boost Buttons
    private static float POS_X2 = POS_X1;
    private static float POS_Y2 = POS_Y1;

//    private static float POS_X2 = 160 - 23;
//    private static float POS_Y2 = 10;

    private static float SIZE_SCALE = (float) 0.6;

    private static ImageButton boostButton, tossButton;
    private static ImageButton.ImageButtonStyle imageButtonStyle;

    public static ImageButton getBoostButton(final GameWorld world) {

        imageButtonStyle = new ImageButton.ImageButtonStyle();
        imageButtonStyle.up = new TextureRegionDrawable(AssetLoader.boostRegion);
        imageButtonStyle.down = new TextureRegionDrawable(AssetLoader.boostRegion);
        boostButton = new ImageButton(imageButtonStyle);

        boostButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                world.getPlayer().boost();
            }
        });
        return boostButton;
    }

    public static ImageButton getTossButton(final GameWorld world) {
        imageButtonStyle = new ImageButton.ImageButtonStyle();
        imageButtonStyle.up = new TextureRegionDrawable(AssetLoader.tossRegion);
        imageButtonStyle.down = new TextureRegionDrawable(AssetLoader.tossRegion);
        tossButton = new ImageButton(imageButtonStyle);
        tossButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("Toss");
                world.getPlayer().dropBall();
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
        wrapper.setScale(SIZE_SCALE * scalePosX/6, SIZE_SCALE * scalePosY/6);

        return wrapper;
    }

    public static Container wrap2(ImageButton ib) {
        float scalePosX = Gdx.graphics.getWidth()/ GameConstants.GAME_WIDTH;
        float scalePosY = Gdx.graphics.getHeight()/ GameConstants.GAME_HEIGHT;

        Container wrapper = new Container(ib);
        wrapper.setTransform(true);
        wrapper.setPosition(POS_X2 * scalePosX, POS_Y2 * scalePosY);
        wrapper.setScale(SIZE_SCALE * scalePosX/6, SIZE_SCALE * scalePosY/6);

        return wrapper;
    }
}