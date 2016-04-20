package com.forofour.game.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.forofour.game.handlers.AssetLoader;
import com.forofour.game.handlers.GameConstants;
import com.forofour.game.net.GameClient;
import com.forofour.game.net.Network;

/**
 * Player Object that adds itself into the box2d world upon creation
 *  Will hold reference to the ball for manipulation of it
 *  Also, to belong to a team as assigned by the server
 */
public class Player {

    private int id;
    private GameClient client;
    private float runTime, lastMoveTime;

    private World box2d; // Physics world that will contain bodies of the object
    private Body body;
    private BodyDef bodyDef; // Allows with definition of the body
    private CircleShape boundingCircle ; // Defines the hitbox of the body
    private Fixture fixture; // Allows with further definition of the body

    private Ball ball; // Ref to ball instance
    private int teamId;
    private Team ownTeam, otherTeam;

    private float radius;

    private Vector2 lastDirection; // Orientation of the player
    private Vector2 lastPosition;

    private static final float PLAYER_SIZE = 2.5f;

    private static final int MAX_VELOCITY = 30; // Default MaxVelocity of the player
    private static final float BOOST_SCALAR = 1.5f; // Scales velocity when boost is triggered

    // Durations for player effects
    private static final float BOOST_DURATION = 1;
    private static final float NO_BOOST_DURATION = 2; // Boost cool down period
    private static final float SLOW_DURATION = 5;
    private static final float CONFUSION_DURATION = 5;
    private static final float INVISIBLE_DURATION = 5;

    // Default Scalar when the effects are not in play
    private float slow_scale = 1;
    private float confuse_x = 1;
    private float confuse_y = 1;
    private float invisibility_scale = 1;

    // Default duration when effects are not in play
    private float boostTime = 0;
    private float noBoostTime = 0;

    private float slowEffectTime = 0;
    private float confusionEffectTime = 0;
    private float invisibleEffectTime = 0;

    private int powerUpType; // Indicates type of power up 1/2/3 (Slow/Confusion/Invisibility)
    private boolean hasPowerUp;

    public Player(int id, Vector2 pos, World box2d, GameClient client) {
        this(pos.x, pos.y, PLAYER_SIZE, null, box2d);
        this.id = id;
        this.client = client;
        runTime =0;
        lastMoveTime = 0;
    }

    public Player(float x, float y, float radius, Ball ball, World box2d){
        this.ball = ball;
        this.radius = radius;

        // Properties of the body within the physics world
        this.box2d = box2d;
        bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(new Vector2(x, y));
        bodyDef.linearDamping = 0.1f;
        bodyDef.fixedRotation = true; // Disables rotation by linear velocity. Overwritten by custom rotation methods

        body = box2d.createBody(bodyDef); // Add body into physics world
        body.setUserData(this); // Links body to object

        // Additional properties of the body within physics world
        boundingCircle = new CircleShape();
        boundingCircle.setRadius(radius);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = boundingCircle;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 1f;
        fixtureDef.restitution = 0f; // Disable bounce of the player. Prevents bouncing off walls
        fixture = body.createFixture(fixtureDef);

        boundingCircle.dispose();
        lastDirection = Vector2.Zero;
        lastPosition = new Vector2(x, y); // Remembers Original Position

        hasPowerUp = false;
    }

    public void update(float delta){
        runTime += delta;
        if(ball == null) { // Find the ball if no instance is attached to it
            Array<Body> bodyArrayList = new Array<Body>();
            box2d.getBodies(bodyArrayList);
            for(Body b :bodyArrayList) {
                if(b.getUserData() instanceof Ball)
                    ball = (Ball) b.getUserData();
            }
        }

        // Sets last moved direction as player's Orientation
        if(!body.getLinearVelocity().epsilonEquals(Vector2.Zero, 5)) { // Prevents flipping of direction at corners
            lastDirection = body.getLinearVelocity();
            body.setTransform(body.getPosition(), body.getLinearVelocity().angleRad());
        }

        updateEffects(delta); // Reduction of time for Effects and disables when over

        // Checks if holding ball for too long
        if(ball.heldTooLong())
            dropBall();
    }

    // Periodically called to reduce duration left for special effects
    private void updateEffects(float delta) {
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
            invisibility_scale *= 0.95; // Rate^ at which player turns invisible
            invisibleEffectTime -= delta;
        }
        else
            deactivateInvisibleEffect();
    }

    // State of boosting
    public boolean isBoosting(){
        if(boostTime > 0)
            return true;
        return false;
    }

    // State of boost cooling down
    public boolean isBoostCooldown(){
        if(boostTime > 0 || noBoostTime > 0)
            return true;
        return false;
    }
    public float boostCooldownPercentage(){
        float percentage = 1f - (noBoostTime/NO_BOOST_DURATION);
        if(percentage<0)
            return 0;
        if(percentage>1)
            return 1;
        return percentage;
    }

    // called by TouchPad periodically to update player movement
    // x,y values as a percentage
    public void knobMove(float x, float y) {
        // Apply boost multiplier(if required)
        if(boostTime > 0) {
            x *= BOOST_SCALAR;
            y *= BOOST_SCALAR;

            noBoostTime = NO_BOOST_DURATION; // Duration before allowed to call boost again
            System.out.println("Player - boost " + boostTime);
        }

        Vector2 newLinearVelocity = new Vector2(x * MAX_VELOCITY * slow_scale * confuse_x, y * MAX_VELOCITY * slow_scale * confuse_y);
        body.setLinearVelocity(newLinearVelocity);

        // Following line works as a "latency smoother"
        // Sending the linear velocity to the server, will quickly set the velocity of the body within the server physics engine
        // Re-sync of position is done with the slow update.
        if(runTime - lastMoveTime > 0.04) {
            client.sendMessageUDP(new Network.PacketPlayerUpdateFast(id, body.getLinearVelocity()));
            lastMoveTime = runTime;
        }
    }

    // Use of powerUp will update Server
    public void powerUp() {
        client.sendMessage(new Network.PacketUsePowerUp());
    }

    // Triggered by the BoostButton press in MainOverlay
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

    public void dropBall(){ // Client Sided command(NOTE: Server has no instance of client)
        if(hasBall() && client != null) {
            ball.loseHoldingPlayer();
            client.sendMessage(new Network.PacketDropBall(id)); // Updates server of the dropping of ball
            Gdx.app.log("Player-dropBall", "id " + id);
        }
    }

    public boolean hasBall(){ // IMPORTANT : Checks that holding player is current player
        if(ball != null) {
            if(ball.getHoldingPlayerId() == id) {
                return true;
            }
        }
        return false;
    }

    // Call from server to assign powerUp upon picking up(Server-sided logic)
    public void acquirePowerUp(int type){
        hasPowerUp = true;
        powerUpType = type;
        Gdx.app.log("Player"+id, "Acquired" +type);
        AssetLoader.powerUpSound.play(GameConstants.SOUND_VOLUME); // Acquired power up sound
    }

    // Call from server that powerUp usage is being called.
    // generateChoice - Random number from server to instantiate type of confusion
    public void usePowerUp(int generatedChoice){
        if(hasPowerUp()) {
            hasPowerUp = false;
            if (powerUpType == 1) { // SLOW - Only to opposing team members
                // TODO: Add slow effect sound
                for(Player p : otherTeam.getTeamList())
                    p.activateSlowEffect();
            } else if (powerUpType == 2) { // CONFUSION - Only to opposing team members
                // TODO: Add confused effect sound
                for(Player p : otherTeam.getTeamList())
                    p.activateConfusionEffect(generatedChoice);
            } else if (powerUpType == 3) { // Invisibility - To self
                // TODO: Add invisible effect sound
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

    public int getPowerUpType() {
        return powerUpType;
    }

    // ACTIVATE EFFECTS CALLED BY THE SERVER
    // Set movement speed scalar, and duration of effect
    public void activateSlowEffect(){
        slow_scale = 0.9f;
        slowEffectTime = SLOW_DURATION;
    }
    // Set the inverse to the controls, based on server's choice
    public void activateConfusionEffect(int generatedNumber){
        switch(generatedNumber) {
            case 0: confuse_x = -1; confuse_y = -1; break;
            case 1: confuse_x = -1; confuse_y = 0.7f; break;
            case 2: confuse_x = 1; confuse_y = -0.7f; break;
            case 3: confuse_x = -0.7f; confuse_y = 1; break;
            default:
                confuse_x = 1; confuse_y = 1; break;
        }
        confusionEffectTime = CONFUSION_DURATION;
        Gdx.app.log("Player"+id, "Activate x:" + confuse_x + " y:"+confuse_y);
    }
    // Set invisibility duration
    public void activateInvisibleEffect(){
        invisibleEffectTime = INVISIBLE_DURATION;
    }

    // Returns the values back to original state
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

    public boolean isSlowed(){
        if(slowEffectTime > 0)
            return true;
        return false;
    }
    public boolean isConfused(){
        if(confusionEffectTime > 0)
            return true;
        return false;
    }

    public float getSlowEffectTime() {
        return slowEffectTime;
    }
    public float getConfusionEffectTime() {
        return confusionEffectTime;
    }
    public float getInvisibility_scale(int playerId) {
        if(playerId == id) {
            if(invisibility_scale < 0.35) { // Minimum Opacity of Player
                return 0.35f;
            }
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
        // If difference is more than set amount, epsilon returns false
        if(!lastPosition.epsilonEquals(getPosition(), 20)) {
            return true;
        }
        return false;
    }

    public float getAngle(){
        return body.getAngle();
    }
    public float getAngleDegree(){
        return body.getAngle() * MathUtils.radiansToDegrees;
    }
    public Vector2 getLastDirection() {
        return lastDirection;
    }

    public int getId(){
        return id;
    }

    public void setTeam(Team ownTeam, Team otherTeam) {
        this.ownTeam = ownTeam;
        this.otherTeam = otherTeam;
    }
}
