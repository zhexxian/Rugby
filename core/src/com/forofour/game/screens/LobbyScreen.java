package com.forofour.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import com.forofour.game.MyGdxGame;
import com.forofour.game.actors.LobbyActorMaker;
import com.forofour.game.actors.MenuActorMaker;
import com.forofour.game.handlers.AssetLoader;
import com.forofour.game.handlers.GameConstants;
import com.forofour.game.net.GameClient;
import com.forofour.game.net.GameServer;
import com.forofour.game.net.Network;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by seanlim on 1/4/2016.
 */
public class LobbyScreen implements Screen {

    private Stage stage;
    private LobbyActorMaker lobbyActorMaker;
    private SpriteBatch batch;

    private GameServer server;
    private GameClient client;
    private boolean isHost;
    private boolean tutorialMode;
    private String playerName;
    boolean showStartNudgeButton;

    public LobbyScreen(String hostname) {
        this(false, false);
    }
    public LobbyScreen(GameServer server) {
        this(false, true);
        this.server = server;
    }

    public LobbyScreen(boolean tutorialMode, final boolean isHost) {
        this.isHost = isHost;
        this.tutorialMode = tutorialMode;

        stage = new Stage(new ExtendViewport(
                GameConstants.GAME_WIDTH,
                GameConstants.GAME_HEIGHT));

        lobbyActorMaker = new LobbyActorMaker(stage);
        lobbyActorMaker.getButtonStartGame().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("StartButton", "Clicked!");
                if(isHost) {
                    server.sendMessage(new Network.PacketInitRound(true));
                    Gdx.app.log("StartButton", "PacketInitRound(true) sent");
                }
            }
        });

        lobbyActorMaker.getButtonNudgeHost().setVisible(false);
        lobbyActorMaker.getButtonNudgeHost().setVisible(false);

        Gdx.input.setInputProcessor(stage);
        batch = new SpriteBatch();
    }

    @Override
    public void show(){

        client = new GameClient(); // Launch a new client

        if(isHost){
            if(server == null)
                server = new GameServer();
            Gdx.app.log("LobbyScreen(host)", "Started server");
            client.connect("localhost");
            Gdx.app.log("LobbyScreen(host)", "Client connected to server");
        }

        else {
            client.quickConnect();
            Gdx.app.log("LobbyScreen", "Connecting to server");
        }

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();

        // TODO: Drawing does not scale accordingly to the phone
        batch.begin();
        for(int i=0; i<client.getMap().getNumberOfBabyFaces(); i++){
            batch.draw(AssetLoader.powerUp,
                    50+i*100, 50, 50, 50);
        }
        batch.end();

        // Only if Desired number of Players are connection would the buttons be Active/Visible
        showStartNudgeButton = client.getMap().getNumberOfBabyFaces() >= GameConstants.MAX_PLAYERS; // Should it be 2 or more?

        if(isHost) {
            // Shows
            lobbyActorMaker.getButtonStartGame().setVisible(showStartNudgeButton);
            if(server.getMap().gameInitiated || tutorialMode) {
                ((MyGdxGame) Gdx.app.getApplicationListener()).setScreen(new MainScreen(false, true, playerName, server, client));
            }
        } else {
            lobbyActorMaker.getButtonNudgeHost().setVisible(showStartNudgeButton);
            if(client.getMap().gameInitiated) {
                ((MyGdxGame) Gdx.app.getApplicationListener()).setScreen(new MainScreen(false, false, playerName, null, client));
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
    }

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        if(client != null)
            client.dispose();
        if(server != null)
            server.dispose();
    }
}
