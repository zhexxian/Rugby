package com.forofour.game.gameworlds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
import com.forofour.game.handlers.GameMap;

import java.util.ArrayList;

/**
 * Created by seanlim on 4/4/2016.
 */
public class MainRenderer {
    private GameMap map;
    private OrthographicCamera cam;
    private CameraAdjustments camAdj;

    // Renderers -- debugRenderer for physics debugging, shapeRenderer for visual effect
    private Box2DDebugRenderer debugRenderer;
    private SpriteBatch batcher;

    // Game objects
    private Ball ball;
    private Player player;
    private Team teamA, teamB;
    private ArrayList<PowerUp> powerUpList;

    private boolean initialized;

    public MainRenderer(GameMap map) {
        // Initialize the objects
        this.map = map;

        cam = new OrthographicCamera();
        cam.setToOrtho(true, GameConstants.GAME_WIDTH, GameConstants.GAME_HEIGHT);

        // Sets the proportion of the entire map to view
        cam.viewportWidth = GameConstants.GAME_WIDTH * GameConstants.VIEW2MAP_RATIO;
        cam.viewportHeight = GameConstants.GAME_HEIGHT * GameConstants.VIEW2MAP_RATIO;

        // Renderers -- debugRenderer for physics debugging, shapeRenderer for visual effect
        debugRenderer = new Box2DDebugRenderer();
        batcher = new SpriteBatch();

        initialized = false;
    }

    public void render(float delta) {
        if(!initialized){
            initialize();
        }

        // Fill the entire screen with black, to prevent potential flickering.
        //Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        debugRenderer.render(map.getBox2d(), cam.combined);

        if(initialized) {
            // Viewport follows player
            cam.position.set(camAdj.getPosition());
            cam.update();

            // Updated camera is used to render stuff
            batcher.setProjectionMatrix(cam.combined);
            drawSprites();
        }
    }

    private void drawSprites() {
        batcher.begin();

        // Background Floor mat
        batcher.draw(AssetLoader.bgRegion, 0, 0, GameConstants.GAME_WIDTH, GameConstants.GAME_HEIGHT);

        // Team A
        for(Player p : teamA.getTeamList()) {
            batcher.setColor(1,1,1,p.getInvisibility_scale(player.getId())); // PowerUp(Invisibility) Effect

            //change the size of player
            float scale = 3f;

            //rotate player according to direction of movement
            float angle = p.getLastDirection().angle();
//            Gdx.app.log("Player" + p.getId(), "Angle " + p.getLastDirection().angle());
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
            batcher.setColor(1, 1, 1, 1);
        }

        // Team B
        for(Player p : teamB.getTeamList()) {
            batcher.setColor(1,1,1,p.getInvisibility_scale(player.getId()));

            //change the size of player
            float scale = 3f;

            //rotate player according to direction of movement
            float angle = p.getLastDirection().angle();
//            Gdx.app.log("Player" + p.getId(), "Angle " + p.getLastDirection().angle());
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
            batcher.setColor(1,1,1,1);
        }

        // Ball
        if(ball != null) {
            if(ball.isHeld()) { // If the ball holder is invisible, ditto!
                batcher.setColor(1,1,1,ball.getHoldingPlayer().getInvisibility_scale(player.getId()));
            }

            batcher.draw(AssetLoader.ball,
                    ball.getBody().getPosition().x - ball.getRadius(),
                    ball.getBody().getPosition().y - ball.getRadius(),
                    ball.getRadius() * 3,
                    ball.getRadius() * 3);
            batcher.setColor(1, 1, 1, 1);
        }

        // PowerUps
        for(PowerUp powerUp : powerUpList) {
            if (!powerUp.isOutOfFrame()) {
                batcher.draw(AssetLoader.powerUp,
                        powerUp.getBody().getPosition().x - powerUp.getRadius(),
                        powerUp.getBody().getPosition().y - powerUp.getRadius(),
                        powerUp.getRadius() * 2,
                        powerUp.getRadius() * 2);
            }
        }

        batcher.end();
    }

    public void initialize(){
        ball = map.getBall();
        player = map.getPlayer();
        if(player != null) {
            camAdj = new CameraAdjustments(cam, player); // Helper to get XY coordinates of viewport
        }
        powerUpList = map.getPowerUpList();
        teamA = map.getTeamA();
        teamB = map.getTeamB();

        if(ball != null && player != null && powerUpList != null &&
                teamA != null && teamB != null) {
            initialized = true;
        }
    }
}
