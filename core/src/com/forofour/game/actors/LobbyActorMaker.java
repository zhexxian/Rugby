package com.forofour.game.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.forofour.game.MyGdxGame;
import com.forofour.game.handlers.GameConstants;
import com.forofour.game.screens.GameScreen;
import com.forofour.game.screens.MainScreen;

/**
 * Created by seanlim on 1/4/2016.
 */
public class LobbyActorMaker {
    private Stage lobbyStage;

    private static BitmapFont textFont;
    private static Label.LabelStyle textStyle;
    private static Label textLabel;

    private BitmapFont lobbyFont;
    private TextButton buttonStartGame;
    private TextButton buttonNudgeHost;

    private int BUTTON_WIDTH = 40;
    private int BUTTON_HEIGHT = 10;
    private int BUTTON_GAP = 5;

    public LobbyActorMaker(Stage stage){
        //set the menu stage
        lobbyStage = stage;

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
        buttonStartGame = new TextButton("Start Game", normal);

        // Client side button to get server to start game
        buttonNudgeHost = new TextButton("Nudge Host", normal);

        buttonStartGame.setSize(this.BUTTON_WIDTH, this.BUTTON_HEIGHT);
        buttonStartGame.setPosition(GameConstants.GAME_WIDTH / 2 - BUTTON_WIDTH / 2,
                (BUTTON_HEIGHT + BUTTON_GAP) * 2);
/*        buttonStartGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ((MyGdxGame) Gdx.app.getApplicationListener()).setScreen(new MainScreen(false, true));
            }
        });*/

        buttonNudgeHost.setSize(this.BUTTON_WIDTH, this.BUTTON_HEIGHT);
        buttonNudgeHost.setPosition(GameConstants.GAME_WIDTH / 2 - BUTTON_WIDTH / 2,
                (BUTTON_HEIGHT + BUTTON_GAP) * 3);
/*        buttonNudgeHost.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

            }
        });*/

        stage.addActor(buttonStartGame);
        stage.addActor(buttonNudgeHost);
    }


    public TextButton getButtonNudgeHost() {
        return buttonNudgeHost;
    }

    public TextButton getButtonStartGame() {
        return buttonStartGame;
    }


    public Stage getMenuStage() {
        return lobbyStage;
    }
}
