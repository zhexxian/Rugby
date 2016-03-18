package com.forofour.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.forofour.game.actors.MenuActorMaker;
import com.forofour.game.handlers.GameConstants;

/**
 * Created by seanlim on 8/3/2016.
 */
public class MenuScreen implements Screen {
    private Stage stage;
    private MenuActorMaker actorMaker;

    public MenuScreen(){
        stage = new Stage(new ExtendViewport(
                GameConstants.GAME_WIDTH,
                GameConstants.GAME_HEIGHT));
        actorMaker = new MenuActorMaker(stage);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.log("GameScreen", String.format("resizing (%d, %d)",
                width, height));
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
