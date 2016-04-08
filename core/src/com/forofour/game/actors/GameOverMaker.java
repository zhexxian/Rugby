package com.forofour.game.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.forofour.game.handlers.GameConstants;

/**
 * Created by zhexian on 08-Apr-16.
 */
public class GameOverMaker {

    private Stage endStage;
    private static BitmapFont textFont;
    private static Label.LabelStyle textStyle;
    private static Label textLabel;

    private static Drawable semiTranslucentBackground;

    private BitmapFont endFont;
    private TextButton buttonPlayAgain;
    private TextButton buttonMainMenu;

    private int BUTTON_WIDTH = 40;
    private int BUTTON_HEIGHT = 10;
    private int BUTTON_GAP = 5;

    public GameOverMaker(Stage stage){
        //set the game end stage
        endStage = stage;

        //create new skin for menu screen
        Skin endSkin = new Skin();

        //load fonts
        Texture texture = new Texture(Gdx.files.internal("fonts/baskek.png"), true); // true enables mipmaps
        texture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear); // linear filtering in nearest mipmap image
        endFont = new BitmapFont(Gdx.files.internal("fonts/baskek.fnt"),
                new TextureRegion(texture), false);
        endFont.getData().setScale(0.15f);

        //set game end font
        endSkin.add("menuFont", endFont);

        //set game end buttons
        endSkin.add("buttonUp", new Texture("basic/button_up.png"));
        endSkin.add("buttonDown", new Texture("basic/button_down.png"));

        //create TextButton style
        TextButton.TextButtonStyle normal = new TextButton.TextButtonStyle();
        normal.font = endSkin.getFont("menuFont");
        normal.up = endSkin.getDrawable("buttonUp");
        normal.down = endSkin.getDrawable("buttonDown");
        normal.pressedOffsetY = -1;

        //rematch button
        buttonPlayAgain = new TextButton("Play Again", normal);

        //back to main menu button
        buttonMainMenu = new TextButton("Main Menu", normal);

        buttonPlayAgain.setSize(this.BUTTON_WIDTH, this.BUTTON_HEIGHT);
        buttonPlayAgain.setPosition(GameConstants.GAME_WIDTH / 2 - BUTTON_WIDTH / 2,
                GameConstants.GAME_HEIGHT / 2 - BUTTON_HEIGHT / 2);
/*        buttonStartGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ((MyGdxGame) Gdx.app.getApplicationListener()).setScreen(new MainScreen(false, true));
            }
        });*/

        buttonMainMenu.setSize(this.BUTTON_WIDTH, this.BUTTON_HEIGHT);
        buttonMainMenu.setPosition(GameConstants.GAME_WIDTH / 2 - BUTTON_WIDTH / 2,
                GameConstants.GAME_HEIGHT / 2 - BUTTON_HEIGHT / 2);
/*        buttonNudgeHost.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

            }
        });*/

        stage.addActor(buttonPlayAgain);
        stage.addActor(buttonMainMenu);

        //Set background image
        endSkin.add("touchBackground", new Texture("sprites/buttons/joystick-out-black.png"));
        //Create Drawable's from TouchPad skin
        semiTranslucentBackground = endSkin.getDrawable("touchBackground");
    }


    public TextButton getButtonPlayAgain() {
        return buttonPlayAgain;
    }

    public TextButton getButtonMainMenu() {
        return buttonMainMenu;
    }

    //change return type later; set to void for now
    public void getYouWinText(){

    }

    //change return type later; set to void for now
    public void getYouLoseText(){

    }

    //change return type later; set to void for now
    public void getSemiTranslucentBackground(){

    }


    public Stage getMenuStage() {
        return endStage;
    }
}
