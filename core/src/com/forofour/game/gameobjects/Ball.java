package com.forofour.game.gameobjects;

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
public class Ball extends BodyDef{
    private World box2d;
    private Body body;
    private BodyDef bodyDef;

    private CircleShape boundingCircle ;
    private Fixture fixture;

    private Player holdingPlayer = null;

    private float immunityTime = 0;
    private float radius;

    private static float IMPULSE_SCALAR = (float) 1.5;
    private boolean playerCollided;

    public Ball(Vector2 pos, World box2d) {
        this(pos.x, pos.y, 2, box2d);
    }

    public Ball(float x, float y, float radius, World box2d) {
        this.radius = radius;

        this.box2d = box2d;
        bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(new Vector2(x, y));
        bodyDef.linearDamping = 0.1f;
//        bodyDef.fixedRotation = true;

        body = box2d.createBody(bodyDef);
        body.setGravityScale(0);
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
        playerCollided = false;
    }

    public void update(float delta) {
        if(isHeld()) {
            body.setTransform(holdingPlayer.getBody().getPosition(), 0); // Assume holdingPlayer !=null
            body.setLinearVelocity(0, 0);

            if(playerCollided) {
                playerCollided = false;
                loseHoldingPlayer();
            }

        } else if(immunityTime > 0) {
            immunityTime -= delta;
            System.out.println("Ball invisible " + immunityTime);
        }
    }

    public boolean isHeld(){
        return holdingPlayer!=null;
    }

    public void triggerCollision(){
        playerCollided = true; // SENSITIVE : Triggers collided state, ball to lose player upon update
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

        // Remove holding player
        holdingPlayer = null;
    }
    public float getRadius() {
        return radius;
    }
    public Body getBody(){
        return body;
    }

}
