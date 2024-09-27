package io.github.bacclean;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    Texture backgroundTexture;
    Texture bucketTexture;
    Texture dropTexture;
    Sound dropSound;
    Music music;  

    Sprite bucketSprite; // Declare a new Sprite variable


    SpriteBatch spriteBatch;
    FitViewport viewport;


     Vector2 touchPos;
    
    // Definición de worldWidth y worldHeight
    final float worldWidth = 8;
    final float worldHeight = 5;

    @Override
    public void create() {
        setScreen(new FirstScreen());
        backgroundTexture = new Texture("background.png");
        bucketTexture = new Texture("bucket.png");
        dropTexture = new Texture("drop.png");

        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"));
        music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));


        bucketSprite = new Sprite(bucketTexture); // Initialize the sprite based on the texture
        bucketSprite.setSize(1, 1); // Define the size of the sprite
        
        touchPos = new Vector2();

        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(worldWidth, worldHeight);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true); // true centers the camera
    }

    @Override
    public void render() {
        // organize code into three methods
        input();
        logic();
        draw();
    }

    private void input() {
        float speed = 4f;
        float delta = Gdx.graphics.getDeltaTime(); // retrieve the current delta

        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            speed = 6f; // Acelerate
            } else if (Gdx.input.isKeyPressed(Input.Keys.W)) {
        bucketSprite.translateY(speed * delta); // Move the bucket up
        } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
        bucketSprite.translateX(-speed * delta); // Move the bucket left
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
        bucketSprite.translateY(-speed * delta); // Move the bucket down
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            bucketSprite.translateX(speed * delta); // Move the bucket right
        }
        
        if (Gdx.input.isTouched()) { // If the user has clicked or tapped the screen
            // todo:React to the player touching the screen
            touchPos.set(Gdx.input.getX(), Gdx.input.getY()); // Get where the touch happened on screen
            viewport.unproject(touchPos); // Convert the units to the world units of the viewport
            // bucketSprite.setCenterX(touchPos.x); // Change the horizontally centered position of the bucket            
        }

    }

    // ! Continue here
    private void logic() {

    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();
        // draw stuff here

        spriteBatch.draw(backgroundTexture, 0, 0, worldWidth, worldHeight); // draw the background
        bucketSprite.draw(spriteBatch); // Sprites have their own draw method

        spriteBatch.end();
    }

}