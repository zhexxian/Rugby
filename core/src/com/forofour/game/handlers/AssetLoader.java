/*This is where the image files are defined and processed*/

package com.forofour.game.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by seanlim on 27/2/2016.
 */
public class AssetLoader {

    public static Texture splash;

    public static Texture bg, menu;

    public static Texture ball;

    public static Texture playerAup, playerBup;
    public static Texture playerAdown, playerBdown;
    public static Texture playerAleft, playerBleft;
    public static Texture playerAright, playerBright;

    public static Texture powerUp;

    public static Texture boostButton, tossButton;
    public static Texture powerSlot1, powerUp1, powerUp2, powerUp3, powerUp4;


    public static TextureRegion splashScreen;
    public static TextureRegion bgMenu;
    public static TextureRegion bgRegion;

    public static TextureRegion playerRegionAup, playerRegionBup;
    public static TextureRegion playerRegionAdown, playerRegionBdown;
    public static TextureRegion playerRegionAleft, playerRegionBleft;
    public static TextureRegion playerRegionAright, playerRegionBright;

    public static TextureRegion boostRegion, tossRegion;
    public static TextureRegion powerSlotRegion1, powerUpRegion1, powerUpRegion2, powerUpRegion3, powerUpRegion4;

    public static void load() {
        //splash screen
        splash = new Texture("Team 404.png");
        splashScreen = new TextureRegion(splash);

        //menu background
        menu = new Texture(Gdx.files.internal("sprites/start-screen.png"));
        bgMenu = new TextureRegion(menu);

        //game background
        bg = new Texture(Gdx.files.internal("sprites/background test/random-nursery-background-2.png"));
        bgRegion = new TextureRegion(bg);

        //ball (milk bottle)
        ball = new Texture(Gdx.files.internal("sprites/bottle-black-outline-upside-down.png"));

        //player A (blue baby) four directions

        playerAup = new Texture(Gdx.files.internal("sprites/individual babies/blue-back-1.png"));
        playerAdown = new Texture(Gdx.files.internal("sprites/individual babies/blue-straight-1.png"));
        playerAleft = new Texture(Gdx.files.internal("sprites/individual babies/blue-left-1.png"));
        playerAright = new Texture(Gdx.files.internal("sprites/individual babies/blue-right-1.png"));

        playerRegionAup = new TextureRegion(playerAup);
        playerRegionAdown = new TextureRegion(playerAdown);
        playerRegionAleft = new TextureRegion(playerAleft);
        playerRegionAright= new TextureRegion(playerAright);

        playerRegionAup.flip(false, true);
        playerRegionAdown.flip(false, true);
        playerRegionAleft.flip(false, true);
        playerRegionAright.flip(false, true);

        //player B (red baby) four directions

        playerBup = new Texture(Gdx.files.internal("sprites/individual babies/red-back-1.png"));
        playerBdown = new Texture(Gdx.files.internal("sprites/individual babies/red-straight-1.png"));
        playerBleft = new Texture(Gdx.files.internal("sprites/individual babies/red-left-1.png"));
        playerBright = new Texture(Gdx.files.internal("sprites/individual babies/red-right-1.png"));

        playerRegionBup = new TextureRegion(playerBup);
        playerRegionBdown = new TextureRegion(playerBdown);
        playerRegionBleft = new TextureRegion(playerBleft);
        playerRegionBright= new TextureRegion(playerBright);

        playerRegionBup.flip(false, true);
        playerRegionBdown.flip(false, true);
        playerRegionBleft.flip(false, true);
        playerRegionBright.flip(false, true);

        //power ups
        powerUp = new Texture(Gdx.files.internal("data/powerUp.png"));

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

        //buttons
        boostButton = new Texture(Gdx.files.internal("sprites/buttons/Boost-activate.png"));
        boostRegion = new TextureRegion(boostButton);
        tossButton = new Texture(Gdx.files.internal("sprites/buttons/Toss-activate.png"));
        tossRegion = new TextureRegion(tossButton);

    }

    public static void dispose() {
        // We must dispose of the texture when we are finished.
        bg.dispose();
        menu.dispose();
        ball.dispose();

        playerAup.dispose();
        playerAdown.dispose();
        playerAleft.dispose();
        playerAright.dispose();

        playerBup.dispose();
        playerBdown.dispose();
        playerBleft.dispose();
        playerBright.dispose();

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
