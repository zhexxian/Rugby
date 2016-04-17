/*This is where the image files are defined and processed*/

package com.forofour.game.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Master container of the game Assets:
 * Images and Sounds of the various Screens(Splash, Menu, Lobby, Game)
 */
public class AssetLoader {

    // Pre-game Textures
    public static Texture splash, lobbyBg;
    public static Texture menu;
    public static Texture tutorialUp, tutorialDown, hostGameUp, joinGameUp, hostGameDown, joinGameDown;
    public static Texture volumeOn, volumeOff;

    // In-game Textures
    public static Texture bg, bg2, bgTrain, infinity;
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
    public static Texture boostButtonUp, tossButtonUp, boostButtonDown, tossButtonDown;
    public static Texture powerSlot1, powerUp1, powerUp2, powerUp3, powerUp4;
    public static Texture slowEffectTexture1, confusionEffectTexture1;


    // Pre-game TextureRegion
    public static TextureRegion splashScreen;
    public static TextureRegion bgMenu;
    public static TextureRegion tutorialRegionUp, tutorialRegionDown;
    public static TextureRegion hostGameRegionUp, joinGameRegionUp;
    public static TextureRegion hostGameRegionDown, joinGameRegionDown;
    public static TextureRegion volumeRegionOn, volumeRegionOff;

    // In-game TextureRegion
    public static TextureRegion bgRegion, bgRegion2, bgTrainRegion;
    public static TextureRegion ballRegion;
    public static TextureRegion playerRegionAup1, playerRegionBup1;
    public static TextureRegion playerRegionAdown1, playerRegionBdown1;
    public static TextureRegion playerRegionAleft1, playerRegionBleft1;
    public static TextureRegion playerRegionAright1, playerRegionBright1;
    public static TextureRegion playerRegionAup2, playerRegionBup2;
    public static TextureRegion playerRegionAdown2, playerRegionBdown2;
    public static TextureRegion playerRegionAleft2, playerRegionBleft2;
    public static TextureRegion playerRegionAright2, playerRegionBright2;

    public static TextureRegion boostRegionUp, tossRegionUp, boostRegionDown, tossRegionDown;
    public static TextureRegion powerUpRegion, powerSlotRegion1, powerUpRegion1, powerUpRegion2, powerUpRegion3, powerUpRegion4;
    public static TextureRegion slowEffectRegion1, slowEffectRegion2;
    public static TextureRegion confusionEffectRegion1, confusionEffectRegion2;

    // In-game Animations
    public static Animation[] TeamAnimationA;
    public static Animation[] TeamAnimationB;
    public static Animation playerAnimationDownA, playerAnimationRightA, playerAnimationUpA, playerAnimationLeftA;
    public static Animation playerAnimationDownB, playerAnimationRightB, playerAnimationUpB, playerAnimationLeftB;

    public static Animation confusionAnimation;
    public static Animation slowAnimation;

    // Overlay Textures
    public static Texture scoreLine;
    public static Texture scoreIndicatorA, scoreIndicatorB;

    // Sounds/Music
    public static Music mainMusic;
    public static Sound fartBoostSound;
    public static Music ingameMusic;
    public static Sound powerUpSound;

    public static Sound tossSound;

    public static Sound menuButtonSound;

    public static Sound slowSound;
    public static Sound confuseSound;
    public static Sound invisibleSound;
    public static Sound nudgeSound;

    // Function that loads all the assets
    public static void load() {
        //splash screen
        splash = new Texture("Team 404.png");
        splashScreen = new TextureRegion(splash);

        //menu background
        menu = new Texture(Gdx.files.internal("sprites/Design 2/Starting screen/start-screen.png"));
        bgMenu = new TextureRegion(menu);

        tutorialUp = new Texture(Gdx.files.internal("sprites/Design 2/Starting screen/tutorial-button.png"));
        tutorialDown = new Texture(Gdx.files.internal("sprites/Design 2/Starting screen/tutorial-button-pressed.png"));
        hostGameUp = new Texture(Gdx.files.internal("sprites/Design 2/Starting screen/host-game-button.png"));
        hostGameDown = new Texture(Gdx.files.internal("sprites/Design 2/Starting screen/host-game-button-pressed.png"));
        joinGameUp= new Texture(Gdx.files.internal("sprites/Design 2/Starting screen/join-game-button.png"));
        joinGameDown = new Texture(Gdx.files.internal("sprites/Design 2/Starting screen/join-game-button-pressed.png"));

        tutorialRegionUp = new TextureRegion(tutorialUp);
        tutorialRegionDown = new TextureRegion(tutorialDown);
        hostGameRegionUp = new TextureRegion(hostGameUp);
        hostGameRegionDown = new TextureRegion(hostGameDown);
        joinGameRegionUp = new TextureRegion(joinGameUp);
        joinGameRegionDown = new TextureRegion(joinGameDown);

        volumeOn= new Texture(Gdx.files.internal("sprites/Design 2/Starting screen/Sound-on.png"));
        volumeOff = new Texture(Gdx.files.internal("sprites/Design 2/Starting screen/Sound-off.png"));
        volumeRegionOn = new TextureRegion(volumeOn);
        volumeRegionOff = new TextureRegion(volumeOff);

        //lobby background
        lobbyBg = new Texture(Gdx.files.internal("sprites/Design 2/Waiting screen/waiting-screen-2.png"));

        //game background
        bg = new Texture(Gdx.files.internal("sprites/background test/random-nursery-background-2.png"));
        bgRegion = new TextureRegion(bg);
        bg2 = new Texture(Gdx.files.internal("sprites/Design 2/Game Screen/background-(1200x640).png"));
        bgRegion2 = new TextureRegion(bg2);

        bgTrain = new Texture(Gdx.files.internal("sprites/Design 2/Game Screen/train-border-(1200x640).png"));
        bgTrainRegion = new TextureRegion(bgTrain);
        bgTrainRegion.flip(false, true);

        infinity = new Texture(Gdx.files.internal("sprites/Design 2/Game Screen/infinity.png"));

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


        slowEffectTexture1 = new Texture(Gdx.files.internal(path+"effect_water-puddle.png"));
        slowEffectRegion1 = new TextureRegion(slowEffectTexture1);
        slowEffectRegion2 = new TextureRegion(slowEffectTexture1);
        slowEffectRegion2.flip(true, false);
        confusionEffectTexture1 = new Texture(Gdx.files.internal(path+"effect_confused.png"));
        confusionEffectRegion1 = new TextureRegion(confusionEffectTexture1);
        confusionEffectRegion2 = new TextureRegion(confusionEffectTexture1);
        confusionEffectRegion2.flip(true, false);

        TextureRegion[] slowEffectRegions = {slowEffectRegion1, slowEffectRegion2};
        TextureRegion[] confusionEffectRegions = {confusionEffectRegion1, confusionEffectRegion2};

        slowAnimation = new Animation(0.25f, slowEffectRegions);
        confusionAnimation = new Animation(0.25f, confusionEffectRegions);

        slowAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
        confusionAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);

        //buttons
        boostButtonUp = new Texture(Gdx.files.internal("sprites/Design 2/Game Screen/boost-button2.png"));
        boostRegionUp = new TextureRegion(boostButtonUp);
        boostButtonDown = new Texture(Gdx.files.internal("sprites/buttons/Boost-activate.png"));
        boostRegionDown = new TextureRegion(boostButtonDown);
        tossButtonUp = new Texture(Gdx.files.internal("sprites/Design 2/Game Screen/toss-button-orange2.png"));
        tossRegionUp = new TextureRegion(tossButtonUp);
        tossButtonDown = new Texture(Gdx.files.internal("sprites/buttons/Toss-activate.png"));
        tossRegionDown = new TextureRegion(tossButtonDown);

        //overlays
        scoreLine = new Texture(Gdx.files.internal("sprites/Design 2/Game Screen/timeline/Line.png"));
        scoreIndicatorA = new Texture(Gdx.files.internal("sprites/Design 2/Game Screen/timeline/blue-mark.png"));
        scoreIndicatorB = new Texture(Gdx.files.internal("sprites/Design 2/Game Screen/timeline/red-mark.png"));

        //sound
        mainMusic = Gdx.audio.newMusic(Gdx.files.internal("sound/mainmenu.wav"));
        fartBoostSound = Gdx.audio.newSound(Gdx.files.internal("sound/fart1.wav"));
        ingameMusic = Gdx.audio.newMusic(Gdx.files.internal("sound/ingame.wav"));
        powerUpSound = Gdx.audio.newSound(Gdx.files.internal("sound/powerup.wav"));
        nudgeSound = fartBoostSound; // TODO: Replace with another sound
        menuButtonSound = Gdx.audio.newSound(Gdx.files.internal("sound/button.mp3"));
        tossSound = Gdx.audio.newSound(Gdx.files.internal("sound/toss.wav"));
        // TODO: Include music for lobbyScreen
        // TODO: Include sounds for the powerUp effects
        confuseSound = Gdx.audio.newSound(Gdx.files.internal("sound/confuse.wav"));
        slowSound = Gdx.audio.newSound(Gdx.files.internal("sound/slow.wav"));
        invisibleSound = Gdx.audio.newSound(Gdx.files.internal("sound/invisible.mp3"));
        // TODO: Include music for endGame screen(Winner/Loser)

    }

    public static void dispose() {
        // We must dispose of the texture when we are finished.
        bg.dispose();
        bgTrain.dispose();
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
        slowEffectTexture1.dispose();
        confusionEffectTexture1.dispose();

        boostButtonUp.dispose();
        tossButtonUp.dispose();
        boostButtonDown.dispose();
        tossButtonDown.dispose();

        powerSlot1.dispose();
        powerUp1.dispose();
        powerUp2.dispose();
        powerUp3.dispose();
        powerUp4.dispose();
    }
}
