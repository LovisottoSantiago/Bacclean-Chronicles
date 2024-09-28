package io.github.bacclean;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    Texture backgroundTexture;


    // Always
    SpriteBatch spriteBatch;
    ExtendViewport extendViewport;
    Camera camera;


    // Player
    Player baccleanPlayer;

    @Override
    public void create() {
        setScreen(new FirstScreen());
        backgroundTexture = new Texture("backgrounds/bg_1.png");


        // Player
        baccleanPlayer = new Player("baccleanPlayer/walk.png", "baccleanPlayer/stance.png");
        baccleanPlayer.setPosition(0, 0);

        // Always
        camera = new OrthographicCamera();
        spriteBatch = new SpriteBatch();
        extendViewport = new ExtendViewport(1024, 768);
        extendViewport.getCamera().position.set(extendViewport.getWorldWidth() / 2, extendViewport.getWorldHeight() / 2, 0);

    }

    @Override
    public void resize(int width, int height) {
        extendViewport.update(width, height, true); // true centers the camera
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
        float delta = Gdx.graphics.getDeltaTime();
        baccleanPlayer.playerMove(speed, delta);
        
        baccleanPlayer.update(delta);
        /*
        screenViewport.getCamera().position.set(baccleanPlayer, baccleanPlayer, 0)
        */

    }

    // todo: continue here
    private void logic() {

    }

    private void draw() {
        ScreenUtils.clear(Color.BLUE);
        extendViewport.apply();
        spriteBatch.setProjectionMatrix(extendViewport.getCamera().combined);
         

        // draw stuff here
        spriteBatch.begin();
        
        // store the worldWidth and worldHeight as local variables for brevity
        float worldWidth = extendViewport.getWorldWidth();
        float worldHeight = extendViewport.getWorldHeight();

        
        spriteBatch.draw(backgroundTexture, 0, 0, worldWidth, worldHeight); // draw the background
        baccleanPlayer.draw(spriteBatch);

        spriteBatch.end();
    }

}