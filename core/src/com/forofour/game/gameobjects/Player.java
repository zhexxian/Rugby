package com.forofour.game.gameobjects;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by seanlim on 19/2/2016.
 */
public class Player {
    private World box2d;
    private Body body;
    private BodyDef bodyDef;
    private CircleShape boundingCircle ;
    private Fixture fixture;

    private Ball ball;
    private boolean hasBall;

    private float radius;
    private float moveX, moveY;

    private static Vector2 lastDirection;
    private static int MAX_IMPULSE = 12;
    private int MAX_VELOCITY = 30;
    private double BOOST_SCALAR = 1.3;
    private float boostTime = 0;

    public Player(float x, float y, float radius, Ball ball, World box2d){
        hasBall = false;
        this.ball = ball;
        this.radius = radius;

        this.box2d = box2d;
        bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(new Vector2(x, y));
        bodyDef.linearDamping = 0.1f;
//        bodyDef.fixedRotation = true;

        body = box2d.createBody(bodyDef);
        body.setUserData(this);

        boundingCircle = new CircleShape();
        boundingCircle.setRadius(radius);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = boundingCircle;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 1f;
        fixtureDef.restitution = 0.2f; // Make it bounce a little bit
        fixture = body.createFixture(fixtureDef);

        boundingCircle.dispose();
        lastDirection = new Vector2();
    }

    public void update(float delta){

        // Angular rotation
        float radInitial = (float) Math.abs(body.getAngle()%(2*Math.PI));
        float radGoal = (float) ((Math.PI/180)*(getLastDirection().angle()%360));
        float radians = (radGoal - radInitial);
        if(radians < -Math.PI)
            radians = (float) (2*Math.PI + radians);
        body.setAngularVelocity((radians * getRadius()) * 10);

        if(boostTime > 0) {
            boostTime -= delta;
            System.out.println("Player boost " + boostTime);
        }

        // Linear movement
        if(body.getLinearVelocity().x < MAX_VELOCITY && body.getLinearVelocity().x > -MAX_VELOCITY)
            body.applyLinearImpulse(moveX, 0, body.getPosition().x, body.getPosition().y, true);
        if(body.getLinearVelocity().y < MAX_VELOCITY && body.getLinearVelocity().y > -MAX_VELOCITY)
            body.applyLinearImpulse(0, moveY, body.getPosition().x, body.getPosition().y, true);
//        body.applyForceToCenter(moveX, moveY, true);

//        System.out.println("PLAYER: " + body.getLinearVelocity().x + " " + body.getLinearVelocity().y);
//        System.out.println(String.format("%5.5f %5.5f %5.5f", radInitial, radGoal, radians));

    }

    public void knobMove(float x, float y) {
        if(boostTime > 0) {
            x *= BOOST_SCALAR;
            y *= BOOST_SCALAR;
        }
        if(x != 0 || y != 0)
            lastDirection.set(x, y);

        body.setLinearVelocity(x*MAX_VELOCITY, y*MAX_VELOCITY);
    }

//    public void move(int direction, boolean toStop) {
//        // direction - up/down/left/right - 1/2/3/4
//        if (toStop) {
//            if (direction == 1 || direction == 2) {
//                moveY = 0;
//                body.setLinearVelocity(body.getLinearVelocity().x, 0);
//
//            } else if (direction == 3 || direction == 4) {
//                moveX = 0;
//                body.setLinearVelocity(0, body.getLinearVelocity().y);
//
//            } else {
//                body.setLinearVelocity(0, 0);
//                moveX = 0;
//                moveY = 0;
//            }
//        }
//        else {
//            if(direction == 1) { // UP
//                moveY = -MAX_IMPULSE;
//            }
//            else if(direction == 2) { //DOWN
//                moveY = MAX_IMPULSE;
//            }
//            else if(direction == 3) { // LEFT
//                moveX = -MAX_IMPULSE;
//            } else if (direction == 4) { // RIGHT
//                moveX = MAX_IMPULSE;
//            }
//        }
//    }

    public Vector2 getLastDirection(){
        return lastDirection;
    }

    public void boost() {
        if(boostTime <= 0 && !hasBall()) {
            boostTime = 1;
        }
    }

    public void dropBall(){
        if(hasBall())
            ball.loseHoldingPlayer();
    }

    public boolean hasBall(){
        if(ball.isHeld())
            return ball.getHoldingPlayer().equals(this);
        return false;
    }

    public float getRadius() {
        return radius;
    }

    public Body getBody(){
        return body;
    }
}
