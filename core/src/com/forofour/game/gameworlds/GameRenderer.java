package com.forofour.game.gameworlds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.forofour.game.gameobjects.Ball;
import com.forofour.game.gameobjects.Player;
import com.forofour.game.handlers.AssetLoader;

/**
 * Created by seanlim on 19/2/2016.
 */
public class GameRenderer {

    private GameWorld world;
    private OrthographicCamera cam;
    private Box2DDebugRenderer debugRenderer;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batcher;

    float gameWidth, gameHeight;

    // Game objects
    private Player player;
    private Ball ball;

    public GameRenderer(GameWorld world, float gameWidth, float gameHeight) {
        this.world = world;

        cam = new OrthographicCamera();
        cam.setToOrtho(true, gameWidth, gameHeight);

        debugRenderer = new Box2DDebugRenderer();

        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(cam.combined);

        batcher = new SpriteBatch();
        batcher.setProjectionMatrix(cam.combined);

        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;

        initGameObjects();
    }

    public void render(float delta) {
//        Gdx.app.log("GameRenderer", "render");

//        Fill the entire screen with black, to prevent potential flickering.
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        debugRenderer.render(world.getBox2d(), cam.combined);

        batcher.begin();

        // Backgound
//        batcher.draw(AssetLoader.bgRegion, 0, 0, gameWidth, gameHeight);

        // Player
        batcher.draw(AssetLoader.playerRegion, // Texture
                player.getBody().getPosition().x - player.getRadius(),
                player.getBody().getPosition().y - player.getRadius(),
                player.getRadius()*2, // width
                player.getRadius()*2); // height

        // Ball
        batcher.draw(AssetLoader.ball,
                ball.getBody().getPosition().x - ball.getRadius(),
                ball.getBody().getPosition().y - ball.getRadius(),
                ball.getRadius()*2,
                ball.getRadius()*2);

        batcher.end();

//        drawShapes();

    }

    private void drawShapes() {
        // Begin ShapeRenderer
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(Color.RED);
        shapeRenderer.circle(player.getBody().getPosition().x,
                player.getBody().getPosition().y, player.getRadius());
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLUE);

        shapeRenderer.circle(ball.getBody().getPosition().x, ball.getBody().getPosition().y,
                ball.getRadius());

        shapeRenderer.end();
    }

    private void initGameObjects() {
        ball = world.getBall();
        player = world.getPlayer();
    }

    public OrthographicCamera getCam(){
        return cam;
    }

    public SpriteBatch getBatch() {
        return batcher;
    }
}
