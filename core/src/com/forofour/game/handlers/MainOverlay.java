package com.forofour.game.handlers;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.forofour.game.actors.TouchPadMaker;
import com.forofour.game.gameobjects.Player;
import com.forofour.game.net.GameClient;
import com.forofour.game.net.Network;

/**
 * Created by seanlim on 4/4/2016.
 */
public class MainOverlay extends Stage {

    private GameClient client;

    private Touchpad touchpad;

    public MainOverlay(GameClient client){
        super();
        this.client = client;
        //make & add actors(HUD components) to the stage
        touchpad = TouchPadMaker.make(client);
        addActor(TouchPadMaker.wrap(touchpad));
    }

    public GameClient getClient(){
        return client;
    }

    public void update(float delta){
        if(client.getMap().getPlayer() != null) { // Player controls
            Player player = client.getMap().getPlayer();
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

        getCamera().update();
        act(delta);
        draw();
    }
}
