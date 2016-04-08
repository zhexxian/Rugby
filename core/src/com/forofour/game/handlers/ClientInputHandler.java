package com.forofour.game.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.forofour.game.MyGdxGame;
import com.forofour.game.gameobjects.Player;
import com.forofour.game.net.GameClient;
import com.forofour.game.net.Network;
import com.forofour.game.screens.LobbyScreen;
import com.forofour.game.screens.MenuScreen;

/**
 * Created by seanlim on 4/4/2016.
 */
public class ClientInputHandler extends InputMultiplexer {
    private MainOverlay overlay;
    private GameClient client;
    private Player player;

    public ClientInputHandler(MainOverlay overlay){
        super(overlay); // Assign Stage adapter as the base input adapter
        this.overlay = overlay;
        this.client = overlay.getClient();
        this.player = client.getMap().getPlayer();
    }

    @Override
    public boolean keyDown(int keycode) {

        if(keycode == Input.Keys.P){
            client.sendMessage(new Network.PacketGamePause());
            Gdx.app.log("Keypressed P", "PauseButton Sent");
        }
        if(keycode == Input.Keys.R){
            client.sendMessage(new Network.PacketGameEnd(true));
            Gdx.app.log("Keypressed R", "PlayAgain(TRUE) Button Sent");
        }
        if(keycode == Input.Keys.F){
            client.sendMessage(new Network.PacketGameEnd(false));
            ((MyGdxGame) Gdx.app.getApplicationListener()).setScreen(new MenuScreen());
            Gdx.app.log("Keypressed F", "PlayAgain(FALSE) Button Sent");
        }
        if(keycode == Input.Keys.L){
            client.restart = true;
            Gdx.app.log("Keypressed L", "ReinitLobby Button Pressed");
        }

        // ADD THE POWER UP INTO THE SERVER
        if(keycode == Input.Keys.Z) {
            client.sendMessage(new Network.PacketAddPowerUp(1));
            Gdx.app.log("Keypressed Z", "1 Sent. Add SLOW to map");
        }
        if(keycode == Input.Keys.X) {
            client.sendMessage(new Network.PacketAddPowerUp(2));
            Gdx.app.log("Keypressed X", "2 Sent. Add CONFUSION to map");
        }
        if(keycode == Input.Keys.C) {
            client.sendMessage(new Network.PacketAddPowerUp(3));
            Gdx.app.log("Keypressed C", "3 Sent. Add INVISIBILITY to map");
        }

        // PLAYER TO BE GIVEN THE POWERUP
        if(keycode == Input.Keys.Q) {
            client.sendMessage(new Network.PacketPickPowerUp(1));
            Gdx.app.log("Keypressed Q", "1 Sent. Give SLOW to player");
        }
        if(keycode == Input.Keys.W) {
            client.sendMessage(new Network.PacketPickPowerUp(2));
            Gdx.app.log("Keypressed W", "2 Sent. Give CONFUSION to player");
        }
        if(keycode == Input.Keys.E) {
            client.sendMessage(new Network.PacketPickPowerUp(3));
            Gdx.app.log("Keypressed E", "3 Sent. Give INVISIBILITY to player");
        }

        // PLAYER TO USE THE POWERUP
        if(keycode == Input.Keys.S) {
            client.sendMessage(new Network.PacketUsePowerUp());
            Gdx.app.log("Keypressed S", "UsePowerUp Sent");
        }

        if(player != null) {
            if(keycode == Input.Keys.T){
                player.dropBall();
                Gdx.app.log("Keypressed T", "Player Tossed Ball");
            }
            if(keycode == Input.Keys.B) {
                if(!player.hasBall()) {
                    player.boost();
                    Gdx.app.log("Keypressed B", "Player Boost");
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return super.keyTyped(character);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        super.touchDown(screenX, screenY, pointer, button);

        if (player != null) {
            System.out.println(screenX + " " + screenY + " " + overlay.getWidth()+ " " + overlay.getHeight()
                    + " " + Gdx.graphics.getWidth() + " " + Gdx.graphics.getHeight());
            return true;
        } else{
            player = client.getMap().getPlayer();
            Gdx.app.log("InputHandler", "Controls binded to player");
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return super.touchUp(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return super.touchDragged(screenX, screenY, pointer);
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return super.mouseMoved(screenX, screenY);
    }

    @Override
    public boolean scrolled(int amount) {
        return super.scrolled(amount);
    }
}
