package com.forofour.game.actors;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.forofour.game.handlers.AssetLoader;
import com.forofour.game.handlers.GameConstants;
import com.forofour.game.net.GameClient;

/**
 * Helper class to generate and manipulate PowerUpSlot as a ImageButton
 */
public class PowerUpSlotMaker {

    // X,Y origin is at bottom left of the screen.
    private static float POS_X1 = 160 - 35;
    private static float POS_Y1 = 15;

    private static float POS_X2 = 160 - 15;
    private static float POS_Y2 = 15;

    private static float SIZE_SCALE = (float) 2;

    private static ImageButton powerSlot, buttonSlot;
    private static ImageButton.ImageButtonStyle emptySlotStyle, powerUpStyle1, powerUpStyle2, powerUpStyle3blue, powerUpStyle3red;

    public static ImageButton getPowerSlot(final GameClient client) {

        emptySlotStyle = new ImageButton.ImageButtonStyle();
        emptySlotStyle.up = new TextureRegionDrawable(AssetLoader.powerSlotRegion1);
        emptySlotStyle.down = new TextureRegionDrawable(AssetLoader.powerSlotRegion1);

        powerUpStyle1 = new ImageButton.ImageButtonStyle();
        powerUpStyle1.up = new TextureRegionDrawable(AssetLoader.powerUpRegion1);
        powerUpStyle1.down = new TextureRegionDrawable(AssetLoader.powerUpRegion1);

        powerUpStyle2 = new ImageButton.ImageButtonStyle();
        powerUpStyle2.up = new TextureRegionDrawable(AssetLoader.powerUpRegion2);
        powerUpStyle2.down = new TextureRegionDrawable(AssetLoader.powerUpRegion2);

        powerUpStyle3blue = new ImageButton.ImageButtonStyle();
        powerUpStyle3blue.up = new TextureRegionDrawable(AssetLoader.powerUpRegion3);
        powerUpStyle3blue.down = new TextureRegionDrawable(AssetLoader.powerUpRegion3);

        powerUpStyle3red = new ImageButton.ImageButtonStyle();
        powerUpStyle3red.up = new TextureRegionDrawable(AssetLoader.powerUpRegion4);
        powerUpStyle3red.down = new TextureRegionDrawable(AssetLoader.powerUpRegion4);

        powerSlot = new ImageButton(emptySlotStyle);
        powerSlot.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (powerSlot.getStyle().equals(emptySlotStyle)) {
                    System.out.println("Pressed on Empty Slot");
                } else {
                    System.out.println("Pressed on Power Up Slot");
                    setEmptySlotStyle();
                    client.getMap().getPlayer().powerUp();
                }
            }
        });

        return powerSlot;
    }

    public static ImageButton getButtonSlot() {
        emptySlotStyle = new ImageButton.ImageButtonStyle();
        emptySlotStyle.up = new TextureRegionDrawable(AssetLoader.powerSlotRegion1);
        buttonSlot = new ImageButton(emptySlotStyle);
        return buttonSlot;
    }

    // Necessary wrapper to scale and position the Actor in different screen sizes
    public static Container wrap1(ImageButton ib) {

        Container wrapper = new Container(ib);
        wrapper.setTransform(true);
        wrapper.setPosition(POS_X1 * GameConstants.SCALE_POS, POS_Y1 * GameConstants.SCALE_POS);
        wrapper.setScale(SIZE_SCALE * GameConstants.SCALE_POS / 6, SIZE_SCALE * GameConstants.SCALE_POS / 6);

        return wrapper;
    }

    public static Container wrap2(ImageButton ib) {

        Container wrapper = new Container(ib);
        wrapper.setTransform(true);
        wrapper.setPosition(POS_X2 * GameConstants.SCALE_POS, POS_Y2 * GameConstants.SCALE_POS);
        wrapper.setScale(SIZE_SCALE * GameConstants.SCALE_POS / 6, SIZE_SCALE * GameConstants.SCALE_POS / 6);

        return wrapper;
    }

    public static void setEmptySlotStyle(){
        powerSlot.setStyle(emptySlotStyle);
    }

    // Helper method to update PowerUpSlot to shot acquired PowerUp
    public static void setPowerUpStyle(int powerUpType, int teamId) {
        switch(powerUpType) {
            case 1:
                powerSlot.setStyle(powerUpStyle1);
                break;
            case 2:
                powerSlot.setStyle(powerUpStyle2);
                break;
            case 3:
                if(teamId == 1)
                    powerSlot.setStyle(powerUpStyle3blue);
                else
                    powerSlot.setStyle(powerUpStyle3red);
                break;
            default:
                powerSlot.setStyle(emptySlotStyle);
        }
    }
}
