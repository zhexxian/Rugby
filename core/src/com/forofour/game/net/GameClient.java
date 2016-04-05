package com.forofour.game.net;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.forofour.game.handlers.GameMap;

import java.io.IOException;

/**
 * Created by seanlim on 1/4/2016.
 */
public class GameClient {

    private Client client;
    private GameMap map;

    public GameClient(){
        map = new GameMap(this);

        client = new Client();
        client.start();
        Network.register(client);

        // Client receives updates via Listener
        client.addListener(new Listener(){
            public void received(Connection c, Object o){
                if (o instanceof Network.PacketDebugAnnouncement){
                    Network.PacketDebugAnnouncement packet = (Network.PacketDebugAnnouncement) o;
                    Gdx.app.log("GameClient", packet.getMsg());
                }
                else if(o instanceof Network.PacketPlayerJoinLeave) {
                    Network.PacketPlayerJoinLeave packet = (Network.PacketPlayerJoinLeave) o;
                    map.setNumberOfBabyFaces(packet.connectedClients);
                }

                // BALL
                else if(o instanceof Network.PacketAddBall) {
                    Network.PacketAddBall packet = (Network.PacketAddBall) o;
                    Gdx.app.log("GameClient", "Adding ball");
                    map.addBall(packet.position, map.getBox2d());
                }
                else if(o instanceof Network.PacketBallState) {
                    Network.PacketBallState packet = (Network.PacketBallState) o;
                    map.updateBallState(packet.position, packet.angle);
                }
                else if(o instanceof Network.PacketBallUpdateFast) { // Ball velocity & Player holder ID
                    Network.PacketBallUpdateFast packet = (Network.PacketBallUpdateFast) o;
                    map.updateBallMovement(packet.movement);
                    Gdx.app.log("GameClient", "PacketBallUpdateFast");
                }

                // PLAYER
                else if(o instanceof Network.PacketAddPlayer) {
                    Network.PacketAddPlayer packet = (Network.PacketAddPlayer) o;
                    Gdx.app.log("GameClient", "Adding player" + c.getID() + "-" + packet.id + "-" + packet.teamNumber);
                    if(c.getID() == packet.id) // Allow specific ID to be the controller
                        map.addPlayer(true, packet.id, packet.teamNumber, packet.position, map.getBox2d());
                    else
                        map.addPlayer(false, packet.id, packet.teamNumber, packet.position, map.getBox2d());
                }
                else if(o instanceof Network.PacketPlayerState) {
                    Network.PacketPlayerState packet = (Network.PacketPlayerState) o;
                    map.updatePlayerState(packet.id, packet.position, packet.angle);
                }
                else if(o instanceof Network.PacketPlayerUpdateFast) {
                    Network.PacketPlayerUpdateFast packet = (Network.PacketPlayerUpdateFast) o;
                    map.updatePlayerMovement(packet.id, packet.movement);
                }

                // NOTE: for reasons, this packet is slower than PacketSetHoldingPlayer
                else if(o instanceof Network.PacketDropBall) {
                    Network.PacketDropBall packet = (Network.PacketDropBall) o;
                    map.updateDropBall();
                }
                else if(o instanceof Network.PacketSetHoldingPlayer) { // Ball holder ID
                    Network.PacketSetHoldingPlayer packet = (Network.PacketSetHoldingPlayer) o;
                    if(packet.id == -1) {
                        map.updateDropBall();
                    } else {
                        map.getBall().setHoldingPlayer(map.getPlayerHash().get(packet.id));
                    }
                    Gdx.app.log("GameClient", "PacketSetHoldingPlayer " +packet.id);
                }
            }
            public void connected(Connection c){
                Gdx.app.log("GameClient", "Player connected");
                        Network.PacketPlayerJoinLeave newPlayer = new Network.PacketPlayerJoinLeave(c.getID(), map.getPlayersConnected().size());
                client.sendTCP(newPlayer);
            }
            public void disconnected(Connection c){
                Gdx.app.log("GameClient", "Player disconnected");
            }
        });
    }

    public Integer connect(String host){
        try{
            client.connect(10000, host, Network.port, Network.portUDP);
            Gdx.app.log("GameClient", "Unable to connect to host");
            return client.getID();
        } catch(IOException ex) {
            Gdx.app.log("GameClient", "Connected to host");
        }
        return null;
    }

    public GameMap getMap(){
        return map;
    }

    public void sendMessage(Object message) {
        if (client.isConnected()) {
            client.sendTCP(message);
        }
    }

    public void sendMessageUDP(Object message) {
        if (client.isConnected()) {
            client.sendUDP(message);
        }
    }

    public void shutdown() {
        client.close();
        client.stop();
    }
}
