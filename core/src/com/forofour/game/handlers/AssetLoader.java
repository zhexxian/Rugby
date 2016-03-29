package com.forofour.game.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by seanlim on 27/2/2016.
 */
public class AssetLoader {

    public static Texture texture;
    public static Texture bg, wallVert, wallHori, menu;
    public static Texture playerA, playerB, ball;

    public static Texture powerUp;

    public static Texture boostButton, tossButton;
    public static Texture powerSlot1, powerUp1, powerUp2, powerUp3, powerUp4;

    public static TextureRegion bgMenu;
    public static TextureRegion bgRegion;
    public static TextureRegion playerRegionA, playerRegionB;

    public static TextureRegion boostRegion, tossRegion;
    public static TextureRegion powerSlotRegion1, powerUpRegion1, powerUpRegion2, powerUpRegion3, powerUpRegion4;

    public static void load() {
//
//        texture = new Texture(Gdx.files.internal(null));
//        wallVert = new Texture(Gdx.files.internal(null));
//        wallHori = new Texture(Gdx.files.internal(null));

        menu = new Texture(Gdx.files.internal("sprites/start-screen.png"));
        playerA = new Texture(Gdx.files.internal("sprites/individual babies/blue-straight-1.png"));
        playerB = new Texture(Gdx.files.internal("sprites/individual babies/red-straight-1.png"));
        ball = new Texture(Gdx.files.internal("sprites/bottle-black-outline-upside-down.png"));
        bg = new Texture(Gdx.files.internal("sprites/background test/random-nursery-background-2.png"));
        powerUp = new Texture(Gdx.files.internal("data/powerUp.png"));


        bgMenu = new TextureRegion(menu);
        bgRegion = new TextureRegion(bg);
        //bgRegion.flip(false, true);
        playerRegionA = new TextureRegion(playerA);
        playerRegionB = new TextureRegion(playerB);

        // Buttons
        boostButton = new Texture(Gdx.files.internal("sprites/buttons/Boost-activate.png"));
        boostRegion = new TextureRegion(boostButton);
        tossButton = new Texture(Gdx.files.internal("sprites/buttons/Toss-activate.png"));
        tossRegion = new TextureRegion(tossButton);

        powerSlot1 = new Texture(Gdx.files.internal("data/power_slot.png"));
        powerSlotRegion1 = new TextureRegion(powerSlot1);
        powerUp1 = new Texture(Gdx.files.internal("data/power_slot_filled1.png"));
        powerUpRegion1 = new TextureRegion(powerUp1);
        powerUp2 = new Texture(Gdx.files.internal("data/power2.png"));
        powerUpRegion2 = new TextureRegion(powerUp2);
        powerUp3 = new Texture(Gdx.files.internal("data/power3.png"));
        powerUpRegion3 = new TextureRegion(powerUp3);
        powerUp4 = new Texture(Gdx.files.internal("data/power4.png"));
        powerUpRegion4 = new TextureRegion(powerUp4);

    }

    public static void dispose() {
        // We must dispose of the texture when we are finished.
        playerA.dispose();
        playerB.dispose();
        ball.dispose();
        bg.dispose();
        powerUp.dispose();

        boostButton.dispose();
        tossButton.dispose();

        powerSlot1.dispose();
        powerUp1.dispose();
        powerUp2.dispose();
        powerUp3.dispose();
        powerUp4.dispose();
    }
}
