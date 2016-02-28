package com.forofour.game.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.MotorJointDef;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;

import java.util.Random;

/**
 * Created by seanlim on 19/2/2016.
 */
public class Ball extends BodyDef{
    private World box2d;
    private Body body;
    private BodyDef bodyDef;

    private CircleShape boundingCircle ;
    private Fixture fixture;

    private Player holdingPlayer = null;

    private float immunityTime = 0;
    private float radius;
    private int maxAcceleration = 40;
    private int maxVelocity = 300;

    private Vector2 lastDirection;
    private static float IMPULSE_SCALAR = (float) 1.5;
/*
    private MouseJointDef jointDef;
    private MouseJoint joint;
    private Body bodyA;
*/

    public Ball(float x, float y, float radius, World box2d){
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
        fixtureDef.density = 0.1f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.6f; // Make it bounce a little bit
        fixture = body.createFixture(fixtureDef);

        boundingCircle.dispose();

/*        BodyDef aDef = new BodyDef();
        aDef.type = BodyType.DynamicBody;
        bodyA = box2d.createBody(aDef);
        jointDef = new MouseJointDef();
        jointDef.bodyA = bodyA;
        jointDef.collideConnected = true;
        jointDef.maxForce = 500;
        jointDef.bodyB = getBody();*/
    }

/*    public MouseJointDef getJointDef(){
        return jointDef;
    }
    public void makeMouseJoint() {
        joint = (MouseJoint) box2d.createJoint(jointDef);
    }
    public boolean isJoint(){
        return joint != null;
    }
    public MouseJoint getJoint(){
        return joint;
    }*/

    public void update(float delta) {
        if(isHeld()) {
            body.setTransform(holdingPlayer.getBody().getPosition(), 0);
            body.setLinearVelocity(0, 0);

        } else if(immunityTime > 0) {
            immunityTime -= delta;
            System.out.println("Ball invisible " + immunityTime);
        }
//        System.out.println("BALL: " + body.getLinearVelocity().x + " " + body.getLinearVelocity().y);
    }

    public boolean isHeld(){
        return holdingPlayer!=null;
    }

    public void setHoldingPlayer(Player player){
        if(immunityTime <= 0) {
            holdingPlayer = player;
        }
    }
    public Player getHoldingPlayer(){
        return holdingPlayer;
    }

    public void loseHoldingPlayer(){
        immunityTime = 2;

        // Formulate the offset from player origin to release the ball
        Vector2 offset = new Vector2();
        offset.add((float) (holdingPlayer.getRadius() * 1.05), 0);
        offset.rotateRad(holdingPlayer.getBody().getAngle());

        // Place the ball at the offset position
        body.setTransform(holdingPlayer.getBody().getPosition().add(offset), 0);
        body.applyLinearImpulse(holdingPlayer.getLastDirection().scl(IMPULSE_SCALAR), body.getPosition(), true);
        holdingPlayer = null;

    }
    public float getRadius() {
        return radius;
    }
    public Body getBody(){
        return body;
    }

}
