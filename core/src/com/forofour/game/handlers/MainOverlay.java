package com.forofour.game.handlers;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.forofour.game.actors.ButtonMaker;
import com.forofour.game.actors.TextLabelMaker;
import com.forofour.game.actors.Timer;
import com.forofour.game.actors.TouchPadMaker;
import com.forofour.game.gameobjects.Ball;
import com.forofour.game.gameobjects.Player;
import com.forofour.game.gameobjects.Team;
import com.forofour.game.net.GameClient;

/**
 * Created by seanlim on 4/4/2016.
 */
public class MainOverlay extends Stage {

    private GameClient client;

    private Touchpad touchpad;
    private ImageButton boostButton, tossButton, powerSlot;
    private Label globalLabel, teamLabel;

    private boolean isInitialized;
    private Player player;
    private Ball ball;
    private Team teamA, teamB;
    private Timer globalTime;

    public MainOverlay(GameClient client){
        super();
        this.client = client;
        //make & add actors(HUD components) to the stage
        touchpad = TouchPadMaker.make(client);
        boostButton = ButtonMaker.getBoostButton(client);
        tossButton = ButtonMaker.getTossButton(client);
        globalLabel = TextLabelMaker.getTimeLabel(client);
        teamLabel = TextLabelMaker.getTimeLabel(client);
//        powerSlot = PowerUpSlotMaker.getPowerSlot(client);

        addActor(TouchPadMaker.wrap(touchpad));
        addActor(ButtonMaker.wrap1(boostButton));
        addActor(ButtonMaker.wrap2(tossButton));
        addActor(TextLabelMaker.wrapGlobalTime(globalLabel));
        addActor(TextLabelMaker.wrapTeamScore(teamLabel));
//        addActor(PowerUpSlotMaker.wrap1(powerSlot));

        isInitialized = false;
        hideActors();
    }

    public GameClient getClient(){
        return client;
    }

    public void update(float delta){
        if(isInitialized){
            captureTouchpad();
            updateTime();
            updateScore(delta);
            updateButtons();

            if(client.getMap().isPaused()) {
                hideActors();
            } else {
                showActors();
            }
        }
        else {
            initialized();
        }

        getCamera().update();
        act(delta);
        draw();
    }
    private void initialized() {
        player = client.getMap().getPlayer();
        teamA = client.getMap().getTeamA();
        teamB = client.getMap().getTeamB();
        ball = client.getMap().getBall();
        globalTime = client.getMap().getGlobalTime();
        if(player != null && teamA != null && teamB != null && ball != null && globalTime != null ) {
            isInitialized = true;
        }
    }

    private void hideActors(){
//        touchpad.setVisible(false);
        boostButton.setVisible(false);
        tossButton.setVisible(false);
        globalLabel.setVisible(false);
        teamLabel.setVisible(false);
    }
    private void showActors() {
        touchpad.setVisible(true);
        globalLabel.setVisible(true);
        teamLabel.setVisible(true);
    }

    private void captureTouchpad() {
        if (player.getReverseDirectionTime() > 0) {
            player.knobMove(-touchpad.getKnobPercentX(), touchpad.getKnobPercentY());
        }
        if (player.getMoveVerySlowlyTime() > 0) {
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
        //add scores
        if(teamA.getTeamList().contains(ball.getHoldingPlayer()))
            teamA.addScore(delta);
        if(teamB.getTeamList().contains(ball.getHoldingPlayer()))
            teamB.addScore(delta);

        // Team time/score display
        if(teamA.getTeamList().contains(player))
            teamLabel.setText("A Score: " + teamA.getScore());
        else if(teamB.getTeamList().contains(player))
            teamLabel.setText("B Score: " + teamB.getScore());
        else
            teamLabel.setText("No team score");
    }

    private void updateButtons() {
        if(player != null) {
            if(player.hasBall()) {
                tossButton.setVisible(true);
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
    }
}
