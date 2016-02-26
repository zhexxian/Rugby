package com.forofour.game.gameobjects;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by seanlim on 19/2/2016.
 */
public class Ball {
    private Vector2 position;
    private Vector2 velocity;
    private Vector2 acceleration;
    private boolean isHeld;
    private Player holdingPlayer = null;

    private float radius;
    private int maxAcceleration = 40;
    private int maxVelocity = 300;

    private Circle boundingCircle;

    public Ball(float x, float y, float radius){
        this.position = new Vector2(x, y);
        this.velocity = new Vector2(0, 0);
        this.acceleration = new Vector2(0, 0);
        this.isHeld = false;

        this.radius = radius;
        this.boundingCircle = new Circle();
    }

    public void update(float delta){
        // Follower player if in possession by one
        if(isHeld) {
            if(holdingPlayer != null) {
                position.add(holdingPlayer.getX(), holdingPlayer.getY());
            }
        }

        // Ordinary movement
        else {
            velocity.add(acceleration.cpy().scl(delta));

            // Limit velocity
            if (velocity.y > maxVelocity) {
                velocity.y = maxVelocity;
                acceleration.y *= 0.8;
            } else if(velocity.y < -maxVelocity){
                velocity.y = -maxVelocity;
                acceleration.y *= 0.8;
            }
            if (velocity.x > maxVelocity) {
                velocity.x = maxVelocity;
                acceleration.x *= 0.8;
            } else if (velocity.x < -maxVelocity) {
                velocity.x = -maxVelocity;
                acceleration.x *= 0.8;
            }

            position.add(velocity.cpy().scl(delta));
        }

        boundingCircle.set(position.x, position.y, radius);
    }

    public boolean isHeld(){
        return isHeld;
    }

    public void setHoldingPlayer(Player player){
        holdingPlayer = player;
    }
    public Player getHoldingPlayer(){
        return holdingPlayer;
    }

    public float getX() {
        return position.x;
    }
    public float getY() {
        return position.y;
    }

    public Circle getBoundingCircle() {
        return boundingCircle;
    }

}
