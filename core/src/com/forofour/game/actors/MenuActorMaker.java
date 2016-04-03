/*This defines button actions of the menu screen*/

package com.forofour.game.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.forofour.game.MyGdxGame;
import com.forofour.game.handlers.GameConstants;
import com.forofour.game.screens.GameScreen;
import com.forofour.game.screens.LobbyScreen;

/**
 * Created by seanlim on 8/3/2016.
 */
public class MenuActorMaker {
    private Stage menuStage;
    private BitmapFont menuFont;
    private TextButton buttonQuick;
    private TextButton buttonHost;
    private TextButton buttonLogout;
    private TextButton buttonJoin;
    private TextButton buttonTutorial;
    private TextButton buttonSettings;

    private int BUTTON_WIDTH = 40;
    private int BUTTON_HEIGHT = 10;
    private int BUTTON_GAP = 5;

    public MenuActorMaker(Stage stage){
        //set the menu stage
        menuStage = stage;

        //create new skin for menu screen
        Skin menuSkin = new Skin();

        //load fonts
        Texture texture = new Texture(Gdx.files.internal("fonts/baskek.png"), true); // true enables mipmaps
        texture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear); // linear filtering in nearest mipmap image
        menuFont = new BitmapFont(Gdx.files.internal("fonts/baskek.fnt"),
                new TextureRegion(texture), false);
        menuFont.getData().setScale(0.15f);

        //set menu font
        menuSkin.add("menuFont", menuFont);

        //set menu buttons
        menuSkin.add("buttonUp", new Texture("basic/button_up.png"));
        menuSkin.add("buttonDown", new Texture("basic/button_down.png"));

        //create TextButton style
        TextButton.TextButtonStyle normal = new TextButton.TextButtonStyle();
        normal.font = menuSkin.getFont("menuFont");
        normal.up = menuSkin.getDrawable("buttonUp");
        normal.down = menuSkin.getDrawable("buttonDown");
        normal.pressedOffsetY = -1;

        //create menu buttons
        buttonTutorial = new TextButton("Tutorial", normal);
        buttonHost = new TextButton("Host Game", normal);
        buttonJoin = new TextButton("Join Game", normal);
        buttonSettings = new TextButton("Settings", normal);

        buttonTutorial.setSize(this.BUTTON_WIDTH, this.BUTTON_HEIGHT);
        buttonTutorial.setPosition(GameConstants.GAME_WIDTH / 2 - BUTTON_WIDTH / 2,
                (BUTTON_HEIGHT + BUTTON_GAP) * 4);
        buttonTutorial.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ((MyGdxGame) Gdx.app.getApplicationListener()).setScreen(new GameScreen());
            }
        });//on click --> change current screen to game screen

        buttonHost.setSize(this.BUTTON_WIDTH, this.BUTTON_HEIGHT);
        buttonHost.setPosition(GameConstants.GAME_WIDTH / 2 - BUTTON_WIDTH / 2,
                (BUTTON_HEIGHT + BUTTON_GAP) * 3);
        buttonHost.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ((MyGdxGame) Gdx.app.getApplicationListener()).setScreen(new LobbyScreen(true));
            }
        });

        buttonJoin.setSize(this.BUTTON_WIDTH, this.BUTTON_HEIGHT);
        buttonJoin.setPosition(GameConstants.GAME_WIDTH / 2 - BUTTON_WIDTH / 2,
                (BUTTON_HEIGHT + BUTTON_GAP) * 2);
        buttonJoin.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ((MyGdxGame) Gdx.app.getApplicationListener()).setScreen(new LobbyScreen(false));
            }
        });

        buttonSettings.setSize(this.BUTTON_WIDTH, this.BUTTON_HEIGHT);
        buttonSettings.setPosition(GameConstants.GAME_WIDTH / 2 - BUTTON_WIDTH / 2,
                (BUTTON_HEIGHT + BUTTON_GAP));
        buttonSettings.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

            }
        });

        stage.addActor(buttonTutorial);
        stage.addActor(buttonHost);
        stage.addActor(buttonJoin);
        stage.addActor(buttonSettings);
    }

    public TextButton getButtonSettings() {
        return buttonSettings;
    }

    public TextButton getButtonJoin() {
        return buttonJoin;
    }

    public TextButton getButtonHost() {
        return buttonHost;
    }

    public TextButton getButtonLogout() {
        return buttonLogout;
    }

    public TextButton getButtonQuick() {
        return buttonQuick;
    }

    public TextButton getButtonTutorial() {
        return buttonTutorial;
    }

    public Stage getMenuStage() {
        return menuStage;
    }
}
