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

        kryo.register(PacketDebugAnnouncement.class);
        kryo.register(PacketPlayerJoinLeave.class);
        kryo.register(PacketShutdown.class);
        kryo.register(PacketNudge.class);

        kryo.register(PacketInitRound.class);
        kryo.register(PacketGamePause.class);
        kryo.register(PacketGameEnd.class);
        kryo.register(PacketReinitLobby.class);

        kryo.register(PacketTeamScores.class);

        kryo.register(PacketAddPlayer.class);
        kryo.register(PacketPlayerState.class);
        kryo.register(PacketPlayerUpdateFast.class);

        kryo.register(PacketAddBall.class);
        kryo.register(PacketBallState.class);
        kryo.register(PacketBallUpdateFast.class);
        kryo.register(PacketDropBall.class);
        kryo.register(PacketSetHoldingPlayer.class);

        kryo.register(PacketAddPowerUp.class);
        kryo.register(PacketUsePowerUp.class);
        kryo.register(PacketPickPowerUp.class);
    }

    // General debug messages used in the preliminary design
    public static class PacketDebugAnnouncement {
        private String msg;
        public PacketDebugAnnouncement(){}
        public PacketDebugAnnouncement(int no){}
        public PacketDebugAnnouncement(String msg) {
            this.msg = msg;
        }
        public String getMsg() {
            return "--- Server Announcement Start --- : " + msg;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Universal triggers from server to connected clients
    // Governs state when clients are in LOBBY & GAME

    // Trigger closure of LobbyScreen
    public static class PacketNudge{
        public PacketNudge() {
        }
    }

    // Trigger closure of LobbyScreen
    public static class PacketShutdown {
        public PacketShutdown() {
        }
    }

    // Trigger gamePause state to/from client
    public static class PacketGamePause {
        public boolean gamePaused;
        public PacketGamePause() {
        }
        public PacketGamePause(boolean gamePaused) {
            this.gamePaused = gamePaused;
        }
    }

    // Server to trigger GameEnd state, Client to reply PlayAgain Intentions
    public static class PacketGameEnd {
        public boolean playAgain;
        public PacketGameEnd() {
        }
        public PacketGameEnd(boolean playAgain) {
            this.playAgain = playAgain;
        }
    }

    // Server's state on readiness to StartGame(e.g. 2 players connected)
    public static class PacketReinitLobby {
        public boolean serverReady;
        public PacketReinitLobby() {
        }
        public PacketReinitLobby(boolean serverReady) {
            this.serverReady = serverReady;
        }
    }

    // Update of players connected
    public static class PacketPlayerJoinLeave {
        public int id;
        public int connectedClients;
        public PacketPlayerJoinLeave(){}
        public PacketPlayerJoinLeave(int id, int connectedClients){
            this.id = id;
            this.connectedClients = connectedClients;
        }
    }

    // Trigger to procced to Game from lobby screen
    public static class PacketInitRound {
        public boolean initiate;
        public PacketInitRound(){}
        public PacketInitRound(boolean initiate){
            this.initiate = initiate;
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    // IN-GAME PACKETS
    // TYPES:
    // Assignments - Server sided logic that dictates clients states
    // Updates/Trigger - May come from client(e.g. drop ball, player location),
    //  and the server serves as a relay to all clients

    // Assignment of connection id to player
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

    // Slow update of player states
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

    // Fast update of player states
    public static class PacketPlayerUpdateFast {
        public int id;
        public Vector2 movement;
        public PacketPlayerUpdateFast(){};
        public PacketPlayerUpdateFast(int id, Vector2 movement) {
            this.id = id;
            this.movement = movement;
        }
    }

    // Assignment of ball into game
    public static class PacketAddBall {
        public Vector2 position;
        public PacketAddBall() {}
        public PacketAddBall(Vector2 postion) {
            this.position = postion;
        }
    }

    // Slow update of ball state
    public static class PacketBallState {
        public Vector2 position;
        public Float angle;
        public boolean isHeld;
        public PacketBallState(){}
        public PacketBallState(Vector2 position, float angle, boolean isHeld) {
            this.position = position;
            this.angle = angle;
            this.isHeld = isHeld;
        }
    }

    // Fast update of ball state
    public static class PacketBallUpdateFast {
        public Vector2 movement;
        public PacketBallUpdateFast(){};
        public PacketBallUpdateFast(Vector2 movement) {
            this.movement = movement;
        }
    }

    // Trigger to client that ball has dropped
    public static class PacketDropBall {
        public int id;
        public Vector2 lastDirection; // For the server
        public PacketDropBall(){}
        public PacketDropBall(int id) {
            this.id = id;
        }
        public PacketDropBall(int id, Vector2 lastDirection) {
            this.id = id;
            this.lastDirection = lastDirection;
        }
    }

    // Assignment of power up into game
    public static class PacketAddPowerUp {
        public int type;
        public Vector2 position;
        public PacketAddPowerUp() {}
        public PacketAddPowerUp(int type) {
            this.type = type;
        }
        public PacketAddPowerUp(Vector2 position, int type) {
            this.position = position;
            this.type = type;
        }
    }

    // Assignment of power up to player
    public static class PacketPickPowerUp {
        public int playerId;
        public int type;
        public int powerUpId;
        public PacketPickPowerUp() {}
        public PacketPickPowerUp(int type) {
            this.type = type;
        }

        public PacketPickPowerUp(int playerId, int type, int powerUpId) {
            this.playerId = playerId;
            this.type = type;
            this.powerUpId = powerUpId;
        }
    }

    // Trigger use of power up
    public static class PacketUsePowerUp {
        public int id;
        public int generatedChoice;
        public PacketUsePowerUp() {}
        public PacketUsePowerUp(int id, int generateChoice) {
            this.id = id;
            this.generatedChoice = generateChoice;
        }
    }

    // Update holder of ball
    public static class PacketSetHoldingPlayer {
        public int id;
        public PacketSetHoldingPlayer() {}
        public PacketSetHoldingPlayer(int id) {
            this.id = id;
        }
    }

    // Update of team scores
    public static class PacketTeamScores {
        public int scoreA;
        public int scoreB;
        public PacketTeamScores() {
        }

        public PacketTeamScores(int scoreA, int scoreB) {
            this.scoreA = scoreA;
            this.scoreB = scoreB;
        }
    }
}
