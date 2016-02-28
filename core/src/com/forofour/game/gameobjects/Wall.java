package com.forofour.game.gameobjects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by seanlim on 26/2/2016.
 */
public class Wall extends BodyDef {

    public Wall (float x, float y, float width, float height, World world) {
// Create our body definition
        BodyDef groundBodyDef =new BodyDef();
// Set its world position
        groundBodyDef.position.set(new Vector2(x, y));

// Create a body from the defintion and add it to the world
        Body groundBody = world.createBody(groundBodyDef);

// Create a polygon shape
        PolygonShape groundBox = new PolygonShape();
// Set the polygon shape as a box which is twice the size of our view port and 20 high
// (setAsBox takes half-width and half-height as arguments)
        groundBox.setAsBox(width, height);
        // Create a fixture from our polygon shape and add it to our ground body
        groundBody.createFixture(groundBox, 0.0f);
    // Clean up after ourselves
        groundBox.dispose();
    }
}
