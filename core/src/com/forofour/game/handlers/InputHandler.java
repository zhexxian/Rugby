package com.forofour.game.handlers;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.forofour.game.gameobjects.Ball;
import com.forofour.game.gameobjects.Player;
import com.forofour.game.gameworlds.GameWorld;

/**
 * Created by seanlim on 19/2/2016.
 */
public class InputHandler implements InputProcessor{

    private GameWorld myWorld;
    private Ball ball;
    private Player player;

    public InputHandler(GameWorld world){
        myWorld = world;
        ball = world.getBall();
        player = world.getPlayer();
    }

    @Override
    public boolean keyDown(int keycode) {

        if(keycode == Input.Keys.UP){
            player.move(1, false);
        }
        if(keycode == Input.Keys.DOWN){
            player.move(2, false);
        }
        if(keycode == Input.Keys.LEFT){
            player.move(3, false);
        }
        if(keycode == Input.Keys.RIGHT){
            player.move(4, false);
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if(keycode == Input.Keys.UP){
            player.move(1, true);
        }
        if(keycode == Input.Keys.DOWN){
            player.move(2, true);
        }
        if(keycode == Input.Keys.LEFT){
            player.move(3, true);
        }
        if(keycode == Input.Keys.RIGHT){
            player.move(4, true);
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
