package com.forofour.game.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.forofour.game.handlers.AssetLoader;
import com.forofour.game.handlers.GameConstants;

/**
 * Created by seanlim on 1/4/2016.
 */
public class LobbyActorMaker {
    private Stage lobbyStage;

    private static BitmapFont textFont;
    private static Label.LabelStyle textStyle;
    private static Label textLabel;

    private BitmapFont lobbyFont;
    private ImageButton.ImageButtonStyle startButtonStyle, nudgeButtonStyle;
    private ImageButton buttonStartGame, buttonNudgeHost;

    private Image lobbyBg;
    private Image playerRed1, playerRed2, playerBlue1, playerBlue2;

    private static final float PLAYER_ICON_SCALE = 1.15f;
    private int BUTTON_WIDTH = 40;
    private int BUTTON_HEIGHT = 10;
    private int BUTTON_GAP = 5;

    public LobbyActorMaker(Stage stage){
        //set the menu stage
        lobbyStage = stage;

        // Lobby Background image
        lobbyBg = new Image(new Texture(Gdx.files.internal("sprites/Design 2/Waiting screen/waiting-screen-2.png")));
        lobbyBg.setSize(GameConstants.GAME_WIDTH, GameConstants.GAME_HEIGHT);
        playerBlue1 = new Image(new Texture(Gdx.files.internal("sprites/Design 2/Waiting screen/blue-head-trimmed.png")));
        playerBlue1.setSize(24 * PLAYER_ICON_SCALE, 20 * PLAYER_ICON_SCALE);
        playerBlue1.setPosition(16, 7);
        playerBlue2 = new Image(new Texture(Gdx.files.internal("sprites/Design 2/Waiting screen/blue-head-trimmed.png")));
        playerBlue2.setSize(24 * PLAYER_ICON_SCALE, 20 * PLAYER_ICON_SCALE);
        playerBlue2.setPosition(16, 32);
        playerRed1 = new Image(new Texture(Gdx.files.internal("sprites/Design 2/Waiting screen/red-head-trimmed.png"))); // 24x20px
        playerRed1.setSize(24 * PLAYER_ICON_SCALE, 20 * PLAYER_ICON_SCALE);
        playerRed1.setPosition(GameConstants.GAME_WIDTH - 43, 27);
        playerRed2 = new Image(new Texture(Gdx.files.internal("sprites/Design 2/Waiting screen/red-head-trimmed.png")));
        playerRed2.setSize(24 * PLAYER_ICON_SCALE, 20 * PLAYER_ICON_SCALE);
        playerRed2.setPosition(GameConstants.GAME_WIDTH - 43, 51);

        //create new skin for menu screen
        Skin menuSkin = new Skin();

        //load fonts
        Texture texture = new Texture(Gdx.files.internal("fonts/baskek.png"), true); // true enables mipmaps
        texture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear); // linear filtering in nearest mipmap image
        lobbyFont = new BitmapFont(Gdx.files.internal("fonts/baskek.fnt"),
                new TextureRegion(texture), false);
        lobbyFont.getData().setScale(0.15f);

        //set menu font
        menuSkin.add("menuFont", lobbyFont);

        //set menu buttons
        menuSkin.add("buttonUp", new Texture("basic/button_up.png"));
        menuSkin.add("buttonDown", new Texture("basic/button_down.png"));

        //create TextButton style
        TextButton.TextButtonStyle normal = new TextButton.TextButtonStyle();
        normal.font = menuSkin.getFont("menuFont");
        normal.up = menuSkin.getDrawable("buttonUp");
        normal.down = menuSkin.getDrawable("buttonDown");
        normal.pressedOffsetY = -1;

        //Server side button to start game
        startButtonStyle = new ImageButton.ImageButtonStyle();
        startButtonStyle.up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("sprites/Design 2/Waiting screen/start-button.png"))));
        startButtonStyle.down = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("sprites/Design 2/Waiting screen/start-button-pressed.png"))));
        buttonStartGame = new ImageButton(startButtonStyle);

        // Client side button to get server to start game
        nudgeButtonStyle = new ImageButton.ImageButtonStyle();
        nudgeButtonStyle.up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("sprites/Design 2/Waiting screen/nudge-host-button.png"))));
        nudgeButtonStyle.down = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("sprites/Design 2/Waiting screen/nudge-host-button-pressed.png"))));
        buttonNudgeHost = new ImageButton(nudgeButtonStyle);

        buttonStartGame.setSize(this.BUTTON_WIDTH, this.BUTTON_HEIGHT);
        buttonStartGame.setPosition(GameConstants.GAME_WIDTH / 2 - BUTTON_WIDTH / 2,
                GameConstants.GAME_HEIGHT / 2 - BUTTON_HEIGHT / 2);

        buttonNudgeHost.setSize(this.BUTTON_WIDTH, this.BUTTON_HEIGHT);
        buttonNudgeHost.setPosition(GameConstants.GAME_WIDTH / 2 - BUTTON_WIDTH / 2,
                GameConstants.GAME_HEIGHT / 2 - BUTTON_HEIGHT / 2);

        stage.addActor(lobbyBg);
        stage.addActor(playerRed1);
        stage.addActor(playerRed2);
        stage.addActor(playerBlue1);
        stage.addActor(playerBlue2);
        stage.addActor(buttonStartGame);
        stage.addActor(buttonNudgeHost);

        playerRed1.setVisible(false);
        playerRed2.setVisible(false);
        playerBlue1.setVisible(false);
        playerBlue2.setVisible(false);
    }

    public Image getLobbyBg() {return lobbyBg;}

    public Image getPlayerBlue1() {
        return playerBlue1;
    }

    public Image getPlayerBlue2() {
        return playerBlue2;
    }

    public Image getPlayerRed1() {
        return playerRed1;
    }

    public Image getPlayerRed2() {
        return playerRed2;
    }

    public ImageButton getButtonNudgeHost() {
        return buttonNudgeHost;
    }

    public ImageButton getButtonStartGame() {
        return buttonStartGame;
    }


    public Stage getMenuStage() {
        return lobbyStage;
    }
}
