package com.forofour.game.net;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.forofour.game.gameworlds.GameWorld;
import com.forofour.game.screens.LobbyScreen;

import java.io.IOException;

/**
 * Created by seanlim on 1/4/2016.
 */
public class GameServer {
    Server server;
    private GameWorld world;
    private LobbyScreen lobby;

    public GameServer(final LobbyScreen lobby){
//        world = new GameWorld();
        this.lobby = lobby;
        server = new Server() {
//            protected Connection newConnection(){
//                return new GameConnection();
//            }
        };

        Packets.register(server);

        server.addListener(new Listener() {
            public void received(Connection c, Object o){
                if (o instanceof Packets.PacketPlayerJoinLeave){
                    Packets.PacketPlayerJoinLeave packet = (Packets.PacketPlayerJoinLeave) o;
                    if(lobby.addConnection(packet.id)) {
                        server.sendToAllTCP(new Packets.PacketDebugAnnouncement("New Player connected successfully"));
                        server.sendToAllTCP(new Packets.PacketDebugAnnouncement("Number of player in lobby: " + lobby.getPlayersConnected().size()));
                    }
                }
            }
            public void connected(Connection c){
                System.out.println("Server Msg: player connected");

            }
            public void disconnected(Connection c){
                System.out.println("Server Msg: player disconnected");
                lobby.dropConnection(c.getID());
                server.sendToAllTCP(new Packets.PacketDebugAnnouncement("Player " + c.getID() + " has disconnected from the game"));
                server.sendToAllTCP(new Packets.PacketDebugAnnouncement("Number of player in lobby: " + lobby.getPlayersConnected().size()));


            }
        });

        try{
            server.bind(Packets.port, Packets.portUDP);
        } catch (IOException ex){
            ex.printStackTrace();
        }

        server.start();
        System.out.println("Server is running");

    }

    public void update(float delta){world.update(delta);}


}
