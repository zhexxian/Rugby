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

    public static Texture boostButton, tossButton;

    public static TextureRegion bgRegion;
    public static TextureRegion playerRegionA, playerRegionB;

    public static TextureRegion boostRegion, tossRegion;

    public static void load() {
//
//        texture = new Texture(Gdx.files.internal(null));
//        wallVert = new Texture(Gdx.files.internal(null));
//        wallHori = new Texture(Gdx.files.internal(null));

        playerA = new Texture(Gdx.files.internal("data/player.png"));
        playerB = new Texture(Gdx.files.internal("data/player2.png"));
        ball = new Texture(Gdx.files.internal("data/ball2.png"));
        bg = new Texture(Gdx.files.internal("data/transparent.png"));

        bgRegion = new TextureRegion(bg);
        bgRegion.flip(false, true);
        playerRegionA = new TextureRegion(playerA);
        playerRegionB = new TextureRegion(playerB);

        // Buttons
        boostButton = new Texture(Gdx.files.internal("data/boostButton.png"));
        boostRegion = new TextureRegion(boostButton);
        tossButton = new Texture(Gdx.files.internal("data/tossButton.png"));
        tossRegion = new TextureRegion(tossButton);

    }

    public static void dispose() {
        // We must dispose of the texture when we are finished.
        playerA.dispose();
        playerB.dispose();
        ball.dispose();
        bg.dispose();

        boostButton.dispose();
        tossButton.dispose();
    }
}
