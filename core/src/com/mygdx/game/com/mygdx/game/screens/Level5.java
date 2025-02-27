// this class serves as the screen which is level5
// created by Rithik Rajasekar and Matt Seng

package com.mygdx.game.com.mygdx.game.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.DodgeGame;
import com.mygdx.game.entities.Boulder;
import com.mygdx.game.entities.Cannon;


public class Level5 extends Level implements Screen {
    public Level5(DodgeGame g) {
        currentLevelNumber = 5;
        game = g;
        levelTexture = new Texture("sprites/dodgeDesertBG.png");
        coins = 0;
        world = "SAND";
        level = " LEVEL 5";

        Boulder.min = 3;
        Boulder.max = 6;
        Boulder.spawnInterval = 5;
        Boulder.spawnDelay = 2;

        Cannon.min = 3;
        Cannon.max = 6;
        Cannon.spawnInterval = 6;
        Cannon.spawnDelay = 2.5;

        playMusic("music/06 - Sand World.mp3");
        createPlayer();
    }

    public void render(float delta) {
        displayBackground(levelTexture);

        //don't mess around with the order of the display methods unless you know what you're doing
        //it will cause a "SpriteBatch.begin must be called before draw" or "SpriteBatch music be called before end" error
        //for some reason displayTimer must be called first
        displayTimer(delta);
        displayWorldAndLevel();
        drawGrid();
        displayCoinCounter();
        displayInvincibilityBar();

        detectCollision();
        detectCoin();

        spawnEntities();
        renderEntities(delta);

        game.batch.end();
    }

    public void spawnEntities() {
        spawnCoins();
        spawnBoulders();
        spawnCannon();
    }

    public void renderEntities(float delta) {
        renderPlayer(delta);
        renderCoins();
        renderBoulders(delta);
        renderCannons(delta);
    }

    public void detectCollision() {
        detectBoulderCollision();
        detectCannonCollision();
    }

    public void resize(int width, int height) {

    }

    public void pause() {

    }

    public void resume() {

    }

    public void hide() {

    }

    public void dispose() {
        super.dispose();
    }
}
