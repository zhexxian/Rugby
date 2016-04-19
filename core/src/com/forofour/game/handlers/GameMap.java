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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Mapping of all game states/objects required for game play.
 * Includes states that signify server is ready, closed, paused
 */
public class GameMap {

    private GameServer server;
    private GameClient client;
    private boolean isHost;
    private String tag; // for debug purposes

    // TimeStamps that allows periodic sending of packets
    private float runTime; // Global runTime since game has started
    private float lastSentTime, lastMoveTime;
    private float lastPlayerCollisionTime;

    // LobbyScreen objects
    private Map<Integer,Boolean> playersConnected = new HashMap<Integer, Boolean>();
    private boolean lobbyFilled = false; // Server triggered
    private int numberOfBabyFaces;

    // In-Game Object/States
    private World box2d; // Physics world that exists in every gameInstance(Resync'd by the the server periodically)
    private Wall wallTop, wallBottom, wallLeft, wallRight; // Borders
    private Player player;  // Reference to specific PlayerObject that the Client will control
    private Ball ball; // Reference to the MilkBottle

    // PowerUp
    private CopyOnWriteArrayList<PowerUp> powerUpList; // List of PowerUp packages that's has yet to be picked
    private CopyOnWriteArrayList<PowerUp> powerUpRemoveList; // REQUIRED list that is emptied OUTSIDE of the Box2d World loop
    private int powerUpCount;  // Count of total PowerUp packages spawned

    private HashMap<Integer, Player> playerHash; // Global listing of players
    private Team teamA, teamB; // Team listing of players

    private Timer globalTime; // Global time

    // States
    private boolean gameInitialized;
    public boolean shutdown, gameInitiated, gamePaused, gameEnd;
    public int gameDuration = 120; // SETTING FOR GAME DURATION, OVERWRITTEN by Server
    public float maxScore = 40;

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
        lastMoveTime = 0;
        lastPlayerCollisionTime = 0; // Used for periodic collision checks;
        this.isHost = isHost;

        // Instantiating of World(w/ gravity to Zero, and no simulation for inactive bodies)
        box2d = new World(new Vector2(0f, 0f), true);
        addWallBoundaries(); // adding of Wall boundaries

        // SERVER-SIDED Collision Detection
        if(isHost)
            box2d.setContactListener(new ListenerClass(this));

        // Instantiating of the lists/hash
        playerHash = new HashMap<Integer, Player>();
        powerUpList = new CopyOnWriteArrayList<PowerUp>();
        powerUpRemoveList = new CopyOnWriteArrayList<PowerUp>();
        powerUpCount = 0;

        //create two teams with different team id
        teamA = new Team(1);
        teamB = new Team(2);
        globalTime = new Timer(gameDuration);
    }

    // Triggered when players decide to PlayAgain, after reentering LobbyScreen
    public void restart() {
        gameInitialized = false;
        gamePaused = false;
        runTime = 0;
        lastSentTime = 0;
        lastMoveTime = 0;
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
        globalTime.setInfinityMode();
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


//            if(runTime - lastMoveTime > 0.03) {
//                client.sendMessageUDP(new Network.PacketPlayerUpdateFast(player.getId(), player.getBody().getLinearVelocity()));
//                lastMoveTime = runTime;
//            }

            // Client-sided logic
            if(!isHost) {
//                Gdx.app.log(tag, "Player" + player.getId() + " ballHeld " + ball.isHeld());
                if(runTime - lastSentTime > 0.5) { // Resync every 500ms
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
                if(server.getTutorialStates() != null) {
                    if(server.getTutorialStates().getShowBall())
                        server.getMap().ball.showBall();
                    else
                        server.getMap().ball.hideBall();
                }

                addTeamScores(delta); // Add score to ball holder

                // FREQUENT UPDATE TO CLIENTS
                for(Player p : playerHash.values()) {
                    serverSendMessage(new Network.PacketPlayerUpdateFast(p.getId(), p.getBody().getLinearVelocity())); // Every player's linear velocity
                }
//                Gdx.app.log(tag, "Server ballHeld " + ball.isHeld());
//                if(!ball.isHeld())
                serverSendMessage(new Network.PacketBallUpdateFast(ball.getBody().getLinearVelocity())); // Ball's linear velocity

                // PERIODIC UPDATE TO CLIENTS(100ms)
                if(runTime - lastSentTime > 0.1) {
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

    // called OUTSIDE of Box2d world loop, to remove dead bodies
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

    // Updates the connection list and count when a new player connects to the lobby
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

    // Getting for the LobbyScreen to display connectedBabys count
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
    // To update LobbyFilled state based on the connection count
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

    // Assignment of players from server
    // Control only if the rightful player
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

    // Assignment of ball by the Server
    public void addBall(Vector2 position, World box2d){
        ball = new Ball(position, box2d);

    }
    public Ball getBall(){
        return ball;
    }

    // Assignment of powerUp package by the Server
    public synchronized int addPowerUp(Vector2 position, int type) {
        PowerUp powerUp = new PowerUp(position, type, ++powerUpCount, box2d);
        powerUpList.add(powerUp);
        return powerUp.getId();
    }
    // Removal of PowerUp package by the server
    // Triggered when server detects powerUp is picked
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

    // Logic to increment relevant team's score based on duration of holding ball
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

    public boolean maximumScore(){
        if(teamA.getScore() >= maxScore || teamB.getScore() >= maxScore)
            return true;
        return false;
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

    // Periodic Updates from server to resync player locality and orientation
    public synchronized void updatePlayerState(int id, Vector2 position, float angle) {
        Player specificPlayer = playerHash.get(id);
        specificPlayer.getBody().setTransform(position, angle);
    }

    // Quick updates from server to sync player movement velocity within each PhysicsWorld
    public synchronized void updatePlayerMovement(int id, Vector2 movement) {
        Player specificPlayer = playerHash.get(id);
        specificPlayer.getBody().setLinearVelocity(movement);
    }
    // Trigger update from server when player Toss/Drops the ball
    public synchronized void updateDropBall(){
        ball.loseHoldingPlayer();
    }
    // Trigger update from server when player Pick/Toss/Drops the ball
    public synchronized void updateHoldingPlayer(int id) {
        ball.setHoldingPlayer(playerHash.get(id));
    }

    // Periodic Updates from server to resync ball locality and orientation
    public synchronized void updateBallState(Vector2 position, Float angle) {
        ball.getBody().setTransform(position, angle);
    }
    // Quick updates from server to sync ball movement velocity within each PhysicsWorld
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

            // Safety to ensure reference is present
            if(map.getBall() != null && map.getPlayerHash() != null) {

                // BETWEEN PLAYER & BALL
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

                // Periodic check to ensure server does not overload with requests
                else if(runTime - lastPlayerCollisionTime > 0.1){
                    // BETWEEN PLAYER & PLAYER, iff either is Ball Holder
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

            // Safety to ensure reference is present
            if(map.getPlayer() != null && map.getPowerUpList() != null){

                // BETWEEN PLAYER & POWERUP
                if(a.getUserData() instanceof Player && b.getUserData() instanceof PowerUp){
                    //if so, powerup gets destroyed and add power up to power up slot below
                    Player player = (Player) a.getUserData();
                    PowerUp powerUp = (PowerUp) b.getUserData();
                    player.acquirePowerUp(powerUp.getType());
                    Gdx.app.log(tag, "Player" + player.getId() + " Type" + powerUp.getType());
                    serverSendMessage(new Network.PacketPickPowerUp(player.getId(), powerUp.getType(), powerUp.getId()));

                    removePowerUp(powerUp.getId()); // Remove body instance of the from the world
//                    AssetLoader.powerUpSound.play(GameConstants.SOUND_VOLUME);
                }
                else if(b.getUserData() instanceof Player && a.getUserData() instanceof PowerUp){
                    Player player = (Player) a.getUserData();
                    PowerUp powerUp = (PowerUp) b.getUserData();
                    player.acquirePowerUp(powerUp.getType());
                    Gdx.app.log(tag, "Player" + player.getId() + " Type" + powerUp.getType());
                    serverSendMessage(new Network.PacketPickPowerUp(player.getId(), powerUp.getType(), powerUp.getId()));
                    removePowerUp(powerUp.getId());
//                    AssetLoader.powerUpSound.play(GameConstants.SOUND_VOLUME);
                }
            }
        }

        @Override
        public void postSolve(Contact contact, ContactImpulse impulse) {

        }
    }
}
