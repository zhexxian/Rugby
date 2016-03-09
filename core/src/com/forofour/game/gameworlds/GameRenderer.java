package com.forofour.game.gameworlds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.forofour.game.gameobjects.Ball;
import com.forofour.game.gameobjects.Player;
import com.forofour.game.handlers.AssetLoader;
import com.forofour.game.handlers.CameraAdjustments;
import com.forofour.game.handlers.GameConstants;


/**
 * Created by seanlim on 19/2/2016.
 */
public class GameRenderer {

    private GameWorld world;
    private OrthographicCamera cam;
    private CameraAdjustments camAdj;
    private BitmapFont timerFont;

    // Renderers
    private Box2DDebugRenderer debugRenderer;
    //private ShapeRenderer shapeRenderer;
    private SpriteBatch batcher;

    // Game objects
    private Player player;
    private Ball ball;

    public GameRenderer(GameWorld world) {
        this.world = world;
        initGameObjects(); // Upon receiving the world

        cam = new OrthographicCamera();
        cam.setToOrtho(true, GameConstants.GAME_WIDTH, GameConstants.GAME_HEIGHT);

        // Sets the proportion of the entire map to view
        cam.viewportWidth = GameConstants.GAME_WIDTH * GameConstants.VIEW2MAP_RATIO;
        cam.viewportHeight = GameConstants.GAME_HEIGHT * GameConstants.VIEW2MAP_RATIO;

        camAdj = new CameraAdjustments(cam, player); // Helper to get XY coordinates of viewport

        timerFont = new BitmapFont(true);

        debugRenderer = new Box2DDebugRenderer();
        //shapeRenderer = new ShapeRenderer();
        batcher = new SpriteBatch();


    }

    public void render(float delta) {
        //        Gdx.app.log("GameRenderer", "render");

        // Viewport follows player
        cam.position.set(camAdj.getPosition());
        cam.update();

        // Updated camera is used to render stuff
        //shapeRenderer.setProjectionMatrix(cam.combined);
        batcher.setProjectionMatrix(cam.combined);

        // Fill the entire screen with black, to prevent potential flickering.
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        debugRenderer.render(world.getBox2d(), cam.combined);
        //drawShapes();
        drawSprites();


        // Update stage buttons visibility states if necessary
        renderButtons();


    }

    private void renderButtons(){
        // Buttons are rendered as the top most layer within stage as actors
        // Only require to trigger the visibility states
        if(world.getPlayer().hasBall()) {
            world.getTossButton().setVisible(true);
            world.getBoostButton().setVisible(false);
        } else {
            world.getTossButton().setVisible(false);
            world.getBoostButton().setVisible(true);
        }
    }

    private void drawSprites() {
        batcher.begin();

        // Player
        batcher.draw(AssetLoader.playerRegion, // Texture
                player.getBody().getPosition().x - player.getRadius(),
                player.getBody().getPosition().y - player.getRadius(),
                player.getRadius() * 2, // width
                player.getRadius() * 2); // height

        // Ball
        batcher.draw(AssetLoader.ball,
                ball.getBody().getPosition().x - ball.getRadius(),
                ball.getBody().getPosition().y - ball.getRadius(),
                ball.getRadius() * 2,
                ball.getRadius() * 2);

        // Timer


        batcher.draw(AssetLoader.bgRegion, 0, GameConstants.GAME_HEIGHT - 42, GameConstants.GAME_WIDTH - 42, 42);
        timerFont.draw(batcher, world.getTimer().getElapsed(), 10.0f, GameConstants.GAME_HEIGHT - 80);

        batcher.end();

    }

/*    private void drawShapes() {
        // Begin ShapeRenderer
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(Color.RED);
        shapeRenderer.circle(player.getBody().getPosition().x * GameConstants.VIEW2MAP_RATIO,
                player.getBody().getPosition().y, player.getRadius());
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLUE);

        shapeRenderer.circle(ball.getBody().getPosition().x * GameConstants.VIEW2MAP_RATIO,
                ball.getBody().getPosition().y,
                ball.getRadius());

        shapeRenderer.end();
    }*/

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
