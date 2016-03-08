package com.forofour.game.handlers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.forofour.game.gameobjects.Player;

/**
 * To assist camera in following the player around.
 * Methods that return Viewport x, y position
 */
public class CameraAdjustments {
    private OrthographicCamera cam;
    private Player myPlayer; // player to tie the view to

    private Vector2 playerPos;

    private float mapLeft = 0;
    private float mapRight = GameConstants.GAME_WIDTH;
    private float mapTop = 0;
    private float mapBottom = GameConstants.GAME_HEIGHT + GameConstants.CONTROLS_Y_OFFSET;

    // As the cam is tied to the player, this will be the bounds of the view area
    private float cameraLeft;
    private float cameraRight;
    private float cameraBottom;
    private float cameraTop;

    // The camera dimensions, halved
    private float cameraHalfWidth;
    private float cameraHalfHeight;

    public CameraAdjustments(OrthographicCamera cam, Player player) {
        this.myPlayer = player;
        this.cam = cam;
        this.cameraHalfWidth = cam.viewportWidth * .5f;
        this.cameraHalfHeight = cam.viewportHeight * .5f;
    }

    private void refreshCameraBounds(){
        playerPos = myPlayer.getBody().getPosition(); // Initialize value for use

        cameraLeft = playerPos.x - cameraHalfWidth;
        cameraRight = playerPos.x + cameraHalfWidth;
        cameraBottom = playerPos.y + cameraHalfHeight;
        cameraTop = playerPos.y - cameraHalfHeight;
    }

    private float getX(){

        // Horizontal axis
        if(cameraLeft <= mapLeft)
        {
            return mapLeft + cameraHalfWidth;
        }
        else if(cameraRight >= mapRight)
        {
            return mapRight - cameraHalfWidth;
        }
        else
            return playerPos.x;
    }
    private float getY(){
        // Vertical axis
        if(cameraBottom >= mapBottom)
        {
            return mapBottom - cameraHalfHeight;
        }
        else if(cameraTop <= mapTop)
        {
            return mapTop + cameraHalfHeight;
        }
        else
            return playerPos.y;
    }

    public Vector3 getPosition() {
        refreshCameraBounds(); //Require to refresh before get methods can be called
        return new Vector3(getX(), getY(), 0f);
    }
}
