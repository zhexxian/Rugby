package com.forofour.game.net;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.forofour.game.gameobjects.Player;
import com.forofour.game.handlers.GameConstants;
import com.forofour.game.handlers.GameMap;
import com.forofour.game.screens.LobbyScreen;

import java.io.IOException;

/**
 * Created by seanlim on 1/4/2016.
 */
public class GameServer {

    private boolean gameStarted;
    private Server server;
    private GameMap map;

    private static int numberOfBabyFaces;

    public GameServer(){
        map = new GameMap(this);

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
                        server.sendToAllTCP(new Network.PacketPlayerJoinLeave(-1, map.getPlayersConnected().size()));
                        //++numberOfBabyFaces;
                    }
                }

                // Updates location of specific player
                else if(o instanceof Network.PacketPlayerState) {
                    Network.PacketPlayerState packet = (Network.PacketPlayerState) o;
                    Gdx.app.log("GameServer", "State Updates for player" + "-" + c.getID() + "-" + packet.id);
                    map.updatePlayerState(packet.id, packet.position, packet.angle);
                    server.sendToAllTCP(new Network.PacketPlayerState(packet.id, packet.position, packet.angle));

                }

                else if(o instanceof Network.PacketPlayerUpdateMovement) {
                    Network.PacketPlayerUpdateMovement packet = (Network.PacketPlayerUpdateMovement) o;
                    Gdx.app.log("GameServer", "Movement Updates for player" + "-" + packet.id + "-" + packet.movement);
                    map.updatePlayerMovement(packet.id, packet.movement);
                    server.sendToAllUDP(new Network.PacketPlayerUpdateMovement(packet.id, packet.movement));

                }
            }

            public void connected(Connection c) {
                Gdx.app.log("GameServer", "Player connected");
            }

            public void disconnected(Connection c) {
                Gdx.app.log("GameServer", "Player disconnected");
                map.dropConnection(c.getID());
                server.sendToAllTCP(new Network.PacketDebugAnnouncement("Player " + c.getID() + " has disconnected from the game"));
                server.sendToAllTCP(new Network.PacketDebugAnnouncement("Number of player in lobby: " + map.getPlayersConnected().size()));
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
                    map.addPlayer(false, id, 1, pos);
                    sendMessage(new Network.PacketAddPlayer(id, 1, pos));
                }
                else {
                    map.addPlayer(false, id, 2, pos);
                    sendMessage(new Network.PacketAddPlayer(id, 2, pos));
                }
            }
        }
        Gdx.app.log("GameServer", "Assigned playerHash: " + map.getPlayerHash().toString());
    }

    public void assignBall() {
        Gdx.app.log("GameServer", "Assigning ball");
        Vector2 ballPosition = new Vector2(GameConstants.GAME_WIDTH/2, GameConstants.GAME_HEIGHT/2);
        map.addBall(ballPosition);
        sendMessage(new Network.PacketAddBall(ballPosition));
    }

    public void updateBallMovement() {
        Gdx.app.log("GameServer", "Sending ball movement updates");
        sendMessage(new Network.PacketBallUpdateMovement(map.getBall().getBody().getLinearVelocity()));
    }
    public void updateBallState() {
        Gdx.app.log("GameServer", "Sending ball state updates");
        sendMessage(new Network.PacketBallState(map.getBall().getBody().getPosition(), 0));
    }

    public static int getNumberOfBabyFaces(){
        int temp = numberOfBabyFaces;
        return temp;
    }
}
