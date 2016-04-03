package com.forofour.game.net;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

/**
 * Created by seanlim on 1/4/2016.
 */
public class Packets {
    public static final int port = 27960;
    public static final int portUDP = 27962;

    public static void register(EndPoint endPoint){
        Kryo kryo = endPoint.getKryo();
        kryo.register(PacketPlayerJoinLeave.class);
        kryo.register(PacketDebugAnnouncement.class);
    }

    public static class PacketDebugAnnouncement {
        private String msg;

        public PacketDebugAnnouncement(){}
        public PacketDebugAnnouncement(String msg) {
            this.msg = msg;
        }
        public String getMsg() {
            return "--- Server Announcement Start --- : " + msg;
        }
    }

    public static class PacketPlayerJoinLeave {
        public int id;

        public PacketPlayerJoinLeave(){}

        public PacketPlayerJoinLeave(int id){
            this.id = id;

        }
    }


}
