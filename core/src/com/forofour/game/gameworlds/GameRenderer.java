package com.forofour.game.gameworlds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.forofour.game.gameobjects.Ball;
import com.forofour.game.gameobjects.Player;

/**
 * Created by seanlim on 19/2/2016.
 */
public class GameRenderer {

    private GameWorld myWorld;
    private OrthographicCamera cam;
    private ShapeRenderer shapeRenderer;

    // Game objects
    private Player player;
    private Ball ball;

    public GameRenderer(GameWorld world, float gameWidth, float gameHeight) {
        this.myWorld = world;

        cam = new OrthographicCamera();
        cam.setToOrtho(true, gameWidth, gameHeight);

        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(cam.combined);

        initGameObjects();
    }

    public void render(float delta) {
        Gdx.app.log("GameRenderer", "render");

        // Fill the entire screen with black, to prevent potential flickering.
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Begin ShapeRenderer
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(Color.RED);
        shapeRenderer.circle(player.getBoundingCircle().x,
                player.getBoundingCircle().y, player.getBoundingCircle().radius);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLUE);
        shapeRenderer.circle(ball.getBoundingCircle().x,
                ball.getBoundingCircle().y, ball.getBoundingCircle().radius);

        shapeRenderer.end();

    }

    private void initGameObjects() {
        ball = myWorld.getBall();
        player = myWorld.getPlayer();

    }
}
