package com.forofour.game.net;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.forofour.game.handlers.GameMap;
import com.forofour.game.tutorialMode.TutorialStates;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Instance of Client (host also has an instance of client)
 *  contains overall gameStates and also, the handling of packets received
 *
 */
public class GameClient {

    private Client client;
    private String hostAddress;

    private GameMap map;
    private boolean tutorialMode;
    public boolean serverReady, playAgain, playEnd;

    public GameClient(boolean tutorialMode){
        this.tutorialMode = tutorialMode;
        map = new GameMap(this);
        if(this.tutorialMode)
            map.setGameDuration(TutorialStates.getDuration()); // Sets the TutorialMode gameDuration and access to other states

        client = new Client();
        client.start();
        Network.register(client);

        // Client receives updates via Listener
        //  As a event, it trigger certain updates as packets are received
        client.addListener(new Listener.ThreadedListener(new Listener(){
            public void received(Connection c, Object o){

                // Generic debug announcment from server
                if (o instanceof Network.PacketDebugAnnouncement){
                    Network.PacketDebugAnnouncement packet = (Network.PacketDebugAnnouncement) o;
                    //Gdx.app.log("GameClient", packet.getMsg());
                }

                // Trigger to exit to mainMenu when server decides not to play
                else if(o instanceof Network.PacketShutdown) {
                    map.shutdown = true;
                    //Gdx.app.log("GameClient", "Exit lobby received from server");
                }

                // Receives packet when server clicks on "StartGame"
                else if(o instanceof Network.PacketInitRound) {
                    Network.PacketInitRound packet = (Network.PacketInitRound) o;
                    map.gameInitiated = packet.initiate;
                    //Gdx.app.log("GameClient", "InitRound Received from Server " + packet.initiate);
                }

                // Triggers pausing of game, all updates are paused, except overlay renderer
                else if(o instanceof Network.PacketGamePause) {
                    Network.PacketGamePause packet = (Network.PacketGamePause) o;
                    map.gamePaused = packet.gamePaused;
                    //Gdx.app.log("GameClient", "PauseButton Received from Server");
                }

                // Triggers the end of game(e.g. time is up)
                else if(o instanceof Network.PacketGameEnd) {
//                    Network.PacketGameEnd packet = (Network.PacketGameEnd) o;
                    map.gamePaused = true;
                    map.gameEnd = true;
                    //Gdx.app.log("GameClient", "Game End Received from Server " + map.gameEnd);
                }

                // Decision on possibility to PlayAgain at the end of gamePlay.
                // Allowed only if server decides to
                else if(o instanceof Network.PacketReinitLobby) {
                    Network.PacketReinitLobby packet = (Network.PacketReinitLobby) o;
                    if(packet.serverReady) {
                        //Gdx.app.log("GameClient", "ReinitLobby ALLOWED Received from Server");
                        reinitLobby(); // If SERVER allows, to show BUTTON to PLAYAGAIN
                    } else {
                        //Gdx.app.log("GameClient", "ReinitLobby DISALLOWED Received from Server");
//                        reinitMenu();
                    }
                }

                // Update on joining/leaving of players
                else if(o instanceof Network.PacketPlayerJoinLeave) {
                    Network.PacketPlayerJoinLeave packet = (Network.PacketPlayerJoinLeave) o;
                    map.setNumberOfBabyFaces(packet.connectedClients);
                    //Gdx.app.log("GameClient", "Number of Connected clients " + packet.connectedClients);
                }
                // Indication that clients are ready
                else if (o instanceof Network.PacketTeamScores) {
                    Network.PacketTeamScores packet = (Network.PacketTeamScores) o;
//                    //Gdx.app.log("GameClient" + c.getID(), "A Current " + map.getTeamA().getScore()
//                            + "\tChange " + packet.scoreA
//                            + "\tMembersId " + map.getTeamA().getTeamList().get(0).getId());
//                    //Gdx.app.log("GameClient" + c.getID(), "B " + map.getTeamB().getScore()
//                            + "\tChange" + packet.scoreB
//                            + "\tMembersId " + map.getTeamA().getTeamList().get(0).getId());
//                    //Gdx.app.log("GameClient" + c.getID(), "Player Team number" + map.getPlayer().getTeamId());
                    map.getTeamA().setScore(packet.scoreA);
                    map.getTeamB().setScore(packet.scoreB);
                }
                // Assignment of ball during game initiation
                else if(o instanceof Network.PacketAddBall) {
                    Network.PacketAddBall packet = (Network.PacketAddBall) o;
                    //Gdx.app.log("GameClient", "Adding ball");
                    map.addBall(packet.position, map.getBox2d());
                }
                // Periodic update of ball position being held state
                else if(o instanceof Network.PacketBallState) {
                    Network.PacketBallState packet = (Network.PacketBallState) o;
                    if(!packet.isHeld) {
                        map.updateBallState(packet.position, packet.angle);
                        map.updateDropBall();
                    }
                    else if(map.getBall().getHoldingPlayer() != null){ // If ball is held, set it to position of holding player
                        map.updateBallState(map.getBall().getHoldingPlayer().getBody().getPosition(), packet.angle);
                    }
                }
                // Fast update of ball movement
                else if(o instanceof Network.PacketBallUpdateFast) { // Ball velocity & Player holder ID
                    Network.PacketBallUpdateFast packet = (Network.PacketBallUpdateFast) o;
                    map.updateBallMovement(packet.movement);
//                    //Gdx.app.log("GameClient", "PacketBallUpdateFast");
                }

                // Assignment of player during game initiation
                else if(o instanceof Network.PacketAddPlayer) {
                    Network.PacketAddPlayer packet = (Network.PacketAddPlayer) o;
                    //Gdx.app.log("GameClient", "Adding player" + c.getID() + "-" + packet.id + "-" + packet.teamNumber);
                    if(c.getID() == packet.id) // Allow specific ID to be the controller
                        map.addPlayer(true, packet.id, packet.teamNumber, packet.position, map.getBox2d());
                    else
                        map.addPlayer(false, packet.id, packet.teamNumber, packet.position, map.getBox2d());
                }
                // Periodic update of player orientation and position
                else if(o instanceof Network.PacketPlayerState) {
                    Network.PacketPlayerState packet = (Network.PacketPlayerState) o;
                    map.updatePlayerState(packet.id, packet.position, packet.angle);
                }
                // Fast update of player movement
                else if(o instanceof Network.PacketPlayerUpdateFast) {
                    Network.PacketPlayerUpdateFast packet = (Network.PacketPlayerUpdateFast) o;
                    map.updatePlayerMovement(packet.id, packet.movement);
                }

                // Trigger that ball has been tossed/dropped
                // NOTE: for reasons, this packet is slower than PacketSetHoldingPlayer
                else if(o instanceof Network.PacketDropBall) {
                    Network.PacketDropBall packet = (Network.PacketDropBall) o;
                    map.updateDropBall();
                }

                // Decision by server on holder of the Ball
                else if(o instanceof Network.PacketSetHoldingPlayer) { // Ball holder ID
                    Network.PacketSetHoldingPlayer packet = (Network.PacketSetHoldingPlayer) o;
                    if(packet.id == -1) {
                        map.updateDropBall();
                    } else {
                        map.updateHoldingPlayer(packet.id);
                    }
                    //Gdx.app.log("GameClient", "PacketSetHoldingPlayer " + packet.id);
                }

                // Assignment of PowerUp package to game
                else if(o instanceof Network.PacketAddPowerUp) {
                    Network.PacketAddPowerUp packet = (Network.PacketAddPowerUp) o;
                    //Gdx.app.log("GameClient", "PacketAddPowerUp, Type:" + packet.type + " Position:" + packet.position);
                    map.addPowerUp(packet.position, packet.type);
                }

                // Acquisition of PowerUp by player
                else if(o instanceof Network.PacketPickPowerUp) {
                    Network.PacketPickPowerUp packet = (Network.PacketPickPowerUp) o;
                    //Gdx.app.log("GameClient", "PacketPickPowerUp, ID:" + packet.playerId + " Type:"+packet.type + " ItemID:"+packet.powerUpId);
                    map.getPlayerHash().get(packet.playerId).acquirePowerUp(packet.type);
                    if(packet.powerUpId != -1) // DEBUG PURPOSES(SV COMMANDS)
                        map.removePowerUp(packet.powerUpId);
                }

                // Trigger of player's use of a powerUp
                else if(o instanceof Network.PacketUsePowerUp) {
                    Network.PacketUsePowerUp packet = (Network.PacketUsePowerUp) o;
//                    //Gdx.app.log("GameClient", "PacketUsePowerUp, ID:" + packet.id);
//                    //Gdx.app.log("GameClient", "HASH " + map.getPlayerHash());
//                    //Gdx.app.log("GameClient", "PLAYER " + map.getPlayer());
//                    //Gdx.app.log("GameClient", "PLAYER " + map.getPlayer().hasPowerUp());
//                    //Gdx.app.log("GameClient", "P1 " + map.getPlayerHash().get(1));
//                    //Gdx.app.log("GameClient", "P1 " + map.getPlayerHash().get(1).hasPowerUp());
//                    //Gdx.app.log("GameClient", "P2 " + map.getPlayerHash().get(2));
//                    //Gdx.app.log("GameClient", "P2 " + map.getPlayerHash().get(2).hasPowerUp());
                    map.getPlayerHash().get(packet.id).usePowerUp(packet.generatedChoice);
//                    //Gdx.app.log("GameClient", "P" + packet.id + "CONSUMED");
//                    //Gdx.app.log("GameClient", "PLAYER " + map.getPlayer());
//                    //Gdx.app.log("GameClient", "PLAYER " + map.getPlayer().hasPowerUp());
//                    //Gdx.app.log("GameClient", "P1 " + map.getPlayerHash().get(1));
//                    //Gdx.app.log("GameClient", "P1 " + map.getPlayerHash().get(1).hasPowerUp());
//                    //Gdx.app.log("GameClient", "P2 " + map.getPlayerHash().get(2));
//                    //Gdx.app.log("GameClient", "P2 " + map.getPlayerHash().get(2).hasPowerUp());
                }
            }
            public void connected(Connection c){
                //Gdx.app.log("GameClient", "Player connected");
                Network.PacketPlayerJoinLeave newPlayer = new Network.PacketPlayerJoinLeave(c.getID(), map.getPlayersConnected().size());
                client.sendTCP(newPlayer);
            }
            public void disconnected(Connection c){
                //Gdx.app.log("GameClient", "Player disconnected");
            }
        }));
    }

    // Generic connect method
    public Integer connect(String host){
        try{
            this.hostAddress = host;
            //Gdx.app.log("Client", "Connecting(specific) to " + host);
            client.connect(5000, host, Network.port, Network.portUDP);
            //Gdx.app.log("Client", "Connected to host");
            return client.getID();
        } catch(IOException ex) {
            //Gdx.app.log("Client", "Failed to connect to host");
            map.shutdown = true;
        }
        return null;
    }

    // Connect method that employs to use of the inbuilt hostDiscovery
    public boolean quickConnect(){
        //Gdx.app.log("Client", "Discovering on " + Network.portUDP);
        InetAddress address = client.discoverHost(Network.portUDP, 5000);

        //Gdx.app.log("Client", "Found server on " + address);
        if(address != null) {
            //Gdx.app.log("Client", "Server HostAddress " + address.getHostAddress());
            //Gdx.app.log("Client", "Server HostName " + address.getHostName());
            this.hostAddress = address.getHostAddress();
            try {
                //Gdx.app.log("Client", "Connecting(quick) to " + address);
                client.connect(1000, address, Network.port, Network.portUDP);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                //Gdx.app.log("Client", "Failed to connect to host at " + address);
                return false;
            }
        }
        return false;
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

    public void reinitLobby() {
        shutdown();
        //Show Client-Sided button to "PLAY AGAIN". Only if server allows
        serverReady = true;
    }

    public void reinitMenu(){
        shutdown();
        playEnd = true;
    }

    public void shutdown() {
        client.close();
        client.stop();
    }

    public void dispose() {
        try {
            client.dispose();
        } catch (IOException e) {
            e.printStackTrace();
        }
        map.dispose();
    }

    // Play again, will reuse previously used address instead of rediscovery which takes time
    public String getHostAddress(){
        String address = hostAddress;
        hostAddress = "";
        return address;
    }
}
