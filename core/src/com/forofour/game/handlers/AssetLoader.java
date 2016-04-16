/*This is where the image files are defined and processed*/

package com.forofour.game.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by seanlim on 27/2/2016.
 */
public class AssetLoader {

    public static Texture splash;

    public static Texture bg, menu;

    public static Texture ball;

    public static Texture playerAup1, playerBup1;
    public static Texture playerAdown1, playerBdown1;
    public static Texture playerAleft1, playerBleft1;
    public static Texture playerAright1, playerBright1;
    public static Texture playerAup2, playerBup2;
    public static Texture playerAdown2, playerBdown2;
    public static Texture playerAleft2, playerBleft2;
    public static Texture playerAright2, playerBright2;

    public static Texture powerUp;

    public static Texture boostButton, tossButton;
    public static Texture powerSlot1, powerUp1, powerUp2, powerUp3, powerUp4;
    public static Texture powerUp1Effect, powerUp2Effect;

    public static TextureRegion splashScreen;
    public static TextureRegion bgMenu;
    public static TextureRegion bgRegion;

    public static TextureRegion ballRegion;
    public static TextureRegion playerRegionAup1, playerRegionBup1;
    public static TextureRegion playerRegionAdown1, playerRegionBdown1;
    public static TextureRegion playerRegionAleft1, playerRegionBleft1;
    public static TextureRegion playerRegionAright1, playerRegionBright1;
    public static TextureRegion playerRegionAup2, playerRegionBup2;
    public static TextureRegion playerRegionAdown2, playerRegionBdown2;
    public static TextureRegion playerRegionAleft2, playerRegionBleft2;
    public static TextureRegion playerRegionAright2, playerRegionBright2;

    public static Animation[] TeamAnimationA;
    public static Animation[] TeamAnimationB;

    public static Animation playerAnimationDownA, playerAnimationRightA, playerAnimationUpA, playerAnimationLeftA;
    public static Animation playerAnimationDownB, playerAnimationRightB, playerAnimationUpB, playerAnimationLeftB;

    public static TextureRegion boostRegion, tossRegion;
    public static TextureRegion powerUpRegion, powerSlotRegion1, powerUpRegion1, powerUpRegion2, powerUpRegion3, powerUpRegion4;
    public static TextureRegion powerUpEffectRegion1, powerUpEffectRegion2;

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
        ballRegion = new TextureRegion(ball);

        //player A (blue baby) four directions
        playerAup1 = new Texture(Gdx.files.internal("sprites/individual babies/blue-back-1.png"));
        playerAdown1 = new Texture(Gdx.files.internal("sprites/individual babies/blue-straight-1.png"));
        playerAleft1 = new Texture(Gdx.files.internal("sprites/individual babies/blue-left-1.png"));
        playerAright1 = new Texture(Gdx.files.internal("sprites/individual babies/blue-right-1.png"));
        playerAup2 = new Texture(Gdx.files.internal("sprites/individual babies/blue-back-2.png"));
        playerAdown2 = new Texture(Gdx.files.internal("sprites/individual babies/blue-straight-2.png"));
        playerAleft2 = new Texture(Gdx.files.internal("sprites/individual babies/blue-left-2.png"));
        playerAright2 = new Texture(Gdx.files.internal("sprites/individual babies/blue-right-2.png"));

        playerRegionAup1 = new TextureRegion(playerAup1);
        playerRegionAdown1 = new TextureRegion(playerAdown1);
        playerRegionAleft1 = new TextureRegion(playerAleft1);
        playerRegionAright1= new TextureRegion(playerAright1);
        playerRegionAup2 = new TextureRegion(playerAup2);
        playerRegionAdown2 = new TextureRegion(playerAdown2);
        playerRegionAleft2 = new TextureRegion(playerAleft2);
        playerRegionAright2= new TextureRegion(playerAright2);

        playerRegionAup1.flip(false, true);
        playerRegionAdown1.flip(false, true);
        playerRegionAleft1.flip(false, true);
        playerRegionAright1.flip(false, true);
        playerRegionAup2.flip(false, true);
        playerRegionAdown2.flip(false, true);
        playerRegionAleft2.flip(false, true);
        playerRegionAright2.flip(false, true);

        TextureRegion[] playerAup = {playerRegionAup1, playerRegionAup2};
        TextureRegion[] playerAdown = {playerRegionAdown1, playerRegionAdown2};
        TextureRegion[] playerAleft = {playerRegionAleft1, playerRegionAleft2};
        TextureRegion[] playerAright = {playerRegionAright1, playerRegionAright2};

        playerAnimationUpA = new Animation(0.1f, playerAup);
        playerAnimationDownA = new Animation(0.1f, playerAdown);
        playerAnimationLeftA = new Animation(0.1f, playerAleft);
        playerAnimationRightA = new Animation(0.1f, playerAright);
        TeamAnimationA = new Animation[]{playerAnimationUpA, playerAnimationDownA, playerAnimationLeftA, playerAnimationRightA};

        playerAnimationUpA.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
        playerAnimationDownA.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
        playerAnimationLeftA.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
        playerAnimationRightA.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);

        //player B (red baby) four directions
        playerBup1 = new Texture(Gdx.files.internal("sprites/individual babies/red-back-1.png"));
        playerBdown1 = new Texture(Gdx.files.internal("sprites/individual babies/red-straight-1.png"));
        playerBleft1 = new Texture(Gdx.files.internal("sprites/individual babies/red-left-1.png"));
        playerBright1 = new Texture(Gdx.files.internal("sprites/individual babies/red-right-1.png"));
        playerBup2 = new Texture(Gdx.files.internal("sprites/individual babies/red-back-2.png"));
        playerBdown2 = new Texture(Gdx.files.internal("sprites/individual babies/red-straight-2.png"));
        playerBleft2 = new Texture(Gdx.files.internal("sprites/individual babies/red-left-2.png"));
        playerBright2 = new Texture(Gdx.files.internal("sprites/individual babies/red-right-2.png"));

        playerRegionBup1 = new TextureRegion(playerBup1);
        playerRegionBdown1 = new TextureRegion(playerBdown1);
        playerRegionBleft1 = new TextureRegion(playerBleft1);
        playerRegionBright1= new TextureRegion(playerBright1);
        playerRegionBup2 = new TextureRegion(playerBup2);
        playerRegionBdown2 = new TextureRegion(playerBdown2);
        playerRegionBleft2 = new TextureRegion(playerBleft2);
        playerRegionBright2= new TextureRegion(playerBright2);

        playerRegionBup1.flip(false, true);
        playerRegionBdown1.flip(false, true);
        playerRegionBleft1.flip(false, true);
        playerRegionBright1.flip(false, true);
        playerRegionBup2.flip(false, true);
        playerRegionBdown2.flip(false, true);
        playerRegionBleft2.flip(false, true);
        playerRegionBright2.flip(false, true);

        TextureRegion[] playerBup = {playerRegionBup1, playerRegionBup2};
        TextureRegion[] playerBdown = {playerRegionBdown1, playerRegionBdown2};
        TextureRegion[] playerBleft = {playerRegionBleft1, playerRegionBleft2};
        TextureRegion[] playerBright = {playerRegionBright1, playerRegionBright2};

        playerAnimationUpB = new Animation(0.1f, playerBup);
        playerAnimationDownB = new Animation(0.1f, playerBdown);
        playerAnimationLeftB = new Animation(0.1f, playerBleft);
        playerAnimationRightB = new Animation(0.1f, playerBright);
        TeamAnimationB = new Animation[]{playerAnimationUpB, playerAnimationDownB, playerAnimationLeftB, playerAnimationRightB};

        playerAnimationUpB.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
        playerAnimationDownB.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
        playerAnimationLeftB.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
        playerAnimationRightB.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);

        //power ups
        // Slow - 1, Confusion - 2, Invisibility - 3
        String path = "sprites/Design 2/Game Screen/powerup/";
        powerUp = new Texture(Gdx.files.internal(path+"object_power-up-box.png"));
        powerUpRegion = new TextureRegion(powerUp);
        powerUpRegion.flip(false, true);

        powerSlot1 = new Texture(Gdx.files.internal(path+"power-up-slot.png"));
        powerSlotRegion1 = new TextureRegion(powerSlot1);

        powerUp1 = new Texture(Gdx.files.internal(path+"slow.png"));
        powerUpRegion1 = new TextureRegion(powerUp1);
        powerUp2 = new Texture(Gdx.files.internal(path+"confused.png"));
        powerUpRegion2 = new TextureRegion(powerUp2);
        powerUp3 = new Texture(Gdx.files.internal(path+"invisible-blue.png"));
        powerUpRegion3 = new TextureRegion(powerUp3);
        powerUp4 = new Texture(Gdx.files.internal(path+"invisible-red.png"));
        powerUpRegion4 = new TextureRegion(powerUp4);

        powerUp1Effect = new Texture(Gdx.files.internal(path+"effect_water-puddle.png"));
        powerUpEffectRegion1 = new TextureRegion(powerUp1Effect);
        powerUp2Effect = new Texture(Gdx.files.internal(path+"effect_confused.png"));
        powerUpEffectRegion2 = new TextureRegion(powerUp2Effect);

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

        playerAup1.dispose();
        playerAdown1.dispose();
        playerAleft1.dispose();
        playerAright1.dispose();
        playerBup1.dispose();
        playerBdown1.dispose();
        playerBleft1.dispose();
        playerBright1.dispose();

        playerAup2.dispose();
        playerAdown2.dispose();
        playerAleft2.dispose();
        playerAright2.dispose();
        playerBup2.dispose();
        playerBdown2.dispose();
        playerBleft2.dispose();
        playerBright2.dispose();

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
