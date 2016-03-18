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
    public static Texture bg, wallVert, wallHori;
    public static Texture playerA, playerB, ball;

    public static Texture powerUp;

    public static Texture boostButton, tossButton;
    public static Texture powerSlot1, powerUp1, powerUp2, powerUp3, powerUp4;

    public static TextureRegion bgRegion;
    public static TextureRegion playerRegionA, playerRegionB;

    public static TextureRegion boostRegion, tossRegion;
    public static TextureRegion powerSlotRegion1, powerUpRegion1, powerUpRegion2, powerUpRegion3, powerUpRegion4;

    public static void load() {
//
//        texture = new Texture(Gdx.files.internal(null));
//        wallVert = new Texture(Gdx.files.internal(null));
//        wallHori = new Texture(Gdx.files.internal(null));

        playerA = new Texture(Gdx.files.internal("data/player.png"));
        playerB = new Texture(Gdx.files.internal("data/player2.png"));
        ball = new Texture(Gdx.files.internal("data/ball2.png"));
        bg = new Texture(Gdx.files.internal("data/transparent.png"));
        powerUp = new Texture(Gdx.files.internal("data/powerUp.png"));

        bgRegion = new TextureRegion(bg);
        bgRegion.flip(false, true);
        playerRegionA = new TextureRegion(playerA);
        playerRegionB = new TextureRegion(playerB);

        // Buttons
        boostButton = new Texture(Gdx.files.internal("data/boostButton.png"));
        boostRegion = new TextureRegion(boostButton);
        tossButton = new Texture(Gdx.files.internal("data/tossButton.png"));
        tossRegion = new TextureRegion(tossButton);

        powerSlot1 = new Texture(Gdx.files.internal("data/power_slot.png"));
        powerSlotRegion1 = new TextureRegion(powerSlot1);
        powerUp1 = new Texture(Gdx.files.internal("data/power1.png"));
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
