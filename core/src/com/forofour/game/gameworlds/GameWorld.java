package com.forofour.game.gameworlds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.forofour.game.actors.PowerUpSlotMaker;
import com.forofour.game.actors.TextLabelMaker;
import com.forofour.game.gameobjects.Ball;
import com.forofour.game.gameobjects.Player;
import com.forofour.game.gameobjects.PowerUp;
import com.forofour.game.gameobjects.Team;
import com.forofour.game.gameobjects.Wall;
import com.forofour.game.actors.ButtonMaker;
import com.forofour.game.handlers.GameConstants;
import com.forofour.game.actors.TouchPadMaker;
import com.forofour.game.actors.Timer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The main game screen
 *
 */
public class GameWorld extends Stage{

    private World box2d; // Box2d world gravity
    private Ball ball;
    private Wall wallTop, wallBottom, wallLeft, wallRight;

    private Player player; // Controls are tied to this instance of player
    private List<Player> playerList;
    private Team teamA, teamB;

    private Timer globalTime;

    private Touchpad touchpad;
    private ImageButton boostButton, tossButton, powerSlot;
    private Label globalLabel, teamLabel;

    private PowerUp powerUp;

    private boolean requireReinitializing;

    public GameWorld(){
        super();

        float gameWidth = GameConstants.GAME_WIDTH;
        float gameHeight = GameConstants.GAME_HEIGHT;

        // Create the physics world
        box2d = new World(new Vector2(0f, 0f), true);
        box2d.setContactListener(new ListenerClass(this)); // Set the player-ball collision logic

        teamA = new Team(24);
        teamB = new Team(369);

        // Add players to the game
        playerList = new ArrayList<Player>();
        addPlayer();

        // Add ball to the game
        addBall();

        // Add powerUp to game
        powerUp = new PowerUp(GameConstants.GAME_WIDTH/3, GameConstants.GAME_HEIGHT/3, 5.0f, box2d);



        // Define the physics world boundaries
        float wallThickness = 1;
        wallTop = new Wall(0, 0, gameWidth, wallThickness, box2d);
        wallBottom = new Wall(0, gameHeight-wallThickness, gameWidth, wallThickness, box2d);
        wallLeft = new Wall(0, 0, wallThickness, gameHeight, box2d);
        wallRight = new Wall(gameWidth-wallThickness, 0, wallThickness, gameHeight, box2d);

        // Initialize game Timer
        globalTime = new Timer();
        globalTime.start();

        // Make & Add actors(HUD components) to the stage
        touchpad = TouchPadMaker.make(this);
        boostButton = ButtonMaker.getBoostButton(this);
        tossButton = ButtonMaker.getTossButton(this);
        globalLabel = TextLabelMaker.getTimeLabel(this);
        teamLabel = TextLabelMaker.getTimeLabel(this);
        powerSlot = PowerUpSlotMaker.getPowerSlot(this);

        addActor(TouchPadMaker.wrap(touchpad));
        addActor(ButtonMaker.wrap1(boostButton));
        addActor(ButtonMaker.wrap2(tossButton));
        addActor(TextLabelMaker.wrapGlobalTime(globalLabel));
        addActor(TextLabelMaker.wrapTeamScore(teamLabel));
        addActor(PowerUpSlotMaker.wrap1(powerSlot));

        requireReinitializing = false;
    }


    public World getBox2d(){
        return box2d;
    }

    public void update(float delta){
//        Gdx.app.log("GameWorld", "update");

        // Updates the Physics world movement/collision - player, ball
        box2d.step(delta, 8, 3);
        box2d.clearForces();

        // Updates the non-Physics states - hasBall, player orientation, etc.
        if(player != null)
            player.update(delta);
        if(ball != null)
            ball.update(delta);
        if(powerUp != null)
            powerUp.update(delta);

        // Adds scores
        if(teamA.getTeamList().contains(ball.getHoldingPlayer()))
            teamA.addScore(delta);
        if(teamB.getTeamList().contains(ball.getHoldingPlayer()))
            teamB.addScore(delta);

        //Stage
        if(globalLabel != null && globalTime != null) // Global time display
            globalLabel.setText(globalTime.getElapsed());
        if(teamLabel != null) { // Team time/score display
            if(teamA.getTeamList().contains(player))
                teamLabel.setText("A Score: " + teamA.getScore());
            else if(teamB.getTeamList().contains(player))
                teamLabel.setText("B Score: " + teamB.getScore());
            else
                teamLabel.setText("No team score");
        }

        if(player.hasPowerUp()) {
            PowerUpSlotMaker.setPowerUpStyle1();
        } else {
            PowerUpSlotMaker.setEmptySlotStyle();
        }

        if(player != null) { // Player controls
            if (player.getReverseDirectionTime() > 0) {
                player.knobMove(-getTouchpad().getKnobPercentX(), getTouchpad().getKnobPercentY());
            }
            if (player.getMoveVerySlowlyTime() > 0) {
                player.knobMove(getTouchpad().getKnobPercentX()/4, -getTouchpad().getKnobPercentY()/4);
            }
            else {
                player.knobMove(getTouchpad().getKnobPercentX(), -getTouchpad().getKnobPercentY());
            }

        }

        getCamera().update();
        act(delta);
        draw();
        // end Stage

    }

    public void addPlayer() {
        if(playerList != null) {
            if(playerList.size() < GameConstants.MAX_PLAYERS) {
                Random r = new Random();
                player = new Player(r.nextInt((int) GameConstants.GAME_WIDTH), r.nextInt((int) GameConstants.GAME_HEIGHT), 2f, ball, box2d);
                if(playerList != null) {
                    playerList.add(player);
                    addPlayerToTeam(player);
                }
            }
            requireReinitializing = true;
        }
    }

    private void addPlayerToTeam(Player p) {
        if(teamA.getTeamList().size() > teamB.getTeamList().size()) {
            teamB.addPlayer(p);
        }
        else {
            teamA.addPlayer(p);
        }
    }

    public void addBall() {
        if(ball == null) {
            ball = new Ball(GameConstants.GAME_WIDTH / 2, GameConstants.GAME_HEIGHT / 2, 1f, box2d);
            requireReinitializing = true;
        }
    }

    public boolean reinitRequired() {
        if(requireReinitializing) {
            requireReinitializing = false;
            return true;
        }
        return false;
    }

    public Ball getBall(){
        return ball;
    }
    public Player getPlayer(){
        return player;
    }
    public List<Player> getPlayerList(){
        return playerList;
    }

    public Touchpad getTouchpad() {return touchpad;}
    public ImageButton getTossButton(){
        return tossButton;
    }
    public ImageButton getBoostButton(){
        return boostButton;
    }

    public Timer getGlobalTime(){
        return globalTime;
    }

    public Team getTeamA() {
        return teamA;
    }
    public Team getTeamB() {
        return teamB;
    }

    public PowerUp getPowerUp() {
        return powerUp;
    }

    public void rotatePlayer(){
        if(playerList!= null) {
            player = playerList.get((playerList.indexOf(player)+1) % playerList.size());
            requireReinitializing = true;
        }
    }
}

/*
Listener to detect collision within Box2d world, and to determine the events that follow
 */
class ListenerClass implements ContactListener{

    private GameWorld world;

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
        if(world.getBall() != null && world.getPlayer() != null) {
            if(!world.getBall().isHeld()) {
                Body a = contact.getFixtureA().getBody();
                Body b = contact.getFixtureB().getBody();

                if(a.getUserData() instanceof Ball) {
                    ((Ball) a.getUserData()).setHoldingPlayer((Player) b.getUserData());
                }
                if(b.getUserData() instanceof Ball) {
                    ((Ball) b.getUserData()).setHoldingPlayer((Player) a.getUserData());
                }

            } else {
                Body a = contact.getFixtureA().getBody();
                Body b = contact.getFixtureB().getBody();

                // TODO: Player collision should be between opposing team members only
                if (a.getUserData() instanceof Player && b.getUserData() instanceof Player) {
                    if(world.getBall().getHoldingPlayer().equals(a.getUserData()) ||
                            world.getBall().getHoldingPlayer().equals(b.getUserData())) {
                        System.out.println("Collision");
                        world.getBall().triggerCollision();
                    }
                }
            }
        }
        if(world.getPlayer() != null && world.getPowerUp() != null){
            //TODO: check if player is in contact with powerup, if so, make powerup vanish and add power up to power up slot below
            Body a = contact.getFixtureA().getBody();
            Body b = contact.getFixtureB().getBody();

            if(a.getUserData() instanceof Player && b.getUserData() instanceof PowerUp){
                // Temporary solution: put power up out of mapview
                ((PowerUp) b.getUserData()).setDisappear();
                ((Player) a.getUserData()).acquirePowerUp(); // TODO: Refactor to recognize Type of PowerUp
            }
            else if(b.getUserData() instanceof Player && a.getUserData() instanceof PowerUp){
                // Temporary solution: put power up out of mapview
                ((PowerUp) a.getUserData()).setDisappear();
                ((Player) b.getUserData()).acquirePowerUp();
            }
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}