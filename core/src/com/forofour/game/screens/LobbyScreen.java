package com.forofour.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.forofour.game.MyGdxGame;
import com.forofour.game.actors.LobbyActorMaker;
import com.forofour.game.handlers.AssetLoader;
import com.forofour.game.handlers.GameConstants;
import com.forofour.game.net.GameClient;
import com.forofour.game.net.GameServer;
import com.forofour.game.net.Network;

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

    private String playerName; // Not implemented
    private String hostname;

    private ImageButton buttonStartGame;
    private ImageButton buttonNudgeHost;
    boolean showStartNudgeButton;

    private Image player1, player2, player3, player4;

    public LobbyScreen(String hostname) {
        this(false, false);
        this.hostname = hostname;
    }
    public LobbyScreen(GameServer server) {
        this(false, true);
        this.server = server;
    }

    public LobbyScreen(boolean tutorialMode, final boolean isHost) {
        this.isHost = isHost;
        this.tutorialMode = tutorialMode;
        this.playerName = "";
        this.hostname = "";

        stage = new Stage(new ExtendViewport(
                GameConstants.GAME_WIDTH,
                GameConstants.GAME_HEIGHT)) {
            @Override
            public boolean keyDown(int keyCode){
                if (keyCode == Input.Keys.BACK || keyCode == Input.Keys.BACKSPACE) {
                    if(server != null) {
                        server.shutdown(true);
                    }
                    if(client != null)
                        client.shutdown();
                    ((MyGdxGame) Gdx.app.getApplicationListener()).setScreen(new MenuScreen());
                }
                return super.keyDown(keyCode);
            }
        };

        lobbyActorMaker = new LobbyActorMaker(stage);
        buttonStartGame = lobbyActorMaker.getButtonStartGame();
        buttonNudgeHost = lobbyActorMaker.getButtonNudgeHost();
        buttonStartGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("StartButton", "Clicked!");
                if (isHost) {
                    server.sendMessage(new Network.PacketInitRound(true));
                    Gdx.app.log("StartButton", "PacketInitRound(true) sent");
                }
            }
        });
        buttonNudgeHost.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("NudgeButton", "Clicked!");
                client.sendMessage(new Network.PacketNudge());
                AssetLoader.nudgeSound.play(GameConstants.SOUND_VOLUME);
            }
        });
        buttonStartGame.setVisible(false);
        buttonNudgeHost.setVisible(false);

        player1 = lobbyActorMaker.getPlayerBlue1();
        player2 = lobbyActorMaker.getPlayerRed1();
        player3 = lobbyActorMaker.getPlayerBlue2();
        player4 = lobbyActorMaker.getPlayerRed2();

        batch = new SpriteBatch();

        Gdx.input.setInputProcessor(stage);
        Gdx.input.setCatchBackKey(true);

    }

    @Override
    public void show(){

        client = new GameClient(tutorialMode); // Launch a new client

        if(isHost){
            if(server == null)
                server = new GameServer(tutorialMode);
            Gdx.app.log("LobbyScreen(host)", "Started server");
            client.connect("localhost");
            Gdx.app.log("LobbyScreen(host)", "Client connected to server");
        }

        else {
            if(hostname != ""){
                client.connect(hostname);
            }
            else {
                if(!client.quickConnect())
                    client.getMap().shutdown = true;
            }
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
//        batch.draw();
        showBabyFaces(client.getMap().getNumberOfBabyFaces());

//        for(int i=0; i<client.getMap().getNumberOfBabyFaces(); i++){
//            batch.draw(AssetLoader.powerUp,
//                    50+i*100, 50, 50, 50);
//        }
        batch.end();

        // Only if Desired number of Players are connection would the buttons be Active/Visible
        showStartNudgeButton = client.getMap().getNumberOfBabyFaces() >= GameConstants.MIN_MULTIPLAYER_NUMBER; // Should it be 2 or more?
        if(isHost) {
            // Shows
            buttonStartGame.setVisible(showStartNudgeButton);
            buttonNudgeHost.setVisible(false);
            if(server.getMap().gameInitiated || tutorialMode) {
                ((MyGdxGame) Gdx.app.getApplicationListener()).setScreen(new MainScreen(tutorialMode, true, playerName, server, client));
            }
        } else {
            buttonStartGame.setVisible(false);
            buttonNudgeHost.setVisible(showStartNudgeButton);
            if(client.getMap().gameInitiated) {
                ((MyGdxGame) Gdx.app.getApplicationListener()).setScreen(new MainScreen(false, false, playerName, null, client));
            }
            if(client.getMap().shutdown) {
                if(client != null)
                    client.shutdown();
                ((MyGdxGame) Gdx.app.getApplicationListener()).setScreen(new MenuScreen());
            }
        }
    }

    private void showBabyFaces(int numberOfBabyFaces) {
        switch (numberOfBabyFaces) {
            case 1:
                player1.setVisible(true);
                player2.setVisible(false);
                player3.setVisible(false);
                player4.setVisible(false);
                break;
            case 2:
                player1.setVisible(true);
                player2.setVisible(true);
                player3.setVisible(false);
                player4.setVisible(false);
                break;
            case 3:
                player1.setVisible(true);
                player2.setVisible(true);
                player3.setVisible(true);
                player4.setVisible(false);
                break;
            case 4:
                player1.setVisible(true);
                player2.setVisible(true);
                player3.setVisible(true);
                player4.setVisible(true);
                break;
            default:
                break;
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
