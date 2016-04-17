package com.forofour.game.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
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
import com.forofour.game.actors.TextLabelMaker;
import com.forofour.game.actors.Timer;
import com.forofour.game.actors.TouchPadMaker;
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
    private ImageButton boostButton, tossButton, powerSlot;
    private Label globalLabel, teamLabel;

    // GameEnd Components
    private static Texture youLose;
    private static Texture youWin;
    private static Image youLoseImage;
    private static Image youWinImage;
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
        teamLabel = TextLabelMaker.getTimeLabel(client);
        powerSlot = PowerUpSlotMaker.getPowerSlot(client);

        addActor(TouchPadMaker.wrap(touchpad));
        addActor(ButtonMaker.wrap1(boostButton));
        addActor(ButtonMaker.wrap2(tossButton));
        addActor(TextLabelMaker.wrapGlobalTime(globalLabel));
        addActor(TextLabelMaker.wrapTeamScore(teamLabel));
        addActor(PowerUpSlotMaker.wrap1(powerSlot));

        // make & add End Game components to the stage
        youLose = new Texture("sprites/Design 2/Game Screen/end game/blue-lose.png");
        youWin = new Texture("sprites/Design 2/Game Screen/end game/red-win.png");
        youLoseImage = new Image(youLose);
        youWinImage = new Image(youWin);

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

        addActor(GameOverMaker.wrapBlackLayer(youLoseImage));
        addActor(GameOverMaker.wrapBlackLayer(youWinImage));
        addActor(GameOverMaker.wrapRematchButton(buttonPlayAgain));
        addActor(GameOverMaker.wrapMenuButton(buttonMainMenu));

        // At start of game, endGame Overlays are not shown
        hideEndgameOverlay();
        isInitialized = false; // Triggered when server has assigned all required objects
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
                    tutorialStates.updateTutorialStates();
                    tutorialStates.printStates();

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
        youLoseImage.setVisible(false);
        youWinImage.setVisible(false);
    }

    // Only shown during gameEnd.
    private void showEndgameOverlay(boolean isHost, boolean hostReady) {
        Gdx.app.log("P"+player.getId(), "Teamid"+ player.getTeamId()
                + " TeamAid" + teamA.getId() + " TeamAScore" + teamA.getScore()
                + " TeamBid" + teamB.getId() + " TeamBScore" + teamB.getScore());
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
                youWinImage.setVisible(true);
            }
            else{
                youLoseImage.setVisible(true);
            }
        }
        else if(player.getTeamId()==2){
            if(teamB.getScore()>teamA.getScore()){
                youWinImage.setVisible(true);
            }
            else{
                youLoseImage.setVisible(true);
            }
        }
        else {
            youLoseImage.setVisible(true);
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
        boostButton.setVisible(false);
        tossButton.setVisible(false);
        powerSlot.setVisible(false);
        globalLabel.setVisible(false);
        teamLabel.setVisible(false);
    }
    public void showActors() {
        touchpad.setVisible(true);
        powerSlot.setVisible(true);
        globalLabel.setVisible(true);
        teamLabel.setVisible(true);
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

        // Team time/score display
        if(teamA.getTeamList().contains(player))
            teamLabel.setText("A Score: " + teamA.getScore());
        else if(teamB.getTeamList().contains(player))
            teamLabel.setText("B Score: " + teamB.getScore());
        else
            teamLabel.setText("No team score");
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
                boostButton.setVisible(false);
            } else {
                tossButton.setVisible(false);
                boostButton.setVisible(true);
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
