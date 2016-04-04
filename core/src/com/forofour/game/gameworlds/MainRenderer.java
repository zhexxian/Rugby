package com.forofour.game.gameworlds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.forofour.game.handlers.CameraAdjustments;
import com.forofour.game.handlers.GameConstants;
import com.forofour.game.handlers.GameMap;

/**
 * Created by seanlim on 4/4/2016.
 */
public class MainRenderer {
    private GameMap map;
    private OrthographicCamera cam;
//    private CameraAdjustments camAdj;

    // Renderers -- debugRenderer for physics debugging, shapeRenderer for visual effect
    private Box2DDebugRenderer debugRenderer;


    public MainRenderer(GameMap map) {
        // Initialize the objects
        this.map = map;

        cam = new OrthographicCamera();
        cam.setToOrtho(true, GameConstants.GAME_WIDTH, GameConstants.GAME_HEIGHT);

        // Sets the proportion of the entire map to view
        cam.viewportWidth = GameConstants.GAME_WIDTH * GameConstants.VIEW2MAP_RATIO;
        cam.viewportHeight = GameConstants.GAME_HEIGHT * GameConstants.VIEW2MAP_RATIO;

//        camAdj = new CameraAdjustments(cam, player); // Helper to get XY coordinates of viewport

        // Renderers -- debugRenderer for physics debugging, shapeRenderer for visual effect
        debugRenderer = new Box2DDebugRenderer();
    }

    public void render(float delta) {
        // Fill the entire screen with black, to prevent potential flickering.
        //Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        debugRenderer.render(map.getBox2d(), cam.combined);
    }
}
