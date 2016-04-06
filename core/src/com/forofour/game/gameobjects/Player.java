package com.forofour.game.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.forofour.game.net.GameClient;
import com.forofour.game.net.Network;

/**
 * Created by seanlim on 19/2/2016.
 */
public class Player {

    private int id;
    private GameClient client;

    private World box2d;
    private Body body;
    private BodyDef bodyDef;
    private CircleShape boundingCircle ;
    private Fixture fixture;

    private Ball ball;
    private int teamId;

    private float radius;

    private Vector2 lastDirection;
    private Vector2 lastPosition;

    //TODO: move the constants to handlers/GameConstants
    private static final int MAX_VELOCITY = 30;
    private static final float BOOST_SCALAR = 1.3f;
    private static final float BOOST_DURATION = 1;
    private static final float REVERSE_DIRECTION_DURATION = 5;
    private static final float MOVE_VERY_SLOWLY_DURATION = 5;
    private static final float NO_BOOST_DURATION = 2;
    private float boostTime = 0;
    private float reverseDirectionTime = 0;
    private float moveVerySlowlyTime = 0;
    private float noBoostTime = 0;

    private boolean hasPowerUp;

    public Player(int id, Vector2 pos, World box2d, GameClient client) {
        this(pos.x, pos.y, 2, null, box2d);
        this.id = id;
        this.client = client;
    }

    public Player(float x, float y, float radius, Ball ball, World box2d){
        this.ball = ball;
        this.radius = radius;

        this.box2d = box2d;
        bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(new Vector2(x, y));
        bodyDef.linearDamping = 0.1f;
        bodyDef.fixedRotation = true;

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
        lastPosition = body.getPosition().cpy();

        hasPowerUp = false;
    }

    public void update(float delta){
        if(ball == null) { // Find the ball if no instance is attached to it
            Array<Body> bodyArrayList = new Array<Body>();
            box2d.getBodies(bodyArrayList);
            for(Body b :bodyArrayList) {
                if(b.getUserData() instanceof Ball)
                    ball = (Ball) b.getUserData();
            }
        }

        // Acquire and set angular direction
        float radInitial = (float) Math.abs(body.getAngle()%(2*Math.PI));
        float radGoal = (float) ((Math.PI/180)*(getLastDirection().angle()));
        float deltaRad = (radGoal - radInitial);
        if(deltaRad < -Math.PI)
            deltaRad = (float) (2*Math.PI + deltaRad);

        if(boostTime > 0)
            boostTime -= delta;
        if(noBoostTime > 0)
            noBoostTime -= delta;
        if(reverseDirectionTime > 0)
            reverseDirectionTime -= delta;
        if(moveVerySlowlyTime > 0)
            moveVerySlowlyTime -= delta;

        body.setAngularVelocity((deltaRad * getRadius()) * 5);

    }

    public void knobMove(float x, float y) {
        // Remembers last moved direction
        if(x != 0 || y != 0)
            lastDirection.set(x, y);

        // Apply boost multiplier(if required)
        if(boostTime > 0) {
            x *= BOOST_SCALAR;
            y *= BOOST_SCALAR;

            noBoostTime = NO_BOOST_DURATION; // Duration before allowed to call boost again
            System.out.println("Player - boost " + boostTime);
        }

        body.setLinearVelocity(x * MAX_VELOCITY, y * MAX_VELOCITY);

        client.sendMessageUDP(new Network.PacketPlayerUpdateFast(id, body.getLinearVelocity()));
    }

    public Vector2 getLastDirection(){
        return lastDirection;
    }

    public void boost() {
        // Returns if not allowed to boost
        if(noBoostTime > 0) {
            System.out.println("Player - no boost " + noBoostTime);
            return;
        }

        // Only boost if not holding ball
        if(boostTime <= 0 && !hasBall()) {
            boostTime = BOOST_DURATION;
        }
    }

    public void dropBall(){
        if(hasBall()) {
            ball.loseHoldingPlayer();
            client.sendMessage(new Network.PacketDropBall(id));
            Gdx.app.log("Player-dropBall", "id " + id);
        }
    }

    public boolean hasBall(){ // IMPORTANT : Checks that holding player is current player
        if(ball != null) {
            if(ball.getHoldingPlayerId() == id) {
                return true;
            }
            // NOTE: Below method keeps crashing.
//            if (ball.isHeld()) {
//                System.out.println("hasBall " + ball.getHoldingPlayer().equals(this));
//                return ball.getHoldingPlayer().equals(this);
//            }
        }
        return false;
    }

    public void acquirePowerUp(){
        hasPowerUp = true;
    }
    public void usePowerUp(){
        hasPowerUp = false;
        //reverseDirectionControl();
        moveVerySlowly();
    }
    public boolean hasPowerUp() {
        return hasPowerUp;
    }

    // Power Up: poop -- direction control reversed, up is down, left is right, and vice versa
    public void reverseDirectionControl(){
        reverseDirectionTime = REVERSE_DIRECTION_DURATION;
    }

    public float getReverseDirectionTime() {
        return reverseDirectionTime;
    }

    // Power Up: vomit -- move very slowly
    public void moveVerySlowly(){
        moveVerySlowlyTime = MOVE_VERY_SLOWLY_DURATION;
    }

    public float getMoveVerySlowlyTime() {
        return moveVerySlowlyTime;
    }

    public float getRadius() {
        return radius;
    }

    public Body getBody(){
        return body;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public Vector2 getPosition() {
        return body.getPosition();
    }
    public boolean positionChanged() {
        if(lastPosition.equals(body.getPosition())) {
            return false;
        }
        else {
            lastPosition = body.getPosition().cpy();
        }
        return true;
    }
    public float getAngle(){
        return body.getAngle();
    }

    public int getId(){
        return id;
    }
}
