package com.forofour.game.gameworlds;

import com.badlogic.gdx.Gdx;
import com.forofour.game.gameobjects.Ball;
import com.forofour.game.gameobjects.Player;

/**
 * Created by seanlim on 19/2/2016.
 */
public class GameWorld {

    private Ball ball;
    private Player player;

    private int time;
    private float runTime = 0;

    public GameWorld(){

        ball = new Ball(100, 100, 1.5f);
        player = new Player(50, 50, 4f, ball);
    }

    public void update(float delta){
        Gdx.app.log("GameWorld", "update");
        runTime += delta;

        player.update(delta);
        ball.update(delta);
    }

    public Ball getBall(){
        return ball;
    }
    public Player getPlayer(){
        return player;
    }

}
