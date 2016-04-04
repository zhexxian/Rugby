package com.forofour.game.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.forofour.game.gameobjects.Ball;
import com.forofour.game.gameobjects.Player;
import com.forofour.game.gameobjects.Team;
import com.forofour.game.gameobjects.Wall;
import com.forofour.game.net.GameClient;
import com.forofour.game.net.GameServer;
import com.forofour.game.net.Network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by seanlim on 4/4/2016.
 */
public class GameMap {

    private GameServer server;
    private GameClient client;
    private boolean isHost;
    private String tag;
    private float runTime;
    private float lastSentTime;

    private Map<Integer,Boolean> playersConnected = new HashMap<Integer, Boolean>();
    private boolean lobbyFilled = false;
    private int numberOfBabyFaces;

    private World box2d;
    private Wall wallTop, wallBottom, wallLeft, wallRight;
    private Player player;
    private Ball ball;

    private HashMap<Integer, Player> playerHash;
    private Team teamA, teamB;

    private boolean gameInitialized;

    public GameMap(GameServer server){
        this(true);
        this.server = server;
    }
    public GameMap(GameClient client){
        this(false);
        this.client = client;
    }

    private GameMap(boolean isHost) {
        gameInitialized = false;
        runTime = 0;
        lastSentTime = 0;
        this.isHost = isHost;
        if(isHost)
            this.tag = "GameMap" + "(server)";
        else
            this.tag = "GameMap" + "(client)";

        //get screen size parameters
        float gameWidth = GameConstants.GAME_WIDTH;
        float gameHeight = GameConstants.GAME_HEIGHT;

        box2d = new World(new Vector2(0f, 0f), true);
        if(isHost)
            box2d.setContactListener(new ListenerClass(this));

        //define the physics world boundaries
        float wallThickness = 1;
        wallTop = new Wall(0, 0, gameWidth, wallThickness, box2d);
        wallBottom = new Wall(0, gameHeight-wallThickness, gameWidth, wallThickness, box2d);
        wallLeft = new Wall(0, 0, wallThickness, gameHeight, box2d);
        wallRight = new Wall(gameWidth-wallThickness, 0, wallThickness, gameHeight, box2d);

        playerHash = new HashMap<Integer, Player>();

        //create two teams with different team id
        teamA = new Team(1);
        teamB = new Team(2);
    }

    // Client updates itself and sends updates to server
    public synchronized void update(float delta){
        runTime += delta;
        //updates the Physics world movement/collision - player, ball
        box2d.step(delta, 8, 3);
        box2d.clearForces();

        if(player != null) {
            player.update(delta);
        }

        // Server-sided logic
        if(isHost) {
            if(!gameInitialized) {
                server.assignPlayers();
                server.assignBall();
                gameInitialized = true;
            }
            serverSendMessage(new Network.PacketBallUpdateMovement(ball.getBody().getLinearVelocity()));
            if(lastSentTime < runTime - 0.1) { // Resync every 100ms
                serverSendMessage(new Network.PacketBallState(ball.getBody().getPosition(), 0));
            }
//            Gdx.app.log(tag, "Within Server-side logic");
        }

        // Client-sided logic
        else {
            Gdx.app.log(tag, runTime + "/" + lastSentTime);
            if(lastSentTime < runTime - 0.1) { // Resync every 100ms
                if (player != null) {
                    clientSendMessageUDP(new Network.PacketPlayerState(player.getId(), player.getPosition(), player.getAngle()));
                    Gdx.app.log(tag, "Updating player" + player.getId() + " position");
                }

                lastSentTime = runTime;
            }
        }

    }

    public boolean addConnection(int id){
        if(!lobbyFilled) {
            Gdx.app.log(tag, "Player " + id +  " joined the game");
            playersConnected.put(id, true);
            updatePlayersConnected();
            return true; // Add connection successful
        }
        return false; // Add connection failed
    }

    public void dropConnection(int id) {
        playersConnected.remove(id);
        updatePlayersConnected();
    }

    public Map<Integer, Boolean> getPlayersConnected() {
        Gdx.app.log(tag, "playersConnected List" + playersConnected.toString());
        return playersConnected;
    }

    public void updatePlayersConnected() {
        if(playersConnected.size() == 4) {
            lobbyFilled = true;
            Gdx.app.log(tag, "Lobby is ready. " + playersConnected.size() + " players connected.");
        }
        else {
            lobbyFilled = false;
            Gdx.app.log(tag, "Lobby is not ready. " + playersConnected.size() + " players connected.");
        }
    }
    public World getBox2d(){
        return box2d;
    }

    public void addPlayer(boolean control, int id, int teamNo, Vector2 position, World box2d) {
        Player newPlayer = new Player(id, position, box2d, client);
        playerHash.put(id, newPlayer);
        if(teamNo == 1)
            teamA.addPlayer(newPlayer);
        else
            teamB.addPlayer(newPlayer);

        if(control)
            player = newPlayer;

    }
    public Player getPlayer(){
        return player;
    }
    public HashMap<Integer, Player> getPlayerHash(){
        return playerHash;
    }

    public void addBall(Vector2 position, World box2d){
        ball = new Ball(position, box2d);

    }
    public Ball getBall(){
        return ball;
    }

    // TODO: Server commands from client, for debugging purpose
    public void svAddPlayer() {
    }

    public synchronized void clientSendMessageUDP(Object msg) {
        client.sendMessageUDP(msg);
    }

    public synchronized void clientSendMessage(Object msg){
        client.sendMessage(msg);
    }

    public synchronized void serverSendMessage(Object msg){
        server.sendMessage(msg);
    }

    public synchronized void updatePlayerState(int id, Vector2 position, float angle) {
        Player specificPlayer = playerHash.get(id);
        specificPlayer.getBody().setTransform(position, angle);
    }

    public synchronized void updatePlayerMovement(int id, Vector2 movement) {
        Player specificPlayer = playerHash.get(id);
        specificPlayer.getBody().setLinearVelocity(movement);
    }

    public void updateBallState(Vector2 position, Float angle) {
        ball.getBody().setTransform(position, angle);
    }

    public void updateBallMovement(Vector2 movement) {
        ball.getBody().setLinearVelocity(movement);
    }

    class ListenerClass implements ContactListener {

        private GameMap map;

        public ListenerClass(GameMap map){
            this.map = map;
        }

        @Override
        public void beginContact(Contact contact) {
        }

        @Override
        public void endContact(Contact contact) {
        }

        @Override
        public void preSolve(Contact contact, Manifold oldManifold) {
            Gdx.app.log(tag, "Collision detected");
            if(map.getBall() != null && map.getPlayer() != null) {
                if(!map.getBall().isHeld()) {
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
                        if(map.getBall().getHoldingPlayer().equals(a.getUserData()) ||
                                map.getBall().getHoldingPlayer().equals(b.getUserData())) {
                            System.out.println("Collision");
                            map.getBall().triggerCollision();
                        }
                    }
                }
            }
/*            if(map.getPlayer() != null && map.getPowerUp() != null){
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
            }*/
        }

        @Override
        public void postSolve(Contact contact, ContactImpulse impulse) {

        }
    }
}
