package com.forofour.game.gameworlds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.forofour.game.gameobjects.Ball;
import com.forofour.game.gameobjects.Player;
import com.forofour.game.gameobjects.Team;
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
//    private BitmapFont timerFont;

    // Renderers
    private Box2DDebugRenderer debugRenderer;
    //private ShapeRenderer shapeRenderer;
    private SpriteBatch batcher;

    // Game objects
    private Ball ball;
    private Player player;
    private Team teamA, teamB;

    public GameRenderer(GameWorld world) {
        // Initialize the objects
        this.world = world;
        this.ball = world.getBall();
        this.player = world.getPlayer();
        this.teamA = world.getTeamA();
        this.teamB = world.getTeamB();

        cam = new OrthographicCamera();
        cam.setToOrtho(true, GameConstants.GAME_WIDTH, GameConstants.GAME_HEIGHT);

        // Sets the proportion of the entire map to view
        cam.viewportWidth = GameConstants.GAME_WIDTH * GameConstants.VIEW2MAP_RATIO;
        cam.viewportHeight = GameConstants.GAME_HEIGHT * GameConstants.VIEW2MAP_RATIO;

        camAdj = new CameraAdjustments(cam, player); // Helper to get XY coordinates of viewport

//        timerFont = new BitmapFont(true);
//        timerFont.getData().setScale(0.25f);

        debugRenderer = new Box2DDebugRenderer();
        //shapeRenderer = new ShapeRenderer();
        batcher = new SpriteBatch();

    }

    public void reinitialize() {
        ball = world.getBall();
        player = world.getPlayer();
        teamA = world.getTeamA();
        teamB = world.getTeamB();
        camAdj.reinitialize(player);
    }

    public void render(float delta) {
        //Gdx.app.log("GameRenderer", "render");

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
        if(player != null) {
            if(player.hasBall()) {
                world.getTossButton().setVisible(true);
                world.getBoostButton().setVisible(false);
            } else {
                world.getTossButton().setVisible(false);
                world.getBoostButton().setVisible(true);
            }
        }
        else {
            world.getTossButton().setVisible(false);
            world.getBoostButton().setVisible(false);
        }


    }

    private void drawSprites() {
        batcher.begin();

        // Team A
        for(Player p : teamA.getTeamList()) {
            batcher.draw(AssetLoader.playerRegionA, // Texture
                    p.getBody().getPosition().x - p.getRadius(),
                    p.getBody().getPosition().y - p.getRadius(),
                    p.getRadius(),
                    p.getRadius(),
                    p.getRadius() * 2, // width
                    p.getRadius() * 2, // height
                    1f, 1f,
                    p.getLastDirection().angle());

        }

        // Team B
        for(Player p : teamB.getTeamList()) {
            batcher.draw(AssetLoader.playerRegionB,
                    p.getBody().getPosition().x - p.getRadius(),
                    p.getBody().getPosition().y - p.getRadius(),
                    p.getRadius(),
                    p.getRadius(),
                    p.getRadius() * 2,
                    p.getRadius() * 2,
                    1f, 1f,
                    p.getLastDirection().angle());
        }

        // Ball
        if(ball != null) {
            batcher.draw(AssetLoader.ball,
                    ball.getBody().getPosition().x - ball.getRadius(),
                    ball.getBody().getPosition().y - ball.getRadius(),
                    ball.getRadius() * 2,
                    ball.getRadius() * 2);
        }

        // Timer
        //timerFont.draw(batcher, world.getGlobalTime().getElapsed(), 10.0f, GameConstants.GAME_HEIGHT - 80);
        batcher.end();

    }

    public OrthographicCamera getCam(){
        return cam;
    }

    public SpriteBatch getBatch() {
        return batcher;
    }
}
