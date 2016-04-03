package com.forofour.game.net;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;

/**
 * Created by seanlim on 1/4/2016.
 */
public class GameClient {


    private Client client;


    public GameClient(){
        client = new Client();
        client.start();
        Packets.register(client);

        client.addListener(new Listener(){
            public void received(Connection c, Object o){
                if (o instanceof Packets.PacketDebugAnnouncement){
                    Packets.PacketDebugAnnouncement packet = (Packets.PacketDebugAnnouncement) o;
                    System.out.println(packet.getMsg());
                }
            }
            public void connected(Connection c){
                System.out.println("Client Msg: player connected");
                Packets.PacketPlayerJoinLeave newPlayer = new Packets.PacketPlayerJoinLeave(c.getID());
                client.sendTCP(newPlayer);
            }
            public void disconnected(Connection c){
                System.out.println("Client Msg: disconnected");
            }
        });
    }

    public void connect(String host){
        try{
            client.connect(10000,host,Packets.port,Packets.portUDP);
            System.out.println("connected to host");
        } catch(IOException ex) {
            System.out.println("can't connect to host");
        }
    }

}
