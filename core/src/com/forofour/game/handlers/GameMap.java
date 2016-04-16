package com.forofour.game.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.forofour.game.actors.Timer;
import com.forofour.game.gameobjects.Ball;
import com.forofour.game.gameobjects.Player;
import com.forofour.game.gameobjects.PowerUp;
import com.forofour.game.gameobjects.Team;
import com.forofour.game.gameobjects.Wall;
import com.forofour.game.net.GameClient;
import com.forofour.game.net.GameServer;
import com.forofour.game.net.Network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

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
    private float lastPlayerCollisionTime;

    private Map<Integer,Boolean> playersConnected = new HashMap<Integer, Boolean>();
    private boolean lobbyFilled = false;
    private int numberOfBabyFaces;

    private World box2d;
    private Wall wallTop, wallBottom, wallLeft, wallRight;
    private Player player;
    private Ball ball;
    private CopyOnWriteArrayList<PowerUp> powerUpList;
    private CopyOnWriteArrayList<PowerUp> powerUpRemoveList;
    private int powerUpCount;

    private HashMap<Integer, Player> playerHash;
    private Team teamA, teamB;

    private Timer globalTime;
    private boolean gameInitialized;
    public boolean shutdown, gameInitiated, gamePaused, gameEnd;
    public int gameDuration = 120;

    public GameMap(GameServer server){
        this(true);
        this.server = server;
        this.tag = "GameMap" + "(server)";
    }
    public GameMap(GameClient client){
        this(false);
        this.client = client;
        this.tag = "GameMap" + "(client)";
    }

    private GameMap(boolean isHost) {
        gameInitialized = false;
        shutdown = false;
        gamePaused = false;
        gameEnd =false;
        runTime = 0;
        lastSentTime = 0; // Used for periodic updates to/from server.
        lastPlayerCollisionTime = 0; // Used for periodic collision checks;
        this.isHost = isHost;

        box2d = new World(new Vector2(0f, 0f), true);
        if(isHost)
            box2d.setContactListener(new ListenerClass(this));
        addWallBoundaries();

        playerHash = new HashMap<Integer, Player>();
        powerUpList = new CopyOnWriteArrayList<PowerUp>();
        powerUpRemoveList = new CopyOnWriteArrayList<PowerUp>();
        powerUpCount = 0;

        //create two teams with different team id
        teamA = new Team(1);
        teamB = new Team(2);
        globalTime = new Timer(gameDuration);
    }

    public void restart() {
        gameInitialized = false;
        gamePaused = false;
        runTime = 0;
        lastSentTime = 0;
        lastPlayerCollisionTime = 0;

        box2d.dispose();
        box2d = new World(new Vector2(0f, 0f), true);
        if(isHost)
            box2d.setContactListener(new ListenerClass(this));
        addWallBoundaries(); //define the physics world boundaries

        playerHash.clear();
        powerUpList.clear();
        powerUpRemoveList.clear();
        powerUpCount = 0;
        teamA.clear();
        teamB.clear();
        globalTime.reset();
    }

    public void setGameDuration(int duration){
        globalTime.setGameDuration(duration);
    }

    public void addWallBoundaries() {
        //define the physics world boundaries
        wallTop = new Wall(0, 0, GameConstants.GAME_WIDTH, 5f, box2d);
        wallBottom = new Wall(0, GameConstants.GAME_HEIGHT, GameConstants.GAME_WIDTH, 5f, box2d);
        wallLeft = new Wall(0, 0, 3.5f, GameConstants.GAME_HEIGHT, box2d);
        wallRight = new Wall(GameConstants.GAME_WIDTH, 0, 3.5f, GameConstants.GAME_HEIGHT, box2d);
    }

    // Client updates itself and sends updates to server
    public synchronized void update(float delta){
        runTime += delta;
        //updates the Physics world movement/collision - player, ball
        box2d.step(delta, 8, 3);
        box2d.clearForces();
        sweepDeadBodies();
        globalTime.update();

        if(gameInitialized) {
            // Common logic
            for(Player p : playerHash.values()) {
                p.update(delta);
            }
            ball.update(delta);

            // Client-sided logic
            if(!isHost) {
//                Gdx.app.log(tag, "Player" + player.getId() + " ballHeld " + ball.isHeld());
                if(runTime - lastSentTime > 0.5) { // Resync every 100ms
                    // Client will receive updated location on PlayerLocations after sending his own
                    clientSendMessage(new Network.PacketPlayerState(player.getId(), player.getPosition(), player.getAngle()));
//                    Gdx.app.log(tag, "Updating player" + player.getId() + " position");
                    lastSentTime = runTime;
                }
            }
        }
        else{
            if(player != null && ball != null && !isHost) {
                gameInitialized = true;
                globalTime.start();
            }
        }

        // Server-sided logic
        if(isHost) {
            if(!gameInitialized) {
                server.assignPlayers();
                player = playerHash.get(1);
                server.assignBall();
                gameInitialized = true;
                globalTime.start();
            }
            else {
                addTeamScores(delta);

                for(Player p : playerHash.values()) {
                    serverSendMessage(new Network.PacketPlayerUpdateFast(p.getId(), p.getBody().getLinearVelocity()));
                }
//                Gdx.app.log(tag, "Server ballHeld " + ball.isHeld());
//                if(!ball.isHeld())
                    serverSendMessage(new Network.PacketBallUpdateFast(ball.getBody().getLinearVelocity()));

                if(runTime - lastSentTime > 0.1) { // Resync every 100ms
//                    if(!ball.isHeld())
                        serverSendMessage(new Network.PacketBallState(ball.getBody().getPosition(), 0, ball.isHeld())); // Always send ball location
                    if(!playersConnected.containsKey(false)) {
                        serverSendMessage(new Network.PacketTeamScores(teamA.getScore(), teamB.getScore())); // to Update client games
                    }
                    lastSentTime = runTime;
                }
            }
        }
    }

    public void sweepDeadBodies() {
        if(!powerUpRemoveList.isEmpty()) {
            for(PowerUp powerUp : powerUpRemoveList) {
                powerUpList.remove(powerUp);
                powerUp.getBody().setUserData(null);
                powerUp.destroy();
            }
            powerUpRemoveList.clear();
        }
    }

    public boolean addConnection(int id){
        if(!lobbyFilled) {
            Gdx.app.log(tag, "Player " + id +  " joined the game");
            playersConnected.put(id, false);
            updatePlayersConnected();
            return true; // Add connection successful
        }
        return false; // Add connection failed
    }
    public void dropConnection(int id) {
        playersConnected.remove(id);
        updatePlayersConnected();
    }

    public void setNumberOfBabyFaces(int numberOfBabyFaces){
        this.numberOfBabyFaces = numberOfBabyFaces;
    }
    public int getNumberOfBabyFaces(){
        int temp = numberOfBabyFaces;
        return temp;
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

        for(Player p: playerHash.values()) {
            if(teamA.getTeamList().contains(p))
                p.setTeam(teamA, teamB);
            else
                p.setTeam(teamB, teamA);
        }

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

    public synchronized int addPowerUp(Vector2 position, int type) {
        PowerUp powerUp = new PowerUp(position, type, ++powerUpCount, box2d);
        powerUpList.add(powerUp);
        return powerUp.getId();
    }
    public synchronized void removePowerUp(int id){
        for(PowerUp powerUp : powerUpList) {
            if(powerUp.getId() == id) {
                powerUpRemoveList.add(powerUp);
            }
        }
    }
    public CopyOnWriteArrayList<PowerUp> getPowerUpList() {
        return powerUpList;
    }

    public Team getTeamA() {
        return teamA;
    }
    public Team getTeamB() {
        return teamB;
    }
    public Team getOpposingTeam(int teamNo) {
        if(teamNo == 1) {
            Gdx.app.log(tag, teamB.toString());
            return teamB;
        }
        else {
            Gdx.app.log(tag, teamA.toString());
            return teamA;
        }
    }

    public void addTeamScores(float delta) {
        //add scores
        if(teamA.getTeamList().contains(ball.getHoldingPlayer()))
            teamA.addScore(delta);
        if(teamB.getTeamList().contains(ball.getHoldingPlayer()))
            teamB.addScore(delta);
    }
    public Timer getGlobalTime() {
        return globalTime;
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
    public synchronized void updateDropBall(){
        ball.loseHoldingPlayer();
    }
    public synchronized void updateHoldingPlayer(int id) {
        ball.setHoldingPlayer(playerHash.get(id));
    }

    public synchronized void updateBallState(Vector2 position, Float angle) {
        ball.getBody().setTransform(position, angle);
    }
    public synchronized void updateBallMovement(Vector2 movement) {
        ball.getBody().setLinearVelocity(movement);
    }

    public float getRunTime() {
        return runTime;
    }

    public void dispose() {
        box2d.dispose();
    }

    // Server-sided Collision
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
            Body a = contact.getFixtureA().getBody();
            Body b = contact.getFixtureB().getBody();
            Gdx.app.log(tag, "Collision detected");

            if(map.getBall() != null && map.getPlayerHash() != null) {
                if(!map.getBall().isHeld()) {
                    Gdx.app.log("Collision", "Ball is not held");

                    if(a.getUserData() instanceof Ball && b.getUserData() instanceof Player) {
                        Gdx.app.log("Collision", "Collided with player" + ((Player) b.getUserData()).getId());
//                        ((Ball) a.getUserData()).setHoldingPlayer((Player) b.getUserData());
                        updateHoldingPlayer(((Player) b.getUserData()).getId()); // Testing
//                        serverSendMessage(new Network.PacketBallUpdateFast(ball.getBody().getLinearVelocity(), ((Player) b.getUserData()).getId()));
                        serverSendMessage(new Network.PacketSetHoldingPlayer(((Player) b.getUserData()).getId()));
                    }
                    if(b.getUserData() instanceof Ball && a.getUserData() instanceof Player) {
                        Gdx.app.log("Collision", "Collided with player" + ((Player) a.getUserData()).getId());
//                        ((Ball) b.getUserData()).setHoldingPlayer((Player) a.getUserData());
                        updateHoldingPlayer(((Player) a.getUserData()).getId());
                        serverSendMessage(new Network.PacketSetHoldingPlayer(((Player) a.getUserData()).getId()));
                    }

                }
                else if(runTime - lastPlayerCollisionTime > 0.1){
                    // TODO: Player collision should be between opposing team members only
                    if (a.getUserData() instanceof Player && b.getUserData() instanceof Player) {
                        Player playerA = (Player) a.getUserData();
                        Player playerB = (Player) b.getUserData();
                        if(map.getBall().getHoldingPlayer().equals(playerA) ||
                                map.getBall().getHoldingPlayer().equals(playerB)) {
                            Gdx.app.log("Collision", "Between ball holder and opposing player");
                            if(playerA.getTeamId() != playerB.getTeamId()) {
                                map.getBall().triggerCollision();
                                serverSendMessage(new Network.PacketDropBall());
                            }
                        }
                    }
//                    lastPlayerCollisionTime = runTime;
                }
            }
            if(map.getPlayer() != null && map.getPowerUpList() != null){
                //Checks if player is in contact with powerup, if so, make powerup vanish and add power up to power up slot below

                if(a.getUserData() instanceof Player && b.getUserData() instanceof PowerUp){
                    Player player = (Player) a.getUserData();
                    PowerUp powerUp = (PowerUp) b.getUserData();
                    player.acquirePowerUp(powerUp.getType()); // TODO: Refactor to recognize Type of PowerUp
                    Gdx.app.log(tag, "Player" + player.getId() + " Type" + powerUp.getType());
                    serverSendMessage(new Network.PacketPickPowerUp(player.getId(), powerUp.getType(), powerUp.getId()));

                    removePowerUp(powerUp.getId()); // Remove body instance of the from the world
                    AssetLoader.powerUpMusic.play();
                }
                else if(b.getUserData() instanceof Player && a.getUserData() instanceof PowerUp){
                    Player player = (Player) a.getUserData();
                    PowerUp powerUp = (PowerUp) b.getUserData();
                    player.acquirePowerUp(powerUp.getType());
                    Gdx.app.log(tag, "Player" + player.getId() + " Type" + powerUp.getType());
                    serverSendMessage(new Network.PacketPickPowerUp(player.getId(), powerUp.getType(), powerUp.getId()));
                    removePowerUp(powerUp.getId());
                    AssetLoader.powerUpMusic.play();
                }
            }
        }

        @Override
        public void postSolve(Contact contact, ContactImpulse impulse) {

        }
    }
}
