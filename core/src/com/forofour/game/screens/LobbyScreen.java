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
    private String playerName;
    boolean showStartNudgeButton;

    public LobbyScreen(boolean tutorialMode, final boolean isHost) {
        this.isHost = isHost;

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
                }
                // Old implementation of buttons press
//                if(isHost)
//                    ((MyGdxGame) Gdx.app.getApplicationListener()).setScreen(new MainScreen(false, true, playerName, server, client));
//                else
//                    ((MyGdxGame) Gdx.app.getApplicationListener()).setScreen(new MainScreen(false, false, playerName, null, client));
            }
        });

        Gdx.input.setInputProcessor(stage);
        batch = new SpriteBatch();
    }

    @Override
    public void show(){

        client = new GameClient();

        if(isHost){
            Gdx.app.log("LobbyScreen", "Starting server");
            server = new GameServer();
        }

        Gdx.app.log("LobbyScreen", "Connecting to server");
        client.connect("192.168.1.231");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
        batch.begin();
        for(int i=0; i<client.getMap().getNumberOfBabyFaces(); i++){
            batch.draw(AssetLoader.powerUp,
                    50+i*100, 50, 50, 50);
        }
        batch.end();

        // Only if Desired number of Players are connection would the buttons be Active/Visible
        showStartNudgeButton = client.getMap().getNumberOfBabyFaces() >= GameConstants.MAX_PLAYERS; // Should it be 2 or more?
        if(isHost) {
            lobbyActorMaker.getButtonStartGame().setVisible(showStartNudgeButton);
            lobbyActorMaker.getButtonNudgeHost().setVisible(false);
            if(server.getMap().gameInitiated) {
                ((MyGdxGame) Gdx.app.getApplicationListener()).setScreen(new MainScreen(false, true, playerName, server, client));
            }
        } else {
            lobbyActorMaker.getButtonStartGame().setVisible(false);
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

    }
}
