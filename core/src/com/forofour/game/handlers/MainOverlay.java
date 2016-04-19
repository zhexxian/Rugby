package com.forofour.game.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.forofour.game.actors.ButtonMaker;
import com.forofour.game.actors.GameOverMaker;
import com.forofour.game.actors.PowerUpSlotMaker;
import com.forofour.game.actors.ScoreIndicatonActorMaker;
import com.forofour.game.actors.TextLabelMaker;
import com.forofour.game.actors.Timer;
import com.forofour.game.actors.TouchPadMaker;
import com.forofour.game.actors.TutorialMaker;
import com.forofour.game.gameobjects.Ball;
import com.forofour.game.gameobjects.Player;
import com.forofour.game.gameobjects.Team;
import com.forofour.game.net.GameClient;
import com.forofour.game.net.Network;
import com.forofour.game.tutorialMode.TutorialStates;

import java.util.ArrayList;

/**
 * A subclass of Libgdx's scene2d Stage which in itself has a render
 * Sole overlay that exist only when the GAME is in play(inc. end game state)
 * All actors are included within the stage upon intialization. However only
 *  relevant actors(Buttons, TimeLabels, Image, EndGameOverlay) are shown at
 *  any time.
 */
public class MainOverlay extends Stage {

    private boolean isHost;
    private GameClient client;

    // HUD Components
    private Touchpad touchpad;
    private ImageButton boostButton, tossButton, buttonSlot, powerSlot;
    private Label globalLabel, teamLabel;
    private Image scoreLine, scoreA, scoreB, infinity;
    private Container boostContainer;

    // GameEnd Components
    private static Texture youLoseRed,youLoseBlue, youWinRed,youWinBlue;
    private static Image youLoseRedImage,youLoseBlueImage,youWinRedImage,youWinBlueImage;
    private Texture buttonPlayAgainTexture, buttonMainMenuTexture;
    private Image buttonPlayAgain, buttonMainMenu;

    // In-Game Components
    private boolean isInitialized;
    private Player player;
    private Ball ball;
    private Team teamA, teamB;
    private Timer globalTime;

    // Tutorial States - only in tutorial mode is it instantiated
    private TutorialStates tutorialStates;
    private static Texture welcomeTexture, boostTexture, holdbottleTexture, tossTexture, powerupTexture, winconditionTexture, endtutorialTexture;
    private static Image welcomeImage, boostImage, holdBottleImage, tossImage, powerUpImage, winConditionImage, endTutorialImage;
    private Texture buttonNextTexture;
    private Image buttonNextImage;
    private int tutorialCount = 0;


    public MainOverlay(final boolean isHost, final GameClient client){
        this(isHost, client, null);
    }

    public MainOverlay(final boolean isHost, final GameClient client, TutorialStates tutorialStates) {
        super();

        this.isHost = isHost;
        this.client = client;
        this.tutorialStates = tutorialStates;
        if(this.tutorialStates != null)
            Gdx.app.log("MainOverlay", "tutorialMode");

        //make & add actors(HUD components) to the stage
        touchpad = TouchPadMaker.make(client);
        boostButton = ButtonMaker.getBoostButton(client, tutorialStates);
        tossButton = ButtonMaker.getTossButton(client);
        globalLabel = TextLabelMaker.getTimeLabel(client);
        infinity = TextLabelMaker.getInfinityImage();
        teamLabel = TextLabelMaker.getTimeLabel(client);
        buttonSlot = PowerUpSlotMaker.getButtonSlot();
        powerSlot = PowerUpSlotMaker.getPowerSlot(client);
        boostContainer = ButtonMaker.wrap1(boostButton);

        scoreLine = ScoreIndicatonActorMaker.getScoreLine();
        scoreA = ScoreIndicatonActorMaker.getIndicatorA();
        scoreB = ScoreIndicatonActorMaker.getIndicatorB();

        addActor(TouchPadMaker.wrap(touchpad));
        addActor(PowerUpSlotMaker.wrap2(buttonSlot));
        addActor(boostContainer);
        addActor(ButtonMaker.wrap2(tossButton));
        addActor(infinity);
        addActor(TextLabelMaker.wrapGlobalTime(globalLabel));
        addActor(TextLabelMaker.wrapTeamScore(teamLabel));
        addActor(PowerUpSlotMaker.wrap1(powerSlot));

        addActor(scoreLine);
        addActor(scoreA);
        addActor(scoreB);

        // make & add End Game components to the stage
        youLoseRed = new Texture("sprites/Design 2/Game Screen/end game/red-lose.png");
        youLoseBlue = new Texture("sprites/Design 2/Game Screen/end game/blue-lose.png");
        youWinRed = new Texture("sprites/Design 2/Game Screen/end game/red-win.png");
        youWinBlue = new Texture("sprites/Design 2/Game Screen/end game/blue-win.png");
        youLoseRedImage = new Image(youLoseRed);
        youLoseBlueImage = new Image(youLoseBlue);
        youWinRedImage = new Image(youWinRed);
        youWinBlueImage = new Image(youWinBlue);

        buttonPlayAgainTexture = new Texture("sprites/Design 2/Game Screen/end game/rematch-button.png");
        buttonMainMenuTexture = new Texture("sprites/Design 2/Game Screen/end game/menu-button.png");
        buttonPlayAgain = new Image(buttonPlayAgainTexture);
        buttonMainMenu = new Image(buttonMainMenuTexture);

        // TODO: debug the listening function
        buttonPlayAgain.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
            {
                if(isHost) {
                    client.sendMessage(new Network.PacketGameEnd(true));
                    Gdx.app.log("MainOverlay", "PlayAgain(TRUE) Button Sent");
                }
                else {
                    client.playAgain = true;
                }
                return true;
            }
        });

        buttonMainMenu.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
            {
                if (isHost) {
                    client.sendMessage(new Network.PacketGameEnd(false));
                    // No change of states here
                    // Allow server to register change and announce to server.
                    Gdx.app.log("MainOverlay", "PlayEnd Button Sent");
                } else {
//                    client.sendMessage(new Network.PacketGameEnd(false));
                    client.playAgain = false;
                    client.playEnd = true;
                }
                return true;
            }
        });

        addActor(GameOverMaker.wrapBlackLayer(youLoseRedImage));
        addActor(GameOverMaker.wrapBlackLayer(youLoseBlueImage));
        addActor(GameOverMaker.wrapBlackLayer(youWinRedImage));
        addActor(GameOverMaker.wrapBlackLayer(youWinBlueImage));
        addActor(GameOverMaker.wrapRematchButton(buttonPlayAgain));
        addActor(GameOverMaker.wrapMenuButton(buttonMainMenu));

        // At start of game, endGame Overlays are not shown
        hideEndgameOverlay();
        isInitialized = false; // Triggered when server has assigned all required objects

        // make and add tutorial components to the stage
        welcomeTexture = new Texture("sprites/Design 2/Game Screen/tutorial overlays/welcome.png");
        boostTexture = new Texture("sprites/Design 2/Game Screen/tutorial overlays/boost.png");
        holdbottleTexture = new Texture("sprites/Design 2/Game Screen/tutorial overlays/holdbottle.png");
        tossTexture = new Texture("sprites/Design 2/Game Screen/tutorial overlays/toss.png");
        powerupTexture = new Texture("sprites/Design 2/Game Screen/tutorial overlays/powerup.png");
        winconditionTexture = new Texture("sprites/Design 2/Game Screen/tutorial overlays/wincondition.png");
        endtutorialTexture = new Texture("sprites/Design 2/Game Screen/tutorial overlays/endtutorial.png");



        welcomeImage = new Image(welcomeTexture);
        boostImage = new Image(boostTexture);
        holdBottleImage = new Image(holdbottleTexture);
        tossImage = new Image(tossTexture);
        powerUpImage = new Image(powerupTexture);
        winConditionImage = new Image(winconditionTexture);
        endTutorialImage = new Image(endtutorialTexture);

        buttonNextTexture = new Texture("sprites/Design 2/Game Screen/tutorial overlays/nextbutton.png");
        buttonNextImage = new Image(buttonNextTexture);

        buttonNextImage.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
                tutorialCount += 1;
                client.sendMessage(new Network.PacketGamePause());
                return true;
            }
        });

        addActor(TutorialMaker.wrapBlackLayer(welcomeImage));
        addActor(TutorialMaker.wrapBlackLayer(boostImage));
        addActor(TutorialMaker.wrapBlackLayer(holdBottleImage));
        addActor(TutorialMaker.wrapBlackLayer(tossImage));
        addActor(TutorialMaker.wrapBlackLayer(powerUpImage));
        addActor(TutorialMaker.wrapBlackLayer(winConditionImage));
        addActor(TutorialMaker.wrapBlackLayer(endTutorialImage));
        addActor(TutorialMaker.wrapNextButton(buttonNextImage));

        // TODO: Overlay triggering in different conditions

        // hide tutorial overlays
        hideTutorialOverlay();
    }

    public GameClient getClient(){
        return client;
    }

    public void update(float delta){
        if (!client.getMap().gamePaused) {
            showActors();
            hideEndgameOverlay();
            if (isInitialized) {
                captureTouchpad();
                updateTime();
                updateScore(globalTime.getElapsedMilliseconds() / 1000);
                updateButtons();

                if(tutorialStates != null) {
                    hideActors();
                    tutorialStates.updateTutorialStates();
                    tutorialStates.printStates();

                    if (tutorialCount == 0) {
                        showTutorialOverlay(true, false, false, false, false, false, false);
                    }
                    if (tutorialCount == 1) {
                        hideTutorialOverlay();
                        showTutorialOverlay(false, true, false, false, false, false, false);
                    }
                    if (tutorialCount == 2 && !tutorialStates.boostedPlayer()){
                        hideTutorialOverlay();
                        showActors();
                        updateButtons();
                        tutorialStates.setShowBall(false);
                    }
                    if (tutorialCount == 2 && tutorialStates.boostedPlayer()){
                        showTutorialOverlay(false,false,true,false,false,false,false);
                    }
                    if (tutorialCount == 3){
                        hideTutorialOverlay();
                        showTutorialOverlay(false,false,false,true,false,false,false);
                    }
                    if (tutorialCount == 4 && !tutorialStates.tossedBall()) {
                        hideTutorialOverlay();
                        showActors();
                        updateButtons();
                        tutorialStates.setShowBall(true);
                    }
                    if (tutorialCount == 4 && tutorialStates.tossedBall()) {
                        showTutorialOverlay(false,false,false,false,true,false,false);
                    }
                    if (tutorialCount == 5 && !tutorialStates.usedPowerUp()) {
                        hideTutorialOverlay();
                        showActors();
                        updateButtons();
                    }
                    if (tutorialCount == 5 && tutorialStates.usedPowerUp()){
                        showTutorialOverlay(false,false,false,false,false,true,false);
                    }
                    if (tutorialCount == 6) {
                        hideTutorialOverlay();
                        showTutorialOverlay(false,false,false,false,false,false,true);
                    }
                    if (tutorialCount == 7) {
                        hideTutorialOverlay();
                        showActors();
                        updateButtons();
                    }

                    // TODO: Include Tutorial Actors Show/Hide logic

                    // TODO: TutorialStates contain the triggers e.g. PlayerMoved, BallPicked, BallTossed
                }

            } else {
                initialized();
            }
        } else {
            hideActors();
            hideEndgameOverlay();
            if (client.getMap().gameEnd) {
                showEndgameOverlay(isHost, client.serverReady);
            }
        }

        getCamera().update();
        act(delta);
        draw();
    }

    // Not shown during game play
    private void hideEndgameOverlay() {
        buttonPlayAgain.setVisible(false);
        buttonMainMenu.setVisible(false);
        youLoseRedImage.setVisible(false);
        youLoseBlueImage.setVisible(false);
        youWinRedImage.setVisible(false);
        youWinBlueImage.setVisible(false);
    }

    // Only shown during gameEnd.
    private void showEndgameOverlay(boolean isHost, boolean hostReady) {
//        Gdx.app.log("P"+player.getId(), "Teamid"+ player.getTeamId()
//                + " TeamAid" + teamA.getId() + " TeamAScore" + teamA.getScore()
//                + " TeamBid" + teamB.getId() + " TeamBScore" + teamB.getScore());
        if(isHost) {
            // Host will see both choices upon game end
            buttonPlayAgain.setVisible(true);
            buttonMainMenu.setVisible(true);
//            Gdx.app.log("MainOverlay-showEndgameOverlay-host", "Show playRestart and mainMenu button");
        }
        else {
            // Client will see only MainMenu button
            // Only when host is Ready, shall client have the option to PlayAgain
            if(hostReady){
                buttonPlayAgain.setVisible(true);
            }
            buttonMainMenu.setVisible(true);
        }

        // EndGame Actors Show/Hide logic
        if(player.getTeamId()==1){
            if(teamA.getScore()>teamB.getScore()){
                youWinBlueImage.setVisible(true);
            }
            else{
                youLoseBlueImage.setVisible(true);
            }
        }
        else if(player.getTeamId()==2){
            if(teamB.getScore()>teamA.getScore()){
                youWinRedImage.setVisible(true);
            }
            else{
                youLoseRedImage.setVisible(true);
            }
        }
        else {
            youLoseBlueImage.setVisible(true);
        }
    }

    // not shown in normal gameplay
    private void hideTutorialOverlay(){
        welcomeImage.setVisible(false);
        boostImage.setVisible(false);
        powerUpImage.setVisible(false);
        holdBottleImage.setVisible(false);
        tossImage.setVisible(false);
        winConditionImage.setVisible(false);
        endTutorialImage.setVisible(false);
        buttonNextImage.setVisible(false);
    }

    private void showTutorialOverlay(boolean isWelcome, boolean isBoost, boolean isHoldBottle, boolean isToss, boolean isPowerUp, boolean isWinCondition, boolean isEndTutorial){

        client.sendMessage(new Network.PacketGamePause());
        buttonNextImage.setVisible(true);

        if (isWelcome){
            welcomeImage.setVisible(true);
        }
        if (isBoost) {
            boostImage.setVisible(true);
        }
        if (isHoldBottle){
            holdBottleImage.setVisible(true);
        }
        if (isToss){
            tossImage.setVisible(true);
        }
        if (isPowerUp){
            powerUpImage.setVisible(true);
        }
        if (isWinCondition){
            winConditionImage.setVisible(true);
        }
        if (isEndTutorial) {
            endTutorialImage.setVisible(true);
        }


    }

    // Safety check to ensure all objects are assigned before Overlay is shown
    private void initialized() {
        player = client.getMap().getPlayer();
        teamA = client.getMap().getTeamA();
        teamB = client.getMap().getTeamB();
        ball = client.getMap().getBall();
        globalTime = client.getMap().getGlobalTime();
        if (player != null && teamA != null && teamB != null && ball != null && globalTime != null ) {
            isInitialized = true;
        }
    }

    public void hideActors(){
        touchpad.setVisible(false);
        buttonSlot.setVisible(false);
        boostButton.setVisible(false);
        tossButton.setVisible(false);
        powerSlot.setVisible(false);
        infinity.setVisible(false);
        globalLabel.setVisible(false);
        teamLabel.setVisible(false);

        scoreLine.setVisible(false);
        scoreA.setVisible(false);
        scoreB.setVisible(false);
    }
    public void showActors() {
        touchpad.setVisible(true);
        buttonSlot.setVisible(true);
        powerSlot.setVisible(true);
//        globalLabel.setVisible(true);
        if(globalTime != null) {
            if(globalTime.getInfinityMode()) {
                globalLabel.setVisible(false);
                infinity.setVisible(true);
            }
            else {
                globalLabel.setVisible(true);
                infinity.setVisible(false);
            }
        }

        teamLabel.setVisible(false);
        scoreLine.setVisible(true);
        scoreA.setVisible(true);
        scoreB.setVisible(true);
    }

    // Touchpad is an actor in stage
    // Requires capture to relay the inputs into the Player's movement
    private void captureTouchpad() {
        if (player.getConfusionEffectTime() > 0) {
            player.knobMove(-touchpad.getKnobPercentX(), touchpad.getKnobPercentY());
        }
        if (player.getSlowEffectTime() > 0) {
            player.knobMove(touchpad.getKnobPercentX()/4, -touchpad.getKnobPercentY()/4);
        }
        else {
            player.knobMove(touchpad.getKnobPercentX(), -touchpad.getKnobPercentY());
        }
    }

    private void updateTime() {
        // Global time display
        globalLabel.setText(globalTime.getElapsed());
    }

    private void updateScore(float delta) {
        float aScore = teamA.getScore();
        float bScore = teamB.getScore();

//        // Team time/score display
//        if(teamA.getTeamList().contains(player))
//            teamLabel.setText("A Score: " + (int) teamA.getScore());
//        else if(teamB.getTeamList().contains(player))
//            teamLabel.setText("B Score: " + (int) teamB.getScore());
//        else
//            teamLabel.setText("No team score");

        if(aScore > bScore)
            scoreB.toBack();
        else if(bScore > aScore)
            scoreA.toBack();
        else if(player.getTeamId() == 1)
            scoreB.toBack();
        else
            scoreA.toBack();

        ScoreIndicatonActorMaker.updateIndicatorPosition(scoreA, aScore/client.getMap().maxScore);
        ScoreIndicatonActorMaker.updateIndicatorPosition(scoreB, bScore/client.getMap().maxScore);
    }

    // Check the states and show client control buttons(Toss/Boost/PowerUp)
    private void updateButtons() {
        if(player != null) {
            // Logic to show/hide toss/boost button
            if(player.hasBall()) {
                tossButton.setVisible(true);
                boostButton.setVisible(false);
            } else if(player.isBoostCooldown()) {
                tossButton.setVisible(false);
                boostButton.setVisible(true);
                Gdx.app.log("Percentage ", "" + player.boostCooldownPercentage());
                ButtonMaker.relativeScale(boostContainer, player.boostCooldownPercentage());
            } else {
                tossButton.setVisible(false);
                boostButton.setVisible(true);
                ButtonMaker.relativeScale(boostContainer, 1);
            }
        }
        else {
            tossButton.setVisible(false);
            boostButton.setVisible(false);
        }

        // PowerUp Slot
        if(player.hasPowerUp())
            PowerUpSlotMaker.setPowerUpStyle(player.getPowerUpType(), player.getTeamId());
        else
            PowerUpSlotMaker.setEmptySlotStyle();
    }

}
