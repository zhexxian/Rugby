package com.forofour.game.gameworlds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.forofour.game.gameobjects.Ball;
import com.forofour.game.gameobjects.Player;
import com.forofour.game.gameobjects.Wall;
import com.forofour.game.handlers.ButtonMaker;
import com.forofour.game.handlers.TouchPadMaker;

/**
 * Created by seanlim on 19/2/2016.
 */
public class GameWorld extends Stage{

    private World box2d; // Box2d world gravity
    private Ball ball;
    private Wall wallTop, wallBottom, wallLeft, wallRight;
    private Player player, player2;

    private Touchpad touchpad;
    private ImageButton boostButton, tossButton;

    private Body destroyBody;
    private float gameWidth, gameHeight;

    private int time;
    private float runTime = 0;

    public GameWorld(float gameWidth, float gameHeight){
        super();
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;

        box2d = new World(new Vector2(0f, 0f), true);
        box2d.setContactListener(new ListenerClass(this));
        ball = new Ball(gameWidth/2, gameHeight/2, 1f, box2d);
        player = new Player(50, 50, 2f, ball, box2d);
//        player2 = new Player(20, 20, 2f, ball, box2d);

        float wallThickness = 1;
        wallTop = new Wall(0, 0, gameWidth, wallThickness, box2d);
        wallBottom = new Wall(0, gameHeight-wallThickness - 30, gameWidth, wallThickness, box2d);
        wallLeft = new Wall(0, 0, wallThickness, gameHeight, box2d);
        wallRight = new Wall(gameWidth-wallThickness, 0, wallThickness, gameHeight, box2d);

        destroyBody = null;

        touchpad = TouchPadMaker.make(this);
        boostButton = ButtonMaker.getBoostButton(this);
        tossButton = ButtonMaker.getTossButton(this);

        addActor(TouchPadMaker.wrap(touchpad));
        addActor(ButtonMaker.wrap1(boostButton));
        addActor(ButtonMaker.wrap2(tossButton));
    }


    public World getBox2d(){
        return box2d;
    }

    public void update(float delta){
//        Gdx.app.log("GameWorld", "update");
        runTime += delta;

        box2d.step(delta, 8, 3);
        box2d.clearForces();
        player.update(delta);
        ball.update(delta);

        //Stage
        player.knobMove(getTouchpad().getKnobPercentX(), -getTouchpad().getKnobPercentY());
        getCamera().update();
        act(delta);
        draw();
        // end Stage

//        if(destroyBody != null) {
//            box2d.destroyBody(destroyBody);
//            destroyBody = null;
//        }
    }

    public Ball getBall(){
        return ball;
    }

    public Player getPlayer(){
        return player;
    }

    public Touchpad getTouchpad() {return touchpad;}

    public float getGameWidth(){
        return gameWidth;
    }
    public float getGameHeight(){
        return gameHeight;
    }

    public void destroy(Body body) {
        destroyBody = body;
    }
}

class ListenerClass implements ContactListener{

    public GameWorld world;

    public ListenerClass(GameWorld world){
        this.world = world;
    }

    @Override
    public void beginContact(Contact contact) {
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        if(!world.getBall().isHeld()) {
            Body a = contact.getFixtureA().getBody();
            Body b = contact.getFixtureB().getBody();

            if(a.getUserData() instanceof Ball) {
//                world.destroy(a);
                ((Ball) a.getUserData()).setHoldingPlayer((Player) b.getUserData());
            }
            if(b.getUserData() instanceof Ball) {
//                world.destroy(b);
                ((Ball) b.getUserData()).setHoldingPlayer((Player) a.getUserData());
            }
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}