package com.forofour.game.net;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.forofour.game.handlers.AssetLoader;
import com.forofour.game.handlers.GameConstants;
import com.forofour.game.handlers.GameMap;
import com.forofour.game.tutorialMode.TutorialStates;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Instance of Server by Host
 *  contains Overall Game States(GameMap + TutorialStates) and status of player's game initiations
 *  Also, the handling of packets received
 */
public class GameServer {

    private Server server;
    private GameMap map;
    private TutorialStates tutorialStates = null;

    private Random random;
    private boolean tutorialMode;
    public boolean playRestart, playEnd;

    private ArrayList<Integer> initiatedPlayers;
    private ArrayList<Integer> powerUpAssignment;

    public GameServer(boolean tutorialMode){
//        Log.set(Log.LEVEL_TRACE);
        this.tutorialMode = tutorialMode;
        random = new Random(System.currentTimeMillis());
        map = new GameMap(this);

        if(tutorialMode) {
            tutorialStates = new TutorialStates(map, this); // Sets the TutorialMode gameDuration and access to other states
        }

        initiatedPlayers = new ArrayList<Integer>();
        initiatedPlayers.add(1); // Host id is always 1 - assumes itself as initiated

        powerUpAssignment = new ArrayList<Integer>();
        assignSpawnTimingPowerUp(); // Generation of PowerUp spawn Timings, which will be assigned during gamePlay

        server = new Server();
        Network.register(server);

        // Server receives updates via Listener
        //  As a event, it trigger certain updates as packets are received
        server.addListener(new Listener() {
            public void received(Connection c, Object o) {

                // Logs id of connected players
                if (o instanceof Network.PacketPlayerJoinLeave) {
                    Network.PacketPlayerJoinLeave packet = (Network.PacketPlayerJoinLeave) o;
                    if (map.addConnection(packet.id)) {
                        server.sendToAllTCP(new Network.PacketDebugAnnouncement("New Player connected successfully"));
                        server.sendToAllTCP(new Network.PacketDebugAnnouncement("Number of player in lobby: " + map.getPlayersConnected().size()));
                    }
                    server.sendToAllTCP(new Network.PacketPlayerJoinLeave(-1, map.getPlayersConnected().size()));

                }
                // When lobby is ready to be started, clients can nudge the server
                else if(o instanceof Network.PacketNudge) {
                    AssetLoader.nudgeSound.play(GameConstants.SOUND_VOLUME);
                }
                // Indication from client that they have received assignment of all GamePlay objects(Player/Ball/PowerUps)
                 else if (o instanceof Network.PacketInitRound) {
                    Network.PacketInitRound packet = (Network.PacketInitRound) o;
                    Gdx.app.log("GameServer", " initiated player" + c.getID());
                    initiatedPlayers.add(c.getID());
                    // NOTE : WOULD HOST EVEN RECEIVE THIS? IF SO, HOW TO START TUTORIAL MODE?
                    if (initiatedPlayers.containsAll(map.getPlayersConnected().keySet()))
                        map.gameInitiated = true;
                    Gdx.app.log("GameServer", "initPlayers - " + initiatedPlayers);
                    Gdx.app.log("GameServer", "playersConnected - " + map.getPlayersConnected().keySet());

                }
                // DEBUG PURPOSE : Client triggered gamePaused
                else if (o instanceof Network.PacketGamePause) {
                    Network.PacketGamePause packet = (Network.PacketGamePause) o;
                    Gdx.app.log("GameServer", "PauseButton Received from Client");
                    map.gamePaused = !map.gamePaused;
                    if (map.gamePaused)
                        map.getGlobalTime().pause();
                    else
                        map.getGlobalTime().start();
                    server.sendToAllTCP(new Network.PacketGamePause(map.gamePaused));
                    Gdx.app.log("GameServer", "Pause value sent " + map.gamePaused);
                }
                // DEBUG PURPOSE : Client(Host only) triggered gameEnd
                else if (o instanceof Network.PacketGameEnd) {
                    Network.PacketGameEnd packet = (Network.PacketGameEnd) o;
                    Gdx.app.log("GameServer", "Game End Received from client");
                    boolean playAgain = packet.playAgain;
                    Gdx.app.log("GameServer", "Player" + c.getID() + " PlayAgain" + playAgain);
                    if (map.gameEnd) { // End State of game
                        if (playAgain) {
                            // Should server decide to reenter lobby, clients will be allowed to as well
                            if (c.getID() == 1) {
                                Gdx.app.log("GameServer", "Server initiated playRestart");
                                sendMessage(new Network.PacketReinitLobby(true));
                                reinitLobby();
                            }
                        } else {
                            // Should server decide to return to main menu, clients will be informed
                            if (c.getID() == 1) {
                                Gdx.app.log("GameServer", "Server initiated endGame");
                                sendMessage(new Network.PacketReinitLobby(false));
                                reinitMenu();
                            }
                        }
                    }
                }
                // Periodic update of location of specific player
                else if (o instanceof Network.PacketPlayerState) {
                    Network.PacketPlayerState packet = (Network.PacketPlayerState) o;
//                    Gdx.app.log("GameServer", "State Updates for player" + "-" + c.getID() + "-" + packet.id);
                    map.updatePlayerState(packet.id, packet.position, packet.angle);
                server.sendToAllTCP(new Network.PacketPlayerState(packet.id, packet.position, packet.angle));

                }
                // Fast update of player position
                else if (o instanceof Network.PacketPlayerUpdateFast) {
                    Network.PacketPlayerUpdateFast packet = (Network.PacketPlayerUpdateFast) o;
//                    Gdx.app.log("GameServer", "Movement Updates for player" + "-" + packet.id + "-" + packet.movement);
                    map.updatePlayerMovement(packet.id, packet.movement);
                    server.sendToAllTCP(new Network.PacketPlayerUpdateFast(packet.id, packet.movement));
                }
                // Trigger update : Player toss of ball
                else if (o instanceof Network.PacketDropBall) {
                    Network.PacketDropBall packet = (Network.PacketDropBall) o;
                    Gdx.app.log("GameServer", "bef Holder " + map.getBall().getHoldingPlayerId());
                    // Check that dropper is indeed holder
                    if (map.getBall().getHoldingPlayerId() == ((Network.PacketDropBall) o).id) {
                        map.updateDropBall();
                        server.sendToAllTCP(new Network.PacketDropBall(packet.id)); // Announcement to all clients
                    }
                    Gdx.app.log("GameServer", "aft Holder " + map.getBall().getHoldingPlayerId());
                    Gdx.app.log("GameServer", "player" + packet.id + " called dropball");
                }

                // DEBUG PURPOSES(SV COMMANDS) : Adding of PowerUps into game
                else if (o instanceof Network.PacketAddPowerUp) {
                    Network.PacketAddPowerUp packet = (Network.PacketAddPowerUp) o;
                    Gdx.app.log("GameServer", "PacketAddPowerUp, Type:" + packet.type);
                    assignPowerUp(packet.type);
                }

                // DEBUG PURPOSES(SV COMMANDS) : Acquisition of PowerUp by player
                else if (o instanceof Network.PacketPickPowerUp) {
                    Network.PacketPickPowerUp packet = (Network.PacketPickPowerUp) o;
                    map.getPlayerHash().get(c.getID()).acquirePowerUp(packet.type);
                Gdx.app.log("GameServer", "PacketPickPowerUp, ID:" + c.getID() + " Type:" + packet.type + " ItemID:" + packet.powerUpId);
                server.sendToAllTCP(new Network.PacketPickPowerUp(c.getID(), packet.type, -1)); // -1 as arbitary powerUpId
                }

                // Trigger update : Player's USE of PowerUp
                else if (o instanceof Network.PacketUsePowerUp) {
                    int generatedChoice = random.nextInt(4);
                    map.getPlayerHash().get(c.getID()).usePowerUp(generatedChoice);
                    server.sendToAllTCP(new Network.PacketUsePowerUp(c.getID(), generatedChoice));
                    Gdx.app.log("GameServer", "PacketUsePowerUp, ID:" + c.getID());
                }
            }

            public void connected(Connection c) {
                Gdx.app.log("GameServer", "Player connected");

            }

            public void disconnected(Connection c) {
                Gdx.app.log("GameServer", "Player disconnected");
                map.dropConnection(c.getID());

                // Basically sending the same thing
                server.sendToAllTCP(new Network.PacketDebugAnnouncement("Player " + c.getID() + " has disconnected from the game"));
                server.sendToAllTCP(new Network.PacketDebugAnnouncement("Number of player in lobby: " + map.getPlayersConnected().size()));
                server.sendToAllTCP(new Network.PacketPlayerJoinLeave(-1, map.getPlayersConnected().size()));
            }
        });

        try{
            server.bind(Network.port, Network.portUDP);
            Gdx.app.log("Server", "Binded to " + Network.port + " " + Network.portUDP);
        } catch (IOException ex){
            ex.printStackTrace();
        }

        server.start(); // Starts the server after all listeners have been defined
        Gdx.app.log("Server", "Started");

    }

    public void update(float delta){
        map.update(delta);

        // Assignment of power ups based on the timeList generated.
        //  upon passing of time, powerUp is assigned
        if(!powerUpAssignment.isEmpty() && map.getGlobalTime().getElapsedSeconds()>0) {
            if (powerUpAssignment.get(0) < map.getGlobalTime().getElapsedSeconds()) {
                Gdx.app.log("GameServer-PowerUpAssignmet", powerUpAssignment.get(0) + " " + map.getGlobalTime().getElapsedSeconds());
                assignPowerUp();
                powerUpAssignment.remove(0);
            }
        }
    }

    public void shutdown(boolean restart) {
        if(restart)
            sendMessage(new Network.PacketShutdown());
        server.close();
        server.stop();
    }

	public void sendMessage(Object message) {
		server.sendToAllTCP(message);
	}

    // Player assignment during initiation of game
    public void assignPlayers() {
        if(map.getPlayersConnected() != null) {
            int playerNumber = 0;
            for(Integer id : map.getPlayersConnected().keySet()) {
                Gdx.app.log("GameServer", "Assigning player" + id);

                // Location based on PreDefined constants
                int x = GameConstants.PLAYER_POSITION[playerNumber][0];
                int y = GameConstants.PLAYER_POSITION[playerNumber++][1];

                Vector2 pos = new Vector2(x, y);
                if(playerNumber%2 != 0) { // TeamAssignment dependent on playerNumber
                    map.addPlayer(false, id, 1, pos, map.getBox2d());  // Control, PlayerId, TeamNumber, Location
                    sendMessage(new Network.PacketAddPlayer(id, 1, pos));
                }
                else {
                    map.addPlayer(false, id, 2, pos, map.getBox2d());
                    sendMessage(new Network.PacketAddPlayer(id, 2, pos));
                }
            }
        }
        Gdx.app.log("GameServer", "Assigned playerHash: " + map.getPlayerHash().toString());
    }

    // Ball assignment during initiation of game
    public void assignBall() {
        Gdx.app.log("GameServer", "Assigning ball");

        // Positioned to the centre of the GameWorld
        Vector2 ballPosition = new Vector2(GameConstants.GAME_WIDTH/2, GameConstants.GAME_HEIGHT/2);
        map.addBall(ballPosition, map.getBox2d());
        sendMessage(new Network.PacketAddBall(ballPosition));
    }

    // PowerUp assignment during initiation of game, helper for assignSpawnTimingPowerUp method
    public void assignPowerUp(){
        int type = 1 + random.nextInt(3);
        assignPowerUp(type);
    }

    public void assignPowerUp(int type) {
        // 3 types - Water(Slow), Cloak(Invisibility) , Confusion(DisorientedControls)
        int distanceFromTopBottom = 7;
        int distanceFromLeftRight = 5;
        Vector2 position = new Vector2(
                distanceFromLeftRight + random.nextInt((int) GameConstants.GAME_WIDTH - 2*distanceFromLeftRight),
                distanceFromTopBottom + random.nextInt((int) GameConstants.GAME_HEIGHT - 2*distanceFromTopBottom));
        int powerUpId = map.addPowerUp(position, type);
        Gdx.app.log("GameServer", "Assigning PowerUp of type"+powerUpId);
        server.sendToAllTCP(new Network.PacketAddPowerUp(position, type));
    }

    // Generates powerUps and the time it should be spawned.
    // Objects are stored in a list
    public void assignSpawnTimingPowerUp() {
        int initialTime = 0;
        while(initialTime < map.gameDuration) {
            initialTime += 15 + random.nextInt(5);
            if(initialTime < map.gameDuration)
                powerUpAssignment.add(initialTime);
        }
    }

    // called when Host decides to "playAgain" after gameEnd
    public void reinitLobby() {
        map.restart();
        initiatedPlayers.clear();
        playRestart = true;
    }

    // called when Host decides not to "playAgain" after gameEnd
    public void reinitMenu(){
        map.restart();
        initiatedPlayers.clear();
        playEnd = true;
    }

    public GameMap getMap() {
        return map;
    }

    public void dispose() {
        map.dispose();
    }

    public TutorialStates getTutorialStates(){
        return tutorialStates;
    }
}
