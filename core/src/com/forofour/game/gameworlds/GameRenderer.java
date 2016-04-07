/*This defines the main styles used in the game*/

package com.forofour.game.gameworlds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.forofour.game.gameobjects.Ball;
import com.forofour.game.gameobjects.Player;
import com.forofour.game.gameobjects.PowerUp;
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

    // Renderers -- debugRenderer for physics debugging, shapeRenderer for visual effect
    private Box2DDebugRenderer debugRenderer;
    //private ShapeRenderer shapeRenderer;

    private SpriteBatch batcher;

    // Game objects
    private Ball ball;
    private Player player;
    private Team teamA, teamB;
    private PowerUp powerUp;

    public GameRenderer(GameWorld world) {
        // Initialize the objects
        this.world = world;
        this.ball = world.getBall();
        this.player = world.getPlayer();
        this.teamA = world.getTeamA();
        this.teamB = world.getTeamB();
        this.powerUp = world.getPowerUp();

        cam = new OrthographicCamera();
        cam.setToOrtho(true, GameConstants.GAME_WIDTH, GameConstants.GAME_HEIGHT);

        // Sets the proportion of the entire map to view
        cam.viewportWidth = GameConstants.GAME_WIDTH * GameConstants.VIEW2MAP_RATIO;
        cam.viewportHeight = GameConstants.GAME_HEIGHT * GameConstants.VIEW2MAP_RATIO;

        camAdj = new CameraAdjustments(cam, player); // Helper to get XY coordinates of viewport

        // Renderers -- debugRenderer for physics debugging, shapeRenderer for visual effect
        debugRenderer = new Box2DDebugRenderer();
        //shapeRenderer = new ShapeRenderer();
        batcher = new SpriteBatch();

    }

    //reinitialization is required after new object is added
    public void reinitialize() {
        ball = world.getBall();
        player = world.getPlayer();
        teamA = world.getTeamA();
        teamB = world.getTeamB();
        camAdj.reinitialize(player);
    }

    public void render(float delta) {
        // Viewport follows player
        cam.position.set(camAdj.getPosition());
        cam.update();

        // Updated camera is used to render stuff
        //shapeRenderer.setProjectionMatrix(cam.combined);
        batcher.setProjectionMatrix(cam.combined);

        // Fill the entire screen with black, to prevent potential flickering.
        //Gdx.gl.glClearColor(0, 0, 0, 1);
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

        //switch between toss and boost buttons
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

        // Background Floor mat
        batcher.draw(AssetLoader.bgRegion, 0, 0, GameConstants.GAME_WIDTH, GameConstants.GAME_HEIGHT);

        // Team A
        for(Player p : teamA.getTeamList()) {
            //change the size of player
            float scale = 3f;

            //rotate player according to direction of movement
            float angle = p.getAngleDegree();
            TextureRegion playerDirection;
            if(angle >= 45 && angle < 135)
                playerDirection = AssetLoader.playerRegionAdown;
            else if(angle >= 135 && angle < 225)
                playerDirection = AssetLoader.playerRegionAleft;
            else if(angle >= 225 && angle < 315)
                playerDirection = AssetLoader.playerRegionAup;
            else
                playerDirection = AssetLoader.playerRegionAright;

            /*draw(TextureRegion region,
                float x,
                float y,
                float originX,
                float originY,
                float width,
                float height,
                float scaleX,
                float scaleY,
                float rotation)*/
            batcher.draw(playerDirection, // Texture
                    p.getBody().getPosition().x-0.5f,
                    p.getBody().getPosition().y-0.5f,
                    1,
                    1,
                    p.getRadius(), // width
                    p.getRadius(), // height
                    scale,
                    scale,
                    0);

        }

        // Team B
        for(Player p : teamB.getTeamList()) {
            //change the size of player
            float scale = 3f;

            //rotate player according to direction of movement
            float angle = p.getAngleDegree();
            TextureRegion playerDirection;
            if(angle >= 45 && angle < 135)
                playerDirection = AssetLoader.playerRegionBdown;
            else if(angle >= 135 && angle < 225)
                playerDirection = AssetLoader.playerRegionBleft;
            else if(angle >= 225 && angle < 315)
                playerDirection = AssetLoader.playerRegionBup;
            else
                playerDirection = AssetLoader.playerRegionBright;

            /*draw(TextureRegion region,
                float x,
                float y,
                float originX,
                float originY,
                float width,
                float height,
                float scaleX,
                float scaleY,
                float rotation)*/
            batcher.draw(playerDirection, // Texture
                    p.getBody().getPosition().x-0.5f,
                    p.getBody().getPosition().y-0.5f,
                    1f,
                    1f,
                    p.getRadius(), // width
                    p.getRadius(), // height
                    scale,
                    scale,
                    0);
        }

        // Ball
        if(ball != null) {
            batcher.draw(AssetLoader.ball,
                    ball.getBody().getPosition().x - ball.getRadius(),
                    ball.getBody().getPosition().y - ball.getRadius(),
                    ball.getRadius() * 3,
                    ball.getRadius() * 3);
        }

        // PowerUp
        if(!powerUp.isOutOfFrame()) {
            batcher.draw(AssetLoader.powerUp,
                    powerUp.getBody().getPosition().x - powerUp.getRadius(),
                    powerUp.getBody().getPosition().y - powerUp.getRadius(),
                    powerUp.getRadius() * 2,
                    powerUp.getRadius() * 2);
        }

        batcher.end();

    }

    public OrthographicCamera getCam(){
        return cam;
    }

    public SpriteBatch getBatch() {
        return batcher;
    }
}
