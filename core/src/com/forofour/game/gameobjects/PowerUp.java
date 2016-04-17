package com.forofour.game.gameobjects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

/**
 * Power Up object
 *  Static body that exists within the box2d(Physics world) and disappears upon being acquired
 *  Automatically adds itself into the physics world when created
 */
public class PowerUp {
    private World box2d;
    private Body body;
    private BodyDef bodyDef; // Simple properties of the physical body
    private CircleShape boundingCircle ; // HitBox, to help with collision
    private Fixture fixture; // Additional properties of the physical body

    private static final float POWER_UP_SIZE = 2f;

    private int type;
    private int id;

    private float radius;
    private boolean outOfFrame;

    // Constructed when called by the server to
    public PowerUp(Vector2 position, int type, int powerUpId, World box2d) {
        this(position.x, position.y, POWER_UP_SIZE, box2d);
        this.type = type;
        this.id = powerUpId;
    }

    public PowerUp(float x, float y, float radius, World box2d){
        this.radius = radius;

        this.box2d = box2d;
        bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody; // Static body processes lesser physics
        bodyDef.position.set(new Vector2(x, y));

        body = box2d.createBody(bodyDef); // Adds the body into the physics world
        body.setUserData(this);

        boundingCircle = new CircleShape();
        boundingCircle.setRadius(radius);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = boundingCircle;
        fixture = body.createFixture(fixtureDef);

        boundingCircle.dispose();
    }

    // Called outside of box2d loop
    public void destroy() {
        box2d.destroyBody(body);
    }

    public int getId(){
        return id;
    }

    public int getType(){
        return type;
    }

    public float getRadius() {
        return radius;
    }

    public Body getBody(){
        return body;
    }

}
