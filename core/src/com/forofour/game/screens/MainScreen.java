package com.forofour.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.forofour.game.MyGdxGame;
import com.forofour.game.actors.TouchPadMaker;
import com.forofour.game.gameobjects.Player;
import com.forofour.game.gameworlds.MainRenderer;
import com.forofour.game.handlers.ClientInputHandler;
import com.forofour.game.handlers.GameMap;
import com.forofour.game.handlers.InputHandler;
import com.forofour.game.handlers.MainOverlay;
import com.forofour.game.net.GameClient;
import com.forofour.game.net.GameServer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.forofour.game.net.Network;

/**
 * Created by seanlim on 4/4/2016.
 */
public class MainScreen implements Screen {

    private boolean tutorialMode;
    private GameMap map;
    private MainRenderer renderer;
    private MainOverlay overlay;

    private GameServer server;
    private GameClient client;
    private boolean isHost;
    private String name;

    private FPSLogger fpsLogger = new FPSLogger();

    public MainScreen(boolean tutorialMode, boolean isHost, String playerName, GameServer server, GameClient client) {
        this.tutorialMode = tutorialMode;
        this.isHost = isHost;
        this.name = playerName;
        this.server = server;
        this.client = client;

        map = client.getMap();
        renderer = new MainRenderer(map);
        overlay = new MainOverlay(client);

        Gdx.input.setInputProcessor(new ClientInputHandler(overlay)); // Stage itself is an inputAdapter

    }

    @Override
    public void show() {

        if(isHost) {
            Gdx.app.log("MainScreen", "Server ready");

        } else {
            Gdx.app.log("MainScreen", "Client ready");
            client.sendMessage(new Network.PacketInitRound());
        }

    }

    @Override
    public void render(float delta) {

        // Client-sided
        // TODO: Player to send server its movement values, Server to reply with update position
        if(!map.gamePaused) {

            map.update(delta);
            map.getGlobalTime().start();

            // Server-sided. Holds master map
            if (isHost) {
                server.update(delta);

//                Gdx.app.log("ClientTime", map.getGlobalTime().getElapsed());
//                Gdx.app.log("HostTime", server.getMap().getGlobalTime().getElapsed());

                if(server.getMap().getGlobalTime().isDone()) { // Triggers End of game when i)TIME IS UP
                    server.getMap().gamePaused = true;
                    server.getMap().gameEnd = true;
                    server.sendMessage(new Network.PacketGameEnd());
                }
            }
        }
        else {
            map.getGlobalTime().pause();
        }

        renderer.render(delta);
        overlay.update(delta);
//        fpsLogger.log();

        if(isHost) {
            if (server.restart) {
                // Restarts host into lobbyScreen at instant
                ((MyGdxGame) Gdx.app.getApplicationListener()).setScreen(new LobbyScreen(false, true));
            }
        }
        else {
            if (client.restart) {
                // Shows PlayAgain button on client's screen
                // Button should launch LobbyScreen and reconnect to sameHost
                ((MyGdxGame) Gdx.app.getApplicationListener()).setScreen(new LobbyScreen(false, false));
            }
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        client.shutdown();
        if (server != null)
            server.shutdown();
    }

    @Override
    public void dispose() {
        overlay.dispose();
        if(server != null)
            server.dispose();
        if(client != null)
            client.dispose();
    }
}
