package com.forofour.game.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.forofour.game.gameobjects.Ball;
import com.forofour.game.gameobjects.Player;
import com.forofour.game.gameworlds.GameWorld;

/**
 * Multiplexer to allow multiple InputHandlers to be used.
 * Currently useful for debugging purposes as game can be run on desktop
 */
public class InputHandler extends InputMultiplexer{

    private GameWorld myWorld;
    private Ball ball;
    private Player player;

    public InputHandler(GameWorld world){
        super(world); // Assign Stage adapter as the base input adapter
        myWorld = world;
        ball = world.getBall();
        player = world.getPlayer();
    }

    public void reinitialize(){
        ball = myWorld.getBall();
        player = myWorld.getPlayer();
    }

    @Override
    public boolean keyDown(int keycode) {

        if(keycode == Input.Keys.P){
            myWorld.addPlayer();
        }
        if(keycode == Input.Keys.O) {
            myWorld.addBall();
        }

        if(player != null) {
            if(keycode == Input.Keys.C){
                player.dropBall();
            }
            if(keycode == Input.Keys.B) {
                if(!player.hasBall()) {
                    player.boost();
                }
            }
            if(keycode == Input.Keys.L) {
                myWorld.rotatePlayer();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
/*        if(keycode == Input.Keys.UP){
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
        }*/
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
                    System.out.println(screenX + " " + screenY + " " + myWorld.getWidth()+ " " + myWorld.getHeight()
                + " " + Gdx.graphics.getWidth() + " " + Gdx.graphics.getHeight());
           return true;
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
