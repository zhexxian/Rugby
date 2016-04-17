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
 * Lobby Screen - called by MenuScreen
 *  if Tutorial mode, MainScreen will immediately be called
 *
 * Buttons are made with the helper class LobbyActorMaker. Button allows :
 *  Transition into Game(if host), Nudging the host(if Game has min. 2 players connected)
 *
 * All lobby screen will have an instance of GameClient
 * Only host will have an instance of GameServer
 *
 */
public class LobbyScreen implements Screen {

    private Stage stage;
    private LobbyActorMaker lobbyActorMaker;

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

    // Constructed in this manner if PlayAgain by Client is choosen at the EndGame
    public LobbyScreen(String hostname) {
        this(false, false);
        this.hostname = hostname;
    }
    public LobbyScreen(GameServer server) {
        this(false, true);
        this.server = server;
    }

    // Default constructor on first run
    public LobbyScreen(boolean tutorialMode, final boolean isHost) {
        this.isHost = isHost;
        this.tutorialMode = tutorialMode;
        this.playerName = "";
        this.hostname = "";

        // Stage is used to contain the Buttons
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

        defineButtonsFunction();
        hideButtons();
        tieConnectedPlayerToImage();

        Gdx.input.setInputProcessor(stage);
        Gdx.input.setCatchBackKey(true);
    }

    private void defineButtonsFunction(){
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
    }

    private void hideButtons(){
        buttonStartGame.setVisible(false);
        buttonNudgeHost.setVisible(false);
    }

    private void tieConnectedPlayerToImage() {
        player1 = lobbyActorMaker.getPlayerBlue1();
        player2 = lobbyActorMaker.getPlayerRed1();
        player3 = lobbyActorMaker.getPlayerBlue2();
        player4 = lobbyActorMaker.getPlayerRed2();
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

        showBabyFaces(client.getMap().getNumberOfBabyFaces());

        // Only if Desired number of Players are connection would the buttons be Active/Visible
        showStartNudgeButton = client.getMap().getNumberOfBabyFaces() >= GameConstants.MIN_MULTIPLAYER_NUMBER; // Should it be 2 or more?

        if(isHost) {
            // HOST allowed to StartGame
            buttonStartGame.setVisible(showStartNudgeButton);
            buttonNudgeHost.setVisible(false);

            // Starts instantly when Clients are have been initiated
            if(server.getMap().gameInitiated || tutorialMode) {
                ((MyGdxGame) Gdx.app.getApplicationListener()).setScreen(new MainScreen(tutorialMode, true, playerName, server, client));
            }
        } else {
            // CLIENT allowed to Nudge
            buttonStartGame.setVisible(false);
            buttonNudgeHost.setVisible(showStartNudgeButton);

            // Changes screen when game is initiated(aft. assignment of objects by server)
            if(client.getMap().gameInitiated) {
                ((MyGdxGame) Gdx.app.getApplicationListener()).setScreen(new MainScreen(false, false, playerName, null, client));
            }

            // Changes screen when server leaves the lobby
            if(client.getMap().shutdown) {
                if(client != null)
                    client.shutdown();
                ((MyGdxGame) Gdx.app.getApplicationListener()).setScreen(new MenuScreen());
            }
        }
    }

    // Trigger visibility of BabyImages depending on the numberplayers connected
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
        if(client != null)
            client.dispose();
        if(server != null)
            server.dispose();
    }
}
