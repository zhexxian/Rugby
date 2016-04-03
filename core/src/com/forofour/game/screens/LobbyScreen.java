package com.forofour.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import com.forofour.game.actors.LobbyActorMaker;
import com.forofour.game.actors.MenuActorMaker;
import com.forofour.game.handlers.GameConstants;
import com.forofour.game.net.GameClient;
import com.forofour.game.net.GameServer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by seanlim on 1/4/2016.
 */
public class LobbyScreen implements Screen {

    private Stage stage;
    private LobbyActorMaker lobbyActorMaker;
    private SpriteBatch batch;

    private boolean isHost;

    private Map<Integer,Boolean> playersConnected = new HashMap<Integer, Boolean>();
    private boolean lobbyFilled = false;

    public LobbyScreen(boolean isHost) {
        this.isHost = isHost;

        stage = new Stage(new ExtendViewport(
                GameConstants.GAME_WIDTH,
                GameConstants.GAME_HEIGHT));

        lobbyActorMaker = new LobbyActorMaker(stage);
        Gdx.input.setInputProcessor(stage);
        batch = new SpriteBatch();
    }

    @Override
    public void show() {

        GameConstants.client = new GameClient();

        if(isHost){
            Log.info("Starting server..");

            GameConstants.server = new GameServer(this);

            GameConstants.client.connect("localhost");
        } else {
            GameConstants.client.connect("localhost");
        }
    }

    public void startGame() {

    }

    public boolean addConnection(int id){
        if(!lobbyFilled) {
            System.out.println(id + " New player join lobby!");
            playersConnected.put(id, true);
            checkReady();
            return true; // Add connection successful
        }
        return false; // Add connection failed
    }

    public void dropConnection(int id) {
        playersConnected.remove(id);
    }

    public void checkReady() {
        if(playersConnected.size() == 4) {
            lobbyFilled = true;
            System.out.println("Host is ready to start game!");
        }
    }

    public Map<Integer, Boolean> getPlayersConnected() {
        return playersConnected;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
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
