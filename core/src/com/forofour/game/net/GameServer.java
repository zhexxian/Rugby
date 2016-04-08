package com.forofour.game.net;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.forofour.game.handlers.GameConstants;
import com.forofour.game.handlers.GameMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by seanlim on 1/4/2016.
 */
public class GameServer {

    private Server server;
    private GameMap map;
    private Random random;
    private ArrayList<Integer> initiatedPlayers;

    public GameServer(){
        random = new Random(System.currentTimeMillis());
        map = new GameMap(this);
        initiatedPlayers = new ArrayList<Integer>();
        initiatedPlayers.add(1);
        server = new Server();
        Network.register(server);

        // Server receives updates via Listener
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

                else if(o instanceof Network.PacketInitRound) {
                    Network.PacketInitRound packet = (Network.PacketInitRound) o;
                    Gdx.app.log("GameServer", " initiated p" + c.getID());
                    initiatedPlayers.add(c.getID());
                    if(initiatedPlayers.containsAll(map.getPlayersConnected().keySet()))
                        map.gameInitiated = true;
                    Gdx.app.log("GameServer", "initPlayers - " + initiatedPlayers);
                    Gdx.app.log("GameServer", "playersConnected - " + map.getPlayersConnected().keySet());
                }

                else if(o instanceof Network.PacketGamePause) {
                    Network.PacketGamePause packet = (Network.PacketGamePause) o;
                    Gdx.app.log("GameServer", "PauseButton Received from Client");
                    map.gamePaused = !map.gamePaused;
                    server.sendToAllTCP(new Network.PacketGamePause(map.gamePaused));
                    Gdx.app.log("GameServer", "Pause value sent " + map.gamePaused);
                }

                // Updates location of specific player
                else if(o instanceof Network.PacketPlayerState) {
                    Network.PacketPlayerState packet = (Network.PacketPlayerState) o;
//                    Gdx.app.log("GameServer", "State Updates for player" + "-" + c.getID() + "-" + packet.id);
                    map.updatePlayerState(packet.id, packet.position, packet.angle);
                    server.sendToAllTCP(new Network.PacketPlayerState(packet.id, packet.position, packet.angle));
                }

                else if(o instanceof Network.PacketPlayerUpdateFast) {
                    Network.PacketPlayerUpdateFast packet = (Network.PacketPlayerUpdateFast) o;
//                    Gdx.app.log("GameServer", "Movement Updates for player" + "-" + packet.id + "-" + packet.movement);
                    map.updatePlayerMovement(packet.id, packet.movement);
                    server.sendToAllTCP(new Network.PacketPlayerUpdateFast(packet.id, packet.movement));
                }

                else if(o instanceof Network.PacketDropBall) {
                    Network.PacketDropBall packet = (Network.PacketDropBall) o;
                    Gdx.app.log("GameServer", "bef Holder " + map.getBall().getHoldingPlayerId());
                    // Check that dropper is indeed holder
                    if(map.getBall().getHoldingPlayerId() == ((Network.PacketDropBall) o).id) {
                        map.updateDropBall();
                        server.sendToAllTCP(new Network.PacketDropBall(packet.id));
                    }
//                    map.getPlayerHash().get(packet.id).dropBall();
//                    map.getBall().loseHoldingPlayer();
                    Gdx.app.log("GameServer", "aft Holder " + map.getBall().getHoldingPlayerId());
                    Gdx.app.log("GameServer", "player" + packet.id + " called dropball");
                }

                // DEBUG PURPOSES(SV COMMANDS) : Adding of PowerUps into game
                else if(o instanceof Network.PacketAddPowerUp) {
                    Network.PacketAddPowerUp packet = (Network.PacketAddPowerUp) o;
                    Vector2 position = new Vector2(10+random.nextInt(50),10+random.nextInt(50));
                    int powerUpId = map.addPowerUp(position, packet.type);
                    Gdx.app.log("GameServer", "PacketAddPowerUp, Type:" + packet.type + " ItemID:"+powerUpId);
                    server.sendToAllTCP(new Network.PacketAddPowerUp(position, packet.type));
                }

                // DEBUG PURPOSES(SV COMMANDS) : Acquisition of PowerUp by player
                else if(o instanceof Network.PacketPickPowerUp) {
                    Network.PacketPickPowerUp packet = (Network.PacketPickPowerUp) o;
                    map.getPlayerHash().get(c.getID()).acquirePowerUp(packet.type);
                    Gdx.app.log("GameServer", "PacketPickPowerUp, ID:" + c.getID() + " Type:" + packet.type + " ItemID:"+packet.powerUpId);
                    server.sendToAllTCP(new Network.PacketPickPowerUp(c.getID(), packet.type, -1)); // -1 as arbitary powerUpId
                }

                // Player's USE of PowerUp
                else if(o instanceof Network.PacketUsePowerUp) {
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
        } catch (IOException ex){
            ex.printStackTrace();
        }

        server.start();
        System.out.println("Server is running");

    }

    public void update(float delta){
        map.update(delta);
//        if(map.getGlobalTime().isDone()) {
//            map.gamePaused = true;
//            sendMessage(new Network.PacketGamePause(true));
//        }
    }

    public void shutdown() {
        server.close();
        server.stop();
    }

	public void sendMessage(Object message) {
		server.sendToAllTCP(message);
	}

    public void assignPlayers() {
        if(map.getPlayersConnected() != null) {
            for(Integer id : map.getPlayersConnected().keySet()) {
                Gdx.app.log("GameServer", "Assigning player" + id);
                int i = map.getPlayerHash().size()*10;
                Vector2 pos = new Vector2(10+i, 10+i);
                if(id%2 != 0) {
                    map.addPlayer(false, id, 1, pos, map.getBox2d());
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

    public void assignBall() {
        Gdx.app.log("GameServer", "Assigning ball");
        Vector2 ballPosition = new Vector2(GameConstants.GAME_WIDTH/2, GameConstants.GAME_HEIGHT/2);
        map.addBall(ballPosition, map.getBox2d());
        sendMessage(new Network.PacketAddBall(ballPosition));
    }

    public void addPowerUp() {
        // 3 types - Water(Slow), Cloak(Invisibility) , Confusion(DisorientedControls)
        Gdx.app.log("GameServer", "Assigning ball");
        int powerupType = 1;
        Vector2 powerupPosition = new Vector2(10, 20);
        map.addPowerUp(powerupPosition, powerupType);
        sendMessage(new Network.PacketAddPowerUp(powerupPosition, powerupType));
    }

    public GameMap getMap() {
        return map;
    }
}
