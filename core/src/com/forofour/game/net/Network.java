package com.forofour.game.net;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

/**
 * Created by seanlim on 1/4/2016.
 */
public class Network {
    public static final int port = 27960;
    public static final int portUDP = 27962;

    public static void register(EndPoint endPoint){
        Kryo kryo = endPoint.getKryo();
        kryo.register(Float.class);
        kryo.register(Vector2.class);
        kryo.register(PacketPlayerJoinLeave.class);
        kryo.register(PacketDebugAnnouncement.class);
        kryo.register(PacketAddPlayer.class);
        kryo.register(PacketPlayerState.class);
        kryo.register(PacketPlayerUpdateMovement.class);
        kryo.register(PacketAddBall.class);
        kryo.register(PacketBallState.class);
        kryo.register(PacketBallUpdateMovement.class);
        kryo.register(PacketInitRound.class);
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

    public static class PacketAddPlayer {
        public int id;
        public int teamNumber;
        public Vector2 position;
        public PacketAddPlayer() {}
        public PacketAddPlayer(int id, int teamNumber, Vector2 position) {
            this.id = id;
            this.teamNumber = teamNumber;
            this.position = position;
        }
    }

    public static class PacketPlayerState {
        public int id;
        public Vector2 position;
        public Float angle;
        public PacketPlayerState(){}
        public PacketPlayerState(int id, Vector2 position, float angle) {
            this.id = id;
            this.position = position;
            this.angle = angle;
        }
    }

    public static class PacketPlayerUpdateMovement {
        public int id;
        public Vector2 movement;
        public PacketPlayerUpdateMovement(){};
        public PacketPlayerUpdateMovement(int id, Vector2 movement) {
            this.id = id;
            this.movement = movement;
        }
    }

    public static class PacketAddBall {
        public Vector2 position;
        public PacketAddBall() {}
        public PacketAddBall(Vector2 postion) {
            this.position = postion;
        }
    }

    public static class PacketBallState {
        public Vector2 position;
        public Float angle;
        public PacketBallState(){}
        public PacketBallState(Vector2 position, float angle) {
            this.position = position;
            this.angle = angle;
        }
    }

    public static class PacketBallUpdateMovement {
        public Vector2 movement;
        public PacketBallUpdateMovement(){};
        public PacketBallUpdateMovement(Vector2 movement) {
            this.movement = movement;
        }
    }

    public static class PacketInitRound {
        public boolean start;
        public PacketInitRound(){}
        public PacketInitRound(boolean start){
            this.start = start;
        }
    }

}
