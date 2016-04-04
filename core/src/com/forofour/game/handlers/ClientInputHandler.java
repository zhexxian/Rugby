package com.forofour.game.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.forofour.game.gameobjects.Ball;
import com.forofour.game.gameobjects.Player;
import com.forofour.game.gameworlds.GameWorld;
import com.forofour.game.net.GameClient;

/**
 * Created by seanlim on 4/4/2016.
 */
public class ClientInputHandler extends InputMultiplexer {
    private MainOverlay overlay;
    private GameClient client;
    private Player player;

    public ClientInputHandler(MainOverlay overlay){
        super(overlay); // Assign Stage adapter as the base input adapter
        this.overlay = overlay;
        this.client = overlay.getClient();
        this.player = client.getMap().getPlayer();
    }

    @Override
    public boolean keyDown(int keycode) {

//        if(keycode == Input.Keys.P){
//            client.svAddPlayer();
//        }
//        if(keycode == Input.Keys.O) {
//            myWorld.addBall();
//        }

        if(player != null) {
            if(keycode == Input.Keys.C){
                player.dropBall();
            }
            if(keycode == Input.Keys.B) {
                if(!player.hasBall()) {
                    player.boost();
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return super.keyTyped(character);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        super.touchDown(screenX, screenY, pointer, button);

        if (player != null) {
            System.out.println(screenX + " " + screenY + " " + overlay.getWidth()+ " " + overlay.getHeight()
                    + " " + Gdx.graphics.getWidth() + " " + Gdx.graphics.getHeight());
            return true;
        } else{
            player = client.getMap().getPlayer();
            Gdx.app.log("InputHandler", "Controls binded to player");
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return super.touchUp(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return super.touchDragged(screenX, screenY, pointer);
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return super.mouseMoved(screenX, screenY);
    }

    @Override
    public boolean scrolled(int amount) {
        return super.scrolled(amount);
    }
}
