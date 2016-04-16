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
 * Created by zhexian on 3/18/2016.
 */
public class PowerUp {
    private World box2d;
    private Body body;
    private BodyDef bodyDef;
    private CircleShape boundingCircle ;
    private Fixture fixture;

    private static final float POWER_UP_SIZE = 2f;

    private int type;
    private int id;

    private float radius;
    private boolean outOfFrame;

    public PowerUp(Vector2 position, int type, int powerUpId, World box2d) {
        this(position.x, position.y, POWER_UP_SIZE, box2d);
        this.type = type;
        this.id = powerUpId;
    }

    public PowerUp(float x, float y, float radius, World box2d){
        this.radius = radius;

        this.box2d = box2d;
        bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(new Vector2(x, y));
//        bodyDef.linearDamping = 0.1f;
//        bodyDef.fixedRotation = true;

        body = box2d.createBody(bodyDef);
        body.setUserData(this);

        boundingCircle = new CircleShape();
        boundingCircle.setRadius(radius);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = boundingCircle;
//        fixtureDef.density = 0.5f;
//        fixtureDef.friction = 1f;
//        fixtureDef.restitution = 0.2f; // Make it bounce a little bit
        fixture = body.createFixture(fixtureDef);

        boundingCircle.dispose();

        outOfFrame = false;
    }

    public void update(float delta){
        if(outOfFrame) {
            disappear();
        }
    }

    public void destroy() {
        box2d.destroyBody(body);
    }

    public boolean isOutOfFrame(){
        return outOfFrame;
    }

    private void disappear() {
        body.setTransform(-10, -10, 0);
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
