package com.forofour.game.net;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.forofour.game.gameobjects.Player;
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
                else if(o instanceof Network.PacketAddBall) {
                    Network.PacketAddBall packet = (Network.PacketAddBall) o;
                    Gdx.app.log("GameClient", "Adding ball");
                    map.addBall(packet.position);
                }
                else if(o instanceof Network.PacketBallState) {
                    Network.PacketBallState packet = (Network.PacketBallState) o;
                    map.updateBallState(packet.position, packet.angle);
                }
                else if(o instanceof Network.PacketBallUpdateMovement) {
                    Network.PacketBallUpdateMovement packet = (Network.PacketBallUpdateMovement) o;
                    map.updateBallMovement(packet.movement);
                }

                else if(o instanceof Network.PacketAddPlayer) {
                    Network.PacketAddPlayer packet = (Network.PacketAddPlayer) o;
                    Gdx.app.log("GameClient", "Adding player" + c.getID() + "-" + packet.id + "-" + packet.teamNumber);
                    if(c.getID() == packet.id) // Allow specific ID to be the controller
                        map.addPlayer(true, packet.id, packet.teamNumber, packet.position);
                    else
                        map.addPlayer(false, packet.id, packet.teamNumber, packet.position);
                }
                else if(o instanceof Network.PacketPlayerState) {
                    Network.PacketPlayerState packet = (Network.PacketPlayerState) o;
                    map.updatePlayerState(packet.id, packet.position, packet.angle);
                }
                else if(o instanceof Network.PacketPlayerUpdateMovement) {
                    Network.PacketPlayerUpdateMovement packet = (Network.PacketPlayerUpdateMovement) o;
                    map.updatePlayerMovement(packet.id, packet.movement);
                }
            }
            public void connected(Connection c){
                Gdx.app.log("GameClient", "Player connected");
                        Network.PacketPlayerJoinLeave newPlayer = new Network.PacketPlayerJoinLeave(c.getID());
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
