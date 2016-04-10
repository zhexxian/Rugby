package com.forofour.game.tutorialMode;

import com.forofour.game.handlers.GameMap;

/**
 * Created by seanlim on 10/4/2016.
 */
public class TutorialStates {

    public int duration = 90;
    public boolean movedPlayer = false;
    public boolean pickedBall = false;
    public boolean tossedBall = false;
    public boolean pickedPowerUp = false;
    public boolean usedPowerUp = false;

    public TutorialStates(GameMap map){
        map.setGameDuration(duration);
    }
}
