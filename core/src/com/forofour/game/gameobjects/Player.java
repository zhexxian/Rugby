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
    private static final float NO_BOOST_DURATION = 2;

    private static final float SLOW_DURATION = 5;
    private static final float CONFUSION_DURATION = 5;
    private static final float INVISIBLE_DURATION = 5;

    private float slow_scale = 1;
    private float confuse_x = 1;
    private float confuse_y = 1;
    private float invisibility_scale = 1;

    private float boostTime = 0;
    private float noBoostTime = 0;

    private float slowEffectTime = 0;
    private float confusionEffectTime = 0;
    private float invisibleEffectTime = 0;

    private int powerUpType;
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
        body.setAngularVelocity((deltaRad * getRadius()) * 5);

        if(boostTime > 0)
            boostTime -= delta;
        if(noBoostTime > 0)
            noBoostTime -= delta;

        if(slowEffectTime > 0)
            slowEffectTime -= delta;
        else
            deactivateSlowEffect();

        if(confusionEffectTime > 0)
            confusionEffectTime -= delta;
        else
            deactivateConfusionEffect();

        if(invisibleEffectTime > 0) {
            invisibility_scale *= 0.95; // Rate^ at which player turns
            invisibleEffectTime -= delta;
        }
        else
            deactivateInvisibleEffect();
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

        body.setLinearVelocity(x * MAX_VELOCITY * slow_scale * confuse_x, y * MAX_VELOCITY * slow_scale * confuse_y);

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

    public void acquirePowerUp(int type){
        hasPowerUp = true;
        powerUpType = type;
        Gdx.app.log("Player"+id, "Acquired" +type);
    }
    public void usePowerUp(){
        if(hasPowerUp()) {
            hasPowerUp = false;
            //reverseDirectionControl();
            if (powerUpType == 1) {
                activateSlowEffect();
            } else if (powerUpType == 2) {
                activateConfusionEffect(1);
            } else if (powerUpType == 3) {
                activateInvisibleEffect();
            }
            Gdx.app.log("Player" + id, "Used" + powerUpType);
        }
        else {
            Gdx.app.log("Player" + id, "No power up to use");
        }
    }
    public boolean hasPowerUp() {
        return hasPowerUp;
    }


    // Power Up: vomit -- move very slowly
    public void activateSlowEffect(){
        slow_scale = 0.5f;
        slowEffectTime = SLOW_DURATION;
    }

    public void activateConfusionEffect(int x){
        switch(x) {
            case 1: confuse_x = -1; confuse_y = -1; break;
            case 2: confuse_x = -1; confuse_y = 1; break;
            case 3: confuse_x = 1; confuse_y = -1; break;
            case 4: confuse_x = 1; confuse_y = 1; break;
            default:
                confuse_x = 1; confuse_y = 1; break;
        }
        confusionEffectTime = CONFUSION_DURATION;
        Gdx.app.log("Player"+id, "Activate x:" + confuse_x + " y:"+confuse_y);
    }
    public void activateInvisibleEffect(){
        invisibleEffectTime = INVISIBLE_DURATION;
    }

    public void deactivateSlowEffect(){
        slow_scale = 1;
    }
    public void deactivateConfusionEffect(){
        confuse_x = 1;
        confuse_y = 1;
    }
    public void deactivateInvisibleEffect(){
        invisibility_scale = 1;
    }

    public float getSlowEffectTime() {
        return slowEffectTime;
    }
    public float getConfusionEffectTime() {
        return confusionEffectTime;
    }

    public float getInvisibility_scale() {
        if(invisibility_scale < 0.2) { // Minimum Opacity of Player
            return 0.2f;
        }
        return invisibility_scale;
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
