package com.forofour.game.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.forofour.game.handlers.GameConstants;

/**
 * Created by zhexian on 08-Apr-16.
 */
public class GameOverMaker {

    private static float SIZE_SCALE = 0.2f;

    private Stage endStage;
    private static BitmapFont textFont;
    private static Label.LabelStyle textStyle;
    private static Label textLabel;

    private static Drawable semiTranslucentBackground;

    private BitmapFont endFont;
    private TextButton buttonPlayAgain;
    private TextButton buttonMainMenu;

    private static int BUTTON_WIDTH = 40;
    private static int BUTTON_HEIGHT = 10;
//    private int BUTTON_GAP = 5;

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

        buttonPlayAgain.setDebug(true);
        buttonMainMenu.setDebug(true);

        // Size have to be done here
        buttonPlayAgain.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        buttonMainMenu.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);

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

    public static Container wrap1(TextButton ib) {
        // X,Y origin is at bottom left of the screen.
        float POS_X1 = GameConstants.GAME_WIDTH/2;
        float POS_Y1 = GameConstants.GAME_HEIGHT/2 + ib.getHeight();

        float scalePosX = Gdx.graphics.getWidth()/ GameConstants.GAME_WIDTH;
        float scalePosY = Gdx.graphics.getHeight()/ GameConstants.GAME_HEIGHT;

        Container wrapper = new Container(ib);
        wrapper.setTransform(true);

        wrapper.setPosition(POS_X1 * scalePosX, POS_Y1 * scalePosY);
        wrapper.setScale(SIZE_SCALE * scalePosX, SIZE_SCALE * scalePosY);

        return wrapper;
    }

    public static Container wrap2(TextButton ib) {
        // X,Y origin is at bottom left of the screen.
        float POS_X2 = GameConstants.GAME_WIDTH/2;
        float POS_Y2 = GameConstants.GAME_HEIGHT/2 - ib.getHeight();

        float scalePosX = Gdx.graphics.getWidth()/ GameConstants.GAME_WIDTH;
        float scalePosY = Gdx.graphics.getHeight()/ GameConstants.GAME_HEIGHT;

        Container wrapper = new Container(ib);
        wrapper.setTransform(true);

        wrapper.setPosition(POS_X2 * scalePosX, POS_Y2 * scalePosY);
        wrapper.setScale(SIZE_SCALE * scalePosX, SIZE_SCALE * scalePosY);

        return wrapper;
    }
}
