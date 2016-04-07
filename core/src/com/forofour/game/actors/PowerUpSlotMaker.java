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
import com.forofour.game.net.GameClient;

/**
 * Created by seanlim on 18/3/2016.
 */
public class PowerUpSlotMaker {

    // X,Y origin is at bottom left of the screen.
    private static float POS_X1 = 110;
    private static float POS_Y1 = 10;

    private static float SIZE_SCALE = (float) 1;

    private static ImageButton powerSlot;
    private static ImageButton.ImageButtonStyle emptySlotStyle, powerUpStyle1;

//    public static ImageButton getPowerSlot(final GameWorld world) {
//
//        emptySlotStyle = new ImageButton.ImageButtonStyle();
//        emptySlotStyle.up = new TextureRegionDrawable(AssetLoader.powerSlotRegion1);
//        emptySlotStyle.down = new TextureRegionDrawable(AssetLoader.powerSlotRegion1);
//
//        powerUpStyle1 = new ImageButton.ImageButtonStyle();
//        powerUpStyle1.up = new TextureRegionDrawable(AssetLoader.powerUpRegion1);
//        powerUpStyle1.down = new TextureRegionDrawable(AssetLoader.powerUpRegion1);
//
//        powerSlot = new ImageButton(emptySlotStyle);
//        powerSlot.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                if(powerSlot.getStyle().equals(emptySlotStyle)) {
//                    System.out.println("Pressed on Empty Slot");
//                }
//                else if(powerSlot.getStyle().equals(powerUpStyle1)) {
//                    System.out.println("Pressed on Power Up 1 Slot");
//                    setEmptySlotStyle();
//                    world.getPlayer().usePowerUp();
////                    ((GameWorld) actor.getStage()).getPlayer().usePowerUp();
//                }
//
////                if(world.getPlayer() != null)
////                    world.getPlayer().boost();
//            }
//        });
//        return powerSlot;
//    }

    public static ImageButton getPowerSlot(final GameClient client) {

        emptySlotStyle = new ImageButton.ImageButtonStyle();
        emptySlotStyle.up = new TextureRegionDrawable(AssetLoader.powerSlotRegion1);
        emptySlotStyle.down = new TextureRegionDrawable(AssetLoader.powerSlotRegion1);

        powerUpStyle1 = new ImageButton.ImageButtonStyle();
        powerUpStyle1.up = new TextureRegionDrawable(AssetLoader.powerUpRegion1);
        powerUpStyle1.down = new TextureRegionDrawable(AssetLoader.powerUpRegion1);

        powerSlot = new ImageButton(emptySlotStyle);
        powerSlot.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(powerSlot.getStyle().equals(emptySlotStyle)) {
                    System.out.println("Pressed on Empty Slot");
                }
                else if(powerSlot.getStyle().equals(powerUpStyle1)) {
                    System.out.println("Pressed on Power Up 1 Slot");
                    setEmptySlotStyle();
//                    client.getMap().getPlayer().usePowerUp();
//                    ((GameWorld) actor.getStage()).getPlayer().usePowerUp();
                }

//                if(world.getPlayer() != null)
//                    world.getPlayer().boost();
            }
        });
        return powerSlot;
    }

    public static Container wrap1(ImageButton ib) {

        Container wrapper = new Container(ib);
        wrapper.setTransform(true);
        wrapper.setPosition(POS_X1 * GameConstants.SCALE_POS, POS_Y1 * GameConstants.SCALE_POS);
        wrapper.setScale(SIZE_SCALE * GameConstants.SCALE_POS / 6, SIZE_SCALE * GameConstants.SCALE_POS / 6);

        return wrapper;
    }

    public static void setPowerUpStyle1(){
        powerSlot.setStyle(powerUpStyle1);
    }
    public static void setEmptySlotStyle(){
        powerSlot.setStyle(emptySlotStyle);
    }
}
