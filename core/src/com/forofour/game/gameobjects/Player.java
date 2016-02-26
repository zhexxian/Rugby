package com.forofour.game.gameobjects;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by seanlim on 19/2/2016.
 */
public class Player {
    private Vector2 position;
    private Vector2 velocity;
    private Vector2 acceleration;

    private Ball ball;
    private boolean hasBall;

    private float radius;
    private int maxAcceleration = 70;
    private int maxVelocity = 200;
    private boolean moveX, moveY;

    private Circle boundingCircle;

    public Player(float x, float y, float radius, Ball ball){
        this.ball = ball;
        this.radius = radius;

        position = new Vector2(x,y);
        velocity = new Vector2(0, 0);
        acceleration = new Vector2(0, 0);
        hasBall = false;
        boundingCircle = new Circle();
    }

    public void update(float delta) {
        velocity.add(acceleration.cpy().scl(delta));

        // Limit velocity
        if(moveY) {
            if (velocity.y > maxVelocity) {
                velocity.y = maxVelocity;
            } else if (velocity.y < -maxVelocity) {
                velocity.y = -maxVelocity;
            }
        }
        if(moveX) {
            if (velocity.x > maxVelocity) {
                velocity.x = maxVelocity;
            } else if (velocity.x < -maxVelocity) {
                velocity.x = -maxVelocity;
            }
        }

        if(!moveY){
            velocity.y *= 0.8;
        }
        if(!moveX){
            velocity.x *= 0.8;
        }

        position.add(velocity.cpy().scl(delta));
        boundingCircle.set(position.x, position.y, radius);
    }

    public void move(float thetre, float magnitude){
    }

    public void move(int direction, boolean toStop){
        // direction - up/down/left/right - 1/2/3/4
        if(toStop){
            if(direction == 1 || direction == 2) {
                moveY = false;
                acceleration.y = 0;
//                velocity.y = 0;
            }
            else if(direction == 3 || direction == 4) {
                moveX = false;
                acceleration.x = 0;
//                velocity.x = 0;
            }
            else {
                moveX = false;
                moveY = false;
                acceleration.y = 0;
                acceleration.x = 0;
//                velocity.x = 0;
//                velocity.y = 0;
            }
            return;
        }

        if(direction == 1) {
            moveY = true;
            acceleration.y = -maxAcceleration;
        }
        else if(direction == 2) {
            moveY = true;
            acceleration.y = maxAcceleration;
        }
        else if(direction == 3) {
            moveX = true;
            acceleration.x = -maxAcceleration;
        }
        else if(direction == 4) {
            moveX = true;
            acceleration.x = maxAcceleration;
        }

    }

    public boolean hasBall(){
        return hasBall;
    }
    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }

    public float getRadius() {
        return radius;
    }
    public Circle getBoundingCircle() {
        return boundingCircle;
    }
}
