package com.mygdx.game.com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mygdx.game.DodgeGame;
import com.mygdx.game.entities.*;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class Level {
    public static final int PLAYER_MOVE_DISTANCE = 63;//9*7, 7 is the scalar multiplier for all sprites
    public static final int PLAYER_WIDTH = 56;//8*7
    public static final int PLAYER_HEIGHT = 70;//10*7
    public static final int COIN_SIZE = 56; // 8*7
    public static final int GRID_WIDTH = 665;
    public static final int GRID_HEIGHT = 665;
    public static final int GRID_OFFSET_X = 588;
    public static final int GRID_OFFSET_Y = 27;
    public static final int GRID_CORNER_SIZE = 84;//12*7
    public static final String[] DIRECTIONS = {"UP", "DOWN", "LEFT", "RIGHT"};
    public static final Texture grid = new Texture("dodgeGrid.png");
    //public static final Music coinSound = Gdx.audio.newMusic(Gdx.files.internal("coin.mp3"));
    //public static final Music loseSound = Gdx.audio.newMusic(Gdx.files.internal("lose.mp3"));

    public String world, level;
    public Music music;
    public DodgeGame game;
    public Timer timer;
    public CoinCounter coinCounter;
    public Player player;
    public static int coins;
    public CopyOnWriteArrayList<Coin> coinList = new CopyOnWriteArrayList<Coin>();
    public int coinSpawnInterval;
    public boolean coinsSpawned;
    public int minBoulders;
    public int maxBoulders;
    public int boulderSpawnInterval;
    public int boulderSpawnDelay;
    public boolean bouldersSpawned;

    public int minCannon;
    public int maxCannon;
    public int cannonSpawnInterval;
    public int cannonSpawnDelay;
    public boolean cannonSpawned;

    public CopyOnWriteArrayList<Boulder> boulderList = new CopyOnWriteArrayList<Boulder>();
    public CopyOnWriteArrayList<BoulderArrow> boulderArrowList = new CopyOnWriteArrayList<BoulderArrow>();

    public CopyOnWriteArrayList<Cannon> cannonList = new CopyOnWriteArrayList<Cannon>();
    public CopyOnWriteArrayList<CannonArrow> cannonArrowList = new CopyOnWriteArrayList<CannonArrow>();

    public Texture levelTexture;
    public static Sprite BackgroundSprite;
    public static int currentLevelNumber = 0;

    public void show(){
        timer = new Timer(60);
        coinCounter = new CoinCounter();
    }
    public void displayBackground(Texture background){
        BackgroundSprite = new Sprite(background);
        BackgroundSprite.scale(7);
        game.batch.begin();
        BackgroundSprite.setPosition(DodgeGame.WIDTH/2 -BackgroundSprite.getWidth()/2, DodgeGame.HEIGHT/2 - BackgroundSprite.getHeight()/2);
        BackgroundSprite.draw(game.batch);
        game.batch.end();
    }

    public void playMusic() {
        music = Gdx.audio.newMusic(Gdx.files.internal("spinAndBurst.mp3"));
        music.setLooping(true);
        music.setVolume(1f);
        music.play();
    }

    public void createPlayer() {
        player = new Player();
        player.x = 4;
        player.y = 4;
    }

    public void displayTimer(float delta) {
        timer.update(delta);
        timer.render(game.batch, game.font);

        //checks if time is up
        if (timer.getWorldTimer() <= 0) {
            music.stop();
            music.dispose();
            game.setScreen(new WonLevel(game));
        }
    }

    public void displayCoinCounter() {
        coinCounter.update(coins);
        coinCounter.render(game.batch, game.font);
    }

    public void displayWorldAndLevel() {
        game.font.setColor(Color.PURPLE);
        game.font.getData().setScale(4f);
        game.font.draw(game.batch, world, (int)(DodgeGame.WIDTH * 0.07) , DodgeGame.HEIGHT/2 + 300);
        game.font.draw(game.batch, level, (int)(DodgeGame.WIDTH * 0.25) , DodgeGame.HEIGHT/2 + 300);
    }

    public void drawGrid() {
        game.batch.draw(grid, GRID_OFFSET_X, GRID_OFFSET_Y, GRID_WIDTH, GRID_HEIGHT);
    }

    public void detectCoin() {
        for (int i = 0; i < coinList.size(); i++) {
            if (coinList.get(i).x == player.x && coinList.get(i).y == player.y) {
                coins++;
                //coinSound.play();
                coinList.remove(i);
            }
        }
    }

    public void renderPlayer() {
        player.update();
        player.render(game.batch);
    }

    public void renderCoins() {
        for (Coin c : coinList) {
            c.update();
            c.render(game.batch);
        }
    }

    public void renderBoulders(float delta) {
        for (int i = 0; i < boulderList.size(); i++) {
            //only updates and renders once the boulder is spawned
            if (boulderList.get(i).spawned) {
                //updates and render if the boulder is on screen
                if (boulderList.get(i).isOnScreen) {
                    boulderList.get(i).update(delta);
                    boulderList.get(i).render(game.batch);
                    //deletes boulder and arrowTexture once the boulder leaves the screen
                } else {
                    boulderList.remove(i);
                    boulderArrowList.remove(i);
                    i--;
                }
                //if the boulder hasn't spawned yet, the arrowTexture will blink
                //(it will stop blinking after the boulder is spawned because it exits this else statement)
            } else {
                boulderArrowList.get(i).render(game.batch);
                if (boulderArrowList.get(i).elapsedTime > boulderSpawnDelay)
                    boulderList.get(i).spawned = true;
            }
        }
    }

    public void renderCannon(float delta) {
        for (int i = 0; i < cannonList.size(); i++) {
            //only updates and renders once the boulder is spawned
            if (cannonList.get(i).spawned) {
                //updates and render if the boulder is on screen
                if (cannonList.get(i).isOnScreen) {
                    cannonList.get(i).update(delta);
                    cannonList.get(i).render(game.batch);
                    //deletes boulder and arrowTexture once the boulder leaves the screen
                } else {
                    cannonList.remove(i);
                    cannonArrowList.remove(i);
                    i--;
                }
                //if the boulder hasn't spawned yet, the arrowTexture will blink
                //(it will stop blinking after the boulder is spawned because it exits this else statement)
            } else {
                cannonArrowList.get(i).render(game.batch);
                if (cannonArrowList.get(i).elapsedTime > cannonSpawnDelay)
                    cannonList.get(i).spawned = true;
            }
        }
    }


    public void spawnCoins(){
        if (timer.getWorldTimer() % coinSpawnInterval == 0 && !coinsSpawned){
            coinList.clear();
            for (int i = 0; i < 2; i++){
                coinList.add(new Coin());
                if (i == 1 && coinList.get(0).x == coinList.get(1).x && coinList.get(0).y == coinList.get(1).y){
                    i--;
                    coinList.remove(1);
                }
            }
            coinsSpawned = true;
        }
        if (timer.getWorldTimer() % coinSpawnInterval != 0){
            coinsSpawned = false;
        }
    }

    public void spawnBoulders() {
        if (timer.getWorldTimer() % boulderSpawnInterval == 0 && !bouldersSpawned){
            //x and y lists to test if it's trying spawn a boulder where one already exists
            ArrayList<Integer> xList = new ArrayList<Integer>();
            ArrayList<Integer> yList = new ArrayList<Integer>();

            for (int i = 0; i < (int) ((maxBoulders - minBoulders + 1) * Math.random() + minBoulders); i++) {
                int x = 0;
                int y = 0;
                String direction = DIRECTIONS[(int) (Math.random() * 4)];

                // randomly assigns a spawn position to the boulder based on what the direction of the boulder is
                if (direction == "UP") {
                    x = (int) (Math.random() * 8);
                    y = -1;
                } else if (direction == "DOWN") {
                    x = (int) (Math.random() * 8);
                    y = 8;
                } else if (direction == "LEFT") {
                    x = 8;
                    y = (int) (Math.random() * 8);
                } else if (direction == "RIGHT") {
                    x = -1;
                    y = (int) (Math.random() * 8);
                }

                //does not create a boulder if one is already there
                boolean inList = false;
                for (int tempX : xList) {
                    for (int tempY : yList) {
                        if (x == tempX && y == tempY) {
                            inList = true;
                        }
                    }
                }
                if (!inList) {
                    boulderList.add(new Boulder(x, y, direction));
                    boulderArrowList.add(new BoulderArrow(x, y, direction));
                    xList.add(x);
                    yList.add(y);
                } else {
                    i--;
                }
            }
            bouldersSpawned = true;
        }
        if (timer.getWorldTimer() % boulderSpawnInterval != 0){
            bouldersSpawned = false;
        }
    }

    public void spawnCannon() {
        if (timer.getWorldTimer() % cannonSpawnInterval == 0 && !cannonSpawned){
            //x and y lists to test if it's trying spawn a boulder where one already exists
            ArrayList<Integer> xList = new ArrayList<Integer>();
            ArrayList<Integer> yList = new ArrayList<Integer>();

            for (int i = 0; i < (int) ((maxCannon - minCannon + 1) * Math.random() + minCannon); i++) {
                int x = 0;
                int y = 0;
                String direction = DIRECTIONS[(int) (Math.random() * 4)];

                // randomly assigns a spawn position to the boulder based on what the direction of the boulder is
                if (direction == "UP") {
                    x = (int) (Math.random() * 8);
                    y = -1;
                } else if (direction == "DOWN") {
                    x = (int) (Math.random() * 8);
                    y = 8;
                } else if (direction == "LEFT") {
                    x = 8;
                    y = (int) (Math.random() * 8);
                } else if (direction == "RIGHT") {
                    x = -1;
                    y = (int) (Math.random() * 8);
                }

                //does not create a boulder if one is already there
                boolean inList = false;
                for (int tempX : xList) {
                    for (int tempY : yList) {
                        if (x == tempX && y == tempY) {
                            inList = true;
                        }
                    }
                }
                if (!inList) {
                    cannonList.add(new Cannon(x, y, direction));
                    cannonArrowList.add(new CannonArrow(x, y, direction));
                    xList.add(x);
                    yList.add(y);
                } else {
                    i--;
                }
            }
            cannonSpawned = true;
        }
        if (timer.getWorldTimer() % boulderSpawnInterval != 0){
            cannonSpawned = false;
        }
    }

    public void detectBoulderCollision() {
        for (int i = 0; i < boulderList.size(); i++) {
            if (boulderList.get(i).x == player.x && boulderList.get(i).y == player.y) {
                //loseSound.play()
                music.dispose();
                game.setScreen(new GameOver(game));
            }
        }
    }

    public void detectCannonCollision(){
        for (int i = 0; i < cannonList.size(); i++) {
            if (cannonList.get(i).x == player.x && cannonList.get(i).y == player.y) {
                //loseSound.play()
                music.dispose();
                game.setScreen(new GameOver(game));
            }
        }
    }
}
