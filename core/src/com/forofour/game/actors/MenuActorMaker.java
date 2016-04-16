/*This defines button actions of the menu screen*/

package com.forofour.game.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.forofour.game.MyGdxGame;
import com.forofour.game.handlers.AssetLoader;
import com.forofour.game.handlers.GameConstants;
import com.forofour.game.screens.LobbyScreen;

/**
 * Created by seanlim on 8/3/2016.
 */
public class MenuActorMaker {
    private Stage menuStage;
    private BitmapFont menuFont;
    private ImageButton.ImageButtonStyle hostButtonStyle, joinButtonStyle, tutorialButtonStyle, volumeButtonStyle;
    private ImageButton buttonHost, buttonJoin, buttonTutorial, buttonVolume;

    private int BUTTON_WIDTH = 40;
    private int BUTTON_HEIGHT = 15;
    private int BUTTON_GAP = 2;

    public MenuActorMaker(Stage stage){
        //set the menu stage
        menuStage = stage;

        //create menu buttons
        tutorialButtonStyle = new ImageButton.ImageButtonStyle();
        tutorialButtonStyle.up = new TextureRegionDrawable(AssetLoader.tutorialRegionUp);
        tutorialButtonStyle.down = new TextureRegionDrawable(AssetLoader.tutorialRegionDown);
        buttonTutorial = new ImageButton(tutorialButtonStyle);

        hostButtonStyle = new ImageButton.ImageButtonStyle();
        hostButtonStyle.up = new TextureRegionDrawable(AssetLoader.hostGameRegionUp);
        hostButtonStyle.down = new TextureRegionDrawable(AssetLoader.hostGameRegionDown);
        buttonHost = new ImageButton(hostButtonStyle);

        joinButtonStyle = new ImageButton.ImageButtonStyle();
        joinButtonStyle.up = new TextureRegionDrawable(AssetLoader.joinGameRegionUp);
        joinButtonStyle.down = new TextureRegionDrawable(AssetLoader.joinGameRegionDown);
        buttonJoin = new ImageButton(joinButtonStyle);
//        buttonSettings = new TextButton("Settings", normal);

        buttonTutorial.setSize(this.BUTTON_WIDTH, this.BUTTON_HEIGHT);
        buttonTutorial.setPosition(GameConstants.GAME_WIDTH / 2 - BUTTON_WIDTH / 2,
                (BUTTON_HEIGHT + BUTTON_GAP) * 3);
        buttonTutorial.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ((MyGdxGame) Gdx.app.getApplicationListener()).setScreen(new LobbyScreen(true, true)); // Tytorial Mode, isHost
            }
        });//on click --> change current screen to game screen

        buttonHost.setSize(this.BUTTON_WIDTH, this.BUTTON_HEIGHT);
        buttonHost.setPosition(GameConstants.GAME_WIDTH / 2 - BUTTON_WIDTH / 2,
                (BUTTON_HEIGHT + BUTTON_GAP) * 2);
        buttonHost.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ((MyGdxGame) Gdx.app.getApplicationListener()).setScreen(new LobbyScreen(false, true));
            }
        });

        buttonJoin.setSize(this.BUTTON_WIDTH, this.BUTTON_HEIGHT);
        buttonJoin.setPosition(GameConstants.GAME_WIDTH / 2 - BUTTON_WIDTH / 2,
                (BUTTON_HEIGHT + BUTTON_GAP));
        buttonJoin.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ((MyGdxGame) Gdx.app.getApplicationListener()).setScreen(new LobbyScreen(false, false));
            }
        });

/*        buttonSettings.setSize(this.BUTTON_WIDTH, this.BUTTON_HEIGHT);
        buttonSettings.setPosition(GameConstants.GAME_WIDTH / 2 - BUTTON_WIDTH / 2,
                (BUTTON_HEIGHT + BUTTON_GAP));
        buttonSettings.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

            }
        });*/

        stage.addActor(buttonTutorial);
        stage.addActor(buttonHost);
        stage.addActor(buttonJoin);
//        stage.addActor(buttonSettings);
    }

    public ImageButton getButtonJoin() {
        return buttonJoin;
    }

    public ImageButton getButtonHost() {
        return buttonHost;
    }

    public ImageButton getButtonTutorial() {
        return buttonTutorial;
    }

    public Stage getMenuStage() {
        return menuStage;
    }
}
