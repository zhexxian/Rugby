package com.forofour.game.tutorialMode;

import com.badlogic.gdx.Gdx;
import com.forofour.game.gameobjects.Player;
import com.forofour.game.handlers.GameMap;
import com.forofour.game.net.GameServer;
import com.forofour.game.net.Network;

/**
 * Created by seanlim on 10/4/2016.
 */
public class TutorialStates {

    private GameMap map;
    private GameServer server;

    private static int duration = 90;
    private boolean movedPlayer = false;
    private boolean pickedBall = false;
    private boolean tossedBall = false;
    private boolean pickedPowerUp = false;
    private boolean usedPowerUp = false;

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
                if(player.positionChanged())
                    movedPlayer = true;
                if(player.hasBall())
                    pickedBall = true;
                if(pickedBall && !player.hasBall())
                    tossedBall = true;
                if(player.hasPowerUp())
                    pickedPowerUp = true;
                if(pickedPowerUp && !player.hasPowerUp())
                    usedPowerUp = true;
            }
            if(movedPlayer && pickedBall && tossedBall && !spawnedPowerUp) {
                spawnInvisibility();
                spawnedPowerUp = true;
            }
        }
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

    public boolean movedPlayer() {
        return movedPlayer;
    }

    public boolean isComplete() {
        if(movedPlayer && pickedBall && tossedBall && pickedPowerUp && usedPowerUp) {
            quit = true;
            return true;
        }
        return false;
    }

    public boolean isDone() {
        return quit;
    }

    public int duration() {
        return duration;
    }

    public void printStates() {
        Gdx.app.log("TutorialStates", "--- States Start ---");
        Gdx.app.log("TutorialStates", "PlayerMoved " + movedPlayer);
        Gdx.app.log("TutorialStates", "BallPicked " + pickedBall);
        Gdx.app.log("TutorialStates", "BallTossed " + tossedBall);
        Gdx.app.log("TutorialStates", "PowerUpSpawned " + spawnedPowerUp);
        Gdx.app.log("TutorialStates", "PowerUpPicked " + pickedPowerUp);
        Gdx.app.log("TutorialStates", "PowerUpUsed " + usedPowerUp);
        Gdx.app.log("TutorialStates", "Complete " + isComplete());
        Gdx.app.log("TutorialStates", "--- States End ---");
    }

    public static int getDuration(){
        return duration;
    }
}
