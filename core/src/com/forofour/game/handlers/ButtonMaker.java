package com.forofour.game.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.forofour.game.gameworlds.GameWorld;

/**
 * Created by seanlim on 28/2/2016.
 */
public class ButtonMaker {

    private static float POS_X1 = 160 - 10;
    private static float POS_Y1 = 13;
    private static float POS_X2 = 160 - 23;
    private static float POS_Y2 = 10;
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
        float gameWidth = 160;
        float gameHeight = Gdx.graphics.getHeight() / (Gdx.graphics.getWidth() / gameWidth);

        float scalePosX = Gdx.graphics.getWidth()/gameWidth;
        float scalePosY = Gdx.graphics.getHeight()/gameHeight;

        Container wrapper = new Container(ib);
        wrapper.setTransform(true);
        wrapper.setPosition(POS_X1 * scalePosX, POS_Y1 * scalePosY);
        wrapper.setScale(SIZE_SCALE * scalePosX/6, SIZE_SCALE * scalePosY/6);

        return wrapper;
    }

    public static Container wrap2(ImageButton ib) {
        float gameWidth = 160;
        float gameHeight = Gdx.graphics.getHeight() / (Gdx.graphics.getWidth() / gameWidth);

        float scalePosX = Gdx.graphics.getWidth()/gameWidth;
        float scalePosY = Gdx.graphics.getHeight()/gameHeight;

        Container wrapper = new Container(ib);
        wrapper.setTransform(true);
        wrapper.setPosition(POS_X2 * scalePosX, POS_Y2 * scalePosY);
        wrapper.setScale(SIZE_SCALE * scalePosX/6, SIZE_SCALE * scalePosY/6);

        return wrapper;
    }
}
