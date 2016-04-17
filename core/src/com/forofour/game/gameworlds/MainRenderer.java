package com.forofour.game.gameworlds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
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
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by seanlim on 4/4/2016.
 */
public class MainRenderer {
    private GameMap map;
    private OrthographicCamera cam;
    private CameraAdjustments camAdj;

    private float runTime;

    // Renderers -- debugRenderer for physics debugging, shapeRenderer for visual effect
    private Box2DDebugRenderer debugRenderer;
    private SpriteBatch batcher;

    // Game objects
    private Ball ball;
    private Player player;
    private Team teamA, teamB;
    private CopyOnWriteArrayList<PowerUp> powerUpList;

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
        runTime = 0;
    }

    public void render(float delta) {
        runTime += delta;
        if(!initialized){
            initialize();
        }


        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Necessary to clear to prevent potential flickering
        Gdx.gl.glClearColor(0, 0, 0, 1); // Fill the entire screen with black, to prevent potential flickering.
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
//        batcher.draw(AssetLoader.bgRegion2, 0, 0, GameConstants.GAME_WIDTH, GameConstants.GAME_HEIGHT); // Test WoodenPlanks

        renderTeamA();
        renderTeamB();

        renderBall();
        renderPowerUps();

        batcher.draw(AssetLoader.bgTrainRegion, 0, 0, GameConstants.GAME_WIDTH, GameConstants.GAME_HEIGHT);

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

    public void renderTeamA() {
        // Team A
        // For each player the render should be in the following sequence
        // 1. Invisibility (It cloaks the entire player + effects)
        // 2. Slow (Bottoms most layer)
        // 3. Player in the respective direction
        // 4. Confusion (Top most layer)

        for(Player p : teamA.getTeamList()) {
            // Invisibility effect
            batcher.setColor(1,1,1,p.getInvisibility_scale(player.getId())); // PowerUp(Invisibility) Effect

            // Slowed effect
            if(p.isSlowed()) {
                batcher.draw(AssetLoader.powerUpEffectRegion1, // Texture
                        p.getBody().getPosition().x,
                        p.getBody().getPosition().y,
                        p.getRadius()-0.5f, // Offset would be the difference of radius from 2
                        -p.getRadius()/5,
                        p.getRadius(), // width
                        p.getRadius()/1.5f, // height
                        p.getRadius(), // scale
                        p.getRadius(), // scale
                        0);
            }


            //rotate player according to direction of movement
            float angle = p.getAngleDegree();
//            Gdx.app.log("Player" + p.getId(), "Angle " + angle);
            TextureRegion playerDirection;

            if(!p.getBody().getLinearVelocity().epsilonEquals(Vector2.Zero, 0.1f)) {
                // TODO: Tie FrameDuration to playerVelocity
                if(p.isBoosting()) {
                    for(Animation animation : AssetLoader.TeamAnimationA){
                        animation.setFrameDuration(0.08f); // Faster animation when boosting
                    }
                }
                else {
                    for(Animation animation : AssetLoader.TeamAnimationA){
                        animation.setFrameDuration(0.16f); // Slow animation when boosting
                    }
                }

                if (angle <= 135 && angle > 45)
                    playerDirection = AssetLoader.playerAnimationDownA.getKeyFrame(runTime);
                else if (angle <= 45 && angle > -45)
                    playerDirection = AssetLoader.playerAnimationRightA.getKeyFrame(runTime);
                else if (angle <= -45 && angle > -135)
                    playerDirection = AssetLoader.playerAnimationUpA.getKeyFrame(runTime);
                else
                    playerDirection = AssetLoader.playerAnimationLeftA.getKeyFrame(runTime);
            }
            else {
                if (angle <= 135 && angle > 45)
                    playerDirection = AssetLoader.playerRegionAdown1;
                else if (angle <= 45 && angle > -45)
                    playerDirection = AssetLoader.playerRegionAright1;
                else if (angle <= -45 && angle > -135)
                    playerDirection = AssetLoader.playerRegionAup1;
                else
                    playerDirection = AssetLoader.playerRegionAleft1;
            }

            // As render begins from TopLeft of body's position, Offset by the radius is required.
            batcher.draw(playerDirection, // Texture
                    p.getBody().getPosition().x,
                    p.getBody().getPosition().y,
                    p.getRadius()-0.5f, // Offset would be the difference of radius from 2
                    p.getRadius()-0.5f,
                    p.getRadius(), // width
                    p.getRadius(), // height
                    p.getRadius(), // scale
                    p.getRadius(), // scale
                    0);

//            System.out.println(GameConstants.SCALE_POS + " " + GameConstants.RENDER_RADIUS_SCALE + " " + p.getRadius());

            // Confused Effect
            if(p.isConfused()) {
                batcher.draw(AssetLoader.powerUpEffectRegion2, // Texture
                        p.getBody().getPosition().x,
                        p.getBody().getPosition().y,
                        p.getRadius()-0.5f, // Offset would be the difference of radius from 2
                        p.getRadius()*2-0.5f,
                        p.getRadius(), // width
                        p.getRadius(), // height
                        p.getRadius(), // scale
                        p.getRadius(), // scale
                        0);
            }

            batcher.setColor(1, 1, 1, 1);
        }
    }

    public void renderTeamB() {
        // Team B
        for(Player p : teamB.getTeamList()) {
            batcher.setColor(1,1,1,p.getInvisibility_scale(player.getId()));

            // Slowed effect
            if(p.isSlowed()) {
                batcher.draw(AssetLoader.powerUpEffectRegion1, // Texture
                        p.getBody().getPosition().x,
                        p.getBody().getPosition().y,
                        p.getRadius()-0.5f, // Offset would be the difference of radius from 2
                        -p.getRadius()/5,
                        p.getRadius(), // width
                        p.getRadius()/1.5f, // height
                        p.getRadius(), // scale
                        p.getRadius(), // scale
                        0);
            }

            //rotate player according to direction of movement
            float angle = p.getAngleDegree();

            TextureRegion playerDirection;
            if(!p.getBody().getLinearVelocity().epsilonEquals(Vector2.Zero, 0.1f)) {
                if(p.isBoosting()) {
                    for(Animation animation : AssetLoader.TeamAnimationB){
                        animation.setFrameDuration(0.1f);
                    }
                }
                else {
                    for(Animation animation : AssetLoader.TeamAnimationB){
                        animation.setFrameDuration(0.2f);
                    }
                }

                if (angle <= 135 && angle > 45)
                    playerDirection = AssetLoader.playerAnimationDownB.getKeyFrame(runTime);
                else if (angle <= 45 && angle > -45)
                    playerDirection = AssetLoader.playerAnimationRightB.getKeyFrame(runTime);
                else if (angle <= -45 && angle > -135)
                    playerDirection = AssetLoader.playerAnimationUpB.getKeyFrame(runTime);
                else
                    playerDirection = AssetLoader.playerAnimationLeftB.getKeyFrame(runTime);
            }
            else {
                if (angle <= 135 && angle > 45)
                    playerDirection = AssetLoader.playerRegionBdown1;
                else if (angle <= 45 && angle > -45)
                    playerDirection = AssetLoader.playerRegionBright1;
                else if (angle <= -45 && angle > -135)
                    playerDirection = AssetLoader.playerRegionBup1;
                else
                    playerDirection = AssetLoader.playerRegionBleft1;
            }

            // As render begins from TopLeft of body's position, Offset by the radius is required.
            batcher.draw(playerDirection, // Texture
                    p.getBody().getPosition().x,
                    p.getBody().getPosition().y,
                    p.getRadius()-0.5f, // Offset would be the difference of radius from 2
                    p.getRadius()-0.5f,
                    p.getRadius(), // width
                    p.getRadius(), // height
                    p.getRadius(), // scale
                    p.getRadius(), // scale
                    0);

            // Confused Effect
            if(p.isConfused()) {
                batcher.draw(AssetLoader.powerUpEffectRegion2, // Texture
                        p.getBody().getPosition().x,
                        p.getBody().getPosition().y,
                        p.getRadius()-0.5f, // Offset would be the difference of radius from 2
                        p.getRadius()*2-0.5f,
                        p.getRadius(), // width
                        p.getRadius(), // height
                        p.getRadius(), // scale
                        p.getRadius(), // scale
                        0);
            }

            batcher.setColor(1,1,1,1);
        }
    }

    private void renderBall() {
        if(ball != null) {
            // If the ball holder is invisible, ditto!
            if(ball.isHeld()) {
                batcher.setColor(1,1,1,ball.getHoldingPlayer().getInvisibility_scale(player.getId()));
            }

            // As render begins from TopLeft of body's position, Offset by the radius is required.
            batcher.draw(AssetLoader.ballRegion,
                    ball.getBody().getPosition().x,
                    ball.getBody().getPosition().y,
                    ball.getRadius(),
                    ball.getRadius(),
                    ball.getRadius(), // width
                    ball.getRadius(), // height
                    ball.getRadius(),
                    ball.getRadius(),
                    0);
            batcher.setColor(1, 1, 1, 1);


//            System.out.println(ball.getRadius() * GameConstants.RENDER_RADIUS_SCALE + " " + GameConstants.RENDER_RADIUS_SCALE);
        }
    }
    private void renderPowerUps() {
        // PowerUps
        if(powerUpList!=null) {
            for (PowerUp powerUp : powerUpList) {
                batcher.draw(AssetLoader.powerUpRegion,
                        powerUp.getBody().getPosition().x,
                        powerUp.getBody().getPosition().y,
                        powerUp.getRadius()*1.2f - 0.4f,
                        powerUp.getRadius()*1.2f - 0.4f,
                        powerUp.getRadius()*1.2f, // width
                        powerUp.getRadius()*1.2f, // height
                        powerUp.getRadius()*1.2f,
                        powerUp.getRadius()*1.2f,
                        0);
            }
        }
        else {
            powerUpList = map.getPowerUpList();
        }
    }
}
