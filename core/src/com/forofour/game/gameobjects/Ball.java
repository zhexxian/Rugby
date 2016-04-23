package com.forofour.game.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.forofour.game.handlers.GameConstants;

/**
 * Ball Object (MILK BOTTLE)
 *  helper that adds itself into the Box2d world upon creation
 *  it holds reference to the ballHolder and methods that manipulates the ball velocity and held state by player
 */
public class Ball{
    private World box2d;
    private Body body;
    private BodyDef bodyDef;

    private static final float BALL_RADIUS = 2f; // Defined size of the ball
    private static final float MAX_HELD_TIME =4f;

    // Properties of the Body
    private CircleShape boundingCircle ;
    private Fixture fixture;

    private float heldTime = 0;
    private float immunityTime = 0;
    private float radius;

    private static float IMPULSE_SCALAR = (float) 1.5; // Impulse multiplier when player drops the MilkBottle

    private boolean shownBall;

    private Player holdingPlayer = null;
    private boolean playerCollided;

    public Ball(Vector2 pos, World box2d) {
        this(pos.x, pos.y, BALL_RADIUS, box2d);
    }

    public Ball(float x, float y, float radius, World box2d) {
        this.radius = radius;

        this.box2d = box2d;
        bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(new Vector2(x, y));
        bodyDef.linearDamping = 0.1f;
        bodyDef.fixedRotation = true;

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
            // Should ball be held. Ball moves with the player
            body.setActive(false); // Disables physics, improves performance
            body.setTransform(holdingPlayer.getBody().getPosition(), 0);
            body.setLinearVelocity(holdingPlayer.getBody().getLinearVelocity());

            if(playerCollided) { // Trigger out of Box2d loop to make changes to the body
                playerCollided = false;
                loseHoldingPlayer(); // LOSE BALL Sequence
            }

            // Ball cannot be held for more than MAX_BALL_DURATION time
            if(!heldTooLong())
                heldTime -= delta;
        }

        // Ball cannot be held for the given period
        else if(immunityTime > 0) {
            immunityTime -= delta;
            //Gdx.app.log("Ball" , "is invisible " + immunityTime);
        }
    }

    // Check if player is holding ball for more than a MAX_HOLDING_DURATION
    public boolean heldTooLong() {
        if(heldTime < 0)
            return true;
        return false;
    }

    public boolean isHeld(){
        return holdingPlayer!=null;
    }

    public void triggerCollision(){
        playerCollided = true; // SENSITIVE : Triggers collided state, ball to lose player upon update
    }

    // Update from server as collision between ball and player happens
    public synchronized void setHoldingPlayer(Player player){
        if(immunityTime <= 0) {
            holdingPlayer = player;
            heldTime = MAX_HELD_TIME;
        }
    }
    public Player getHoldingPlayer(){
        return holdingPlayer;
    }
    public int getHoldingPlayerId() {
        if(holdingPlayer == null)
            return -1;
        return holdingPlayer.getId();
    }

    // Triggers the ball being dropped or tossed by the player
    // Ball physics that occur when the player loses the ball
    public void loseHoldingPlayer(){
        if(holdingPlayer != null) {
            body.setActive(true); // Enable Physics
            immunityTime = 1;

            // Formulate the offset from player origin to release the ball
            Vector2 offset = new Vector2();
            offset.add((float) (holdingPlayer.getRadius() * 1.10), 0);
            offset.rotateRad(holdingPlayer.getBody().getAngle());

            // Place the ball at the offset position
            body.setTransform(holdingPlayer.getBody().getPosition().add(offset), 0);
            body.setLinearVelocity(offset.scl(10));
            body.applyLinearImpulse(holdingPlayer.getLastDirection().scl(IMPULSE_SCALAR), body.getPosition(), true);
        }
        // Remove holding player
        holdingPlayer = null;
    }

    public void hideBall(){
        shownBall = false;
        body.setTransform(new Vector2(-10, -10), 0);
    }
    public void showBall(){
        if(!shownBall) {
            shownBall = true;
            body.setTransform(new Vector2(GameConstants.GAME_WIDTH / 2, GameConstants.GAME_HEIGHT / 2), 0);
        }
    }

    public float getRadius() {
        return radius;
    }
    public Body getBody(){
        return body;
    }

}
