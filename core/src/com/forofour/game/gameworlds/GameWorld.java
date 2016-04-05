/*This defines the main logic used in the game*/

package com.forofour.game.gameworlds;

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
import com.badlogic.gdx.utils.viewport.ExtendViewport;
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

public class GameWorld{

    private World box2d; // Box2d world gravity

    private Ball ball;

    private Player player; // Controls are tied to this instance of player
    private List<Player> playerList;
    private Team teamA, teamB;

    private PowerUp powerUp;
    private Wall wallTop, wallBottom, wallLeft, wallRight;

    private Stage gameStage;
    private Touchpad touchpad;
    private ImageButton boostButton, tossButton, powerSlot;
    private Label globalLabel, teamLabel;

    private Timer globalTime;
    private boolean requireReinitializing;

    public GameWorld(){

        //get screen size parameters
        float gameWidth = GameConstants.GAME_WIDTH;
        float gameHeight = GameConstants.GAME_HEIGHT;

        //create the physics world
        box2d = new World(new Vector2(0f, 0f), true);
        box2d.setContactListener(new ListenerClass(this)); // Set the player-ball collision logic

        //add ball to the game
        addBall();

        //create two teams with different team id
        teamA = new Team(24);
        teamB = new Team(369);

        //add players to the game
        playerList = new ArrayList<Player>();
        addPlayer();

        //add powerUp to game
        powerUp = new PowerUp(GameConstants.GAME_WIDTH/3, GameConstants.GAME_HEIGHT/3, 5.0f, box2d);

        //define the physics world boundaries
        float wallThickness = 1;
        wallTop = new Wall(0, 0, gameWidth, wallThickness, box2d);
        wallBottom = new Wall(0, gameHeight-wallThickness, gameWidth, wallThickness, box2d);
        wallLeft = new Wall(0, 0, wallThickness, gameHeight, box2d);
        wallRight = new Wall(gameWidth-wallThickness, 0, wallThickness, gameHeight, box2d);

        //initialize game Timer
        globalTime = new Timer();
        globalTime.start();

        //make & add actors(HUD components) to the stage
/*        touchpad = TouchPadMaker.make(this);
        boostButton = ButtonMaker.getBoostButton(this);
        tossButton = ButtonMaker.getTossButton(this);
        globalLabel = TextLabelMaker.getTimeLabel(this);
        teamLabel = TextLabelMaker.getTimeLabel(this);
        powerSlot = PowerUpSlotMaker.getPowerSlot(this);*/

        gameStage = new Stage();

        gameStage.addActor(TouchPadMaker.wrap(touchpad));
        gameStage.addActor(ButtonMaker.wrap1(boostButton));
        gameStage.addActor(ButtonMaker.wrap2(tossButton));
        gameStage.addActor(TextLabelMaker.wrapGlobalTime(globalLabel));
        gameStage.addActor(TextLabelMaker.wrapTeamScore(teamLabel));
        gameStage.addActor(PowerUpSlotMaker.wrap1(powerSlot));

    }

    public void update(float delta){

        //updates the Physics world movement/collision - player, ball
        box2d.step(delta, 8, 3);
        box2d.clearForces();

        //updates the non-Physics states - hasBall, player orientation, etc.
        if(player != null)
            player.update(delta);
        if(ball != null)
            ball.update(delta);
        if(powerUp != null)
            powerUp.update(delta);

        //add scores
        if(teamA.getTeamList().contains(ball.getHoldingPlayer()))
            teamA.addScore(delta);
        if(teamB.getTeamList().contains(ball.getHoldingPlayer()))
            teamB.addScore(delta);

        //add timer
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

        //add power up
        if(player.hasPowerUp()) {
            PowerUpSlotMaker.setPowerUpStyle1();
        } else {
            PowerUpSlotMaker.setEmptySlotStyle();
        }

        //define player movement
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

        gameStage.getCamera().update();
        gameStage.act(delta);
        gameStage.draw();
        // end Stage

    }

    //add player to the game
    public void addPlayer() {
        if(playerList != null) {
            if(playerList.size() < GameConstants.MAX_PLAYERS) {
                //add player to a random location
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

    //add player to a team
    private void addPlayerToTeam(Player p) {
        if(teamA.getTeamList().size() > teamB.getTeamList().size()) {
            teamB.addPlayer(p);
        }
        else {
            teamA.addPlayer(p);
        }
    }

    //add ball to the game
    public void addBall() {
        if(ball == null) {
            ball = new Ball(GameConstants.GAME_WIDTH / 2, GameConstants.GAME_HEIGHT / 2, 1f, box2d);
            requireReinitializing = true;
        }
    }

    //require reinitialization after new object is added
    public boolean reinitRequired() {
        if(requireReinitializing) {
            requireReinitializing = false;
            return true;
        }
        return false;
    }

    public World getBox2d(){
        return box2d;
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

    public Stage getGameStage(){
        return gameStage;
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

    //for single player mode debugging -- switch between different players
    public void switchPlayer(){
        if(playerList!= null) {
            player = playerList.get((playerList.indexOf(player)+1) % playerList.size());
            requireReinitializing = true;
        }
    }
}


//Listener to detect collision within Box2d world, and to determine the events that follow
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