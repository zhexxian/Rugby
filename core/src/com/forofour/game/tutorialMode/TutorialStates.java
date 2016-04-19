package com.forofour.game.tutorialMode;

import com.badlogic.gdx.Gdx;
import com.forofour.game.gameobjects.Player;
import com.forofour.game.handlers.GameMap;
import com.forofour.game.net.GameServer;

/**
 * Tutorial States
 *  Instantiated only in tutorial mode.
 * Keeps track of the states of the player progress in learning the game
 *  e.g. Ball picked, Ball thrown count, PowerUp used
 * MainOverlay makes use of the states here to show the relavent instructions
 */
public class TutorialStates {

    private GameMap map;
    private GameServer server;

    private static int duration = 90;

    private int boostCount = 0;
    private int tossCount = 0;
    private int pickCount = 0;

    public boolean wasBoosting = false;
    public boolean hadBall = false;

    private boolean movedPlayer = false;
    private boolean boostedPlayer = false;
    private boolean pickedBall = false;
    private boolean tossedBall = false;
    private boolean pickedPowerUp = false;
    private boolean usedPowerUp = false;

    private boolean showBall = true;

    private boolean spawnedPowerUp = false;
    private boolean quit = false;

    public TutorialStates(GameMap map, GameServer server) {
        this(map);
        this.server = server;
    }

    public TutorialStates(GameMap map){
        this.map = map;
        this.map.setGameDuration(duration);
    }

    public void spawnInvisibility() {
        server.assignPowerUp(3);
    }

    public void updateTutorialStates() {
        if(map != null) {
            if(map.getPlayer() != null) {
                Player player = map.getPlayer();

                // Update tutorialStates
                if(player.positionChanged())
                    movedPlayer = true;
                if(boostCount >= 3) { // boostCount, only state that triggered by Tapping of BoostButton
                    boostedPlayer = true;
                }
                if(player.hasBall()) {
                    pickedBall = true;
                    hadBall = true;
                }
                if(tossCount >= 3) {
                    tossedBall = true;
                }
                if(player.hasPowerUp())
                    pickedPowerUp = true;
                if(pickedPowerUp && !player.hasPowerUp())
                    usedPowerUp = true;

                // To increase count when ball has been tossed
                if(hadBall && !player.hasBall()) {
                    hadBall = false;
                    usedToss();
                }
            }
            if(movedPlayer && pickedBall && tossedBall && !spawnedPowerUp) {
                spawnInvisibility();
                spawnedPowerUp = true;
            }
        }
    }

    public void usedToss() {
        tossCount += 1;
    }
    public void usedBoost() {
        boostCount += 1;
    }

    public boolean usedPowerUp() {
        return usedPowerUp;
    }

    public boolean tossedBall() {
        return tossedBall;
    }

    public boolean pickedPowerUp() {
        return pickedPowerUp;
    }

    public boolean pickedBall() {
        return pickedBall;
    }

    public boolean boostedPlayer() {return boostedPlayer;}

    public boolean movedPlayer() {
        return movedPlayer;
    }

    public boolean getShowBall(){ return showBall;}

    public boolean setShowBall(boolean showBall){ this.showBall = showBall; return showBall;}

    public boolean isComplete() {
//        if(movedPlayer && boostedPlayer && pickedBall && tossedBall && pickedPowerUp && usedPowerUp) {
//            quit = true;
//            return true;
//        }
        return false;
    }

    public boolean isDone() {
        return quit;
    }

    public int duration() {
        return duration;
    }

    public void printStates() {
        Gdx.app.log("TutorialStates", "--- Tutorial States ---");
        Gdx.app.log("TutorialStates", "PlayerMoved " + movedPlayer);
        Gdx.app.log("TutorialStates", "BallPicked " + pickedBall);
        Gdx.app.log("TutorialStates", "BallTossed " + tossedBall);
        Gdx.app.log("TutorialStates", "PowerUpSpawned " + spawnedPowerUp);
        Gdx.app.log("TutorialStates", "PowerUpPicked " + pickedPowerUp);
        Gdx.app.log("TutorialStates", "PowerUpUsed " + usedPowerUp);
        Gdx.app.log("TutorialStates", "Complete " + isComplete());

        Gdx.app.log("TutorialStates", "BoostCount " + boostCount);
        Gdx.app.log("TutorialStates", "TossCount " + tossCount);
    }

    public static int getDuration(){
        return duration;
    }
}
