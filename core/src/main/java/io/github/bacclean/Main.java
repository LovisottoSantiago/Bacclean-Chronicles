package io.github.bacclean;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
        
    
        // Always
        camera = new OrthographicCamera();
        extendViewport = new ExtendViewport(1024, 768, camera);  // Initialize viewport and camera
        camera.position.set(extendViewport.getWorldWidth() / 2, extendViewport.getWorldHeight() / 2, 0);  // Center camera
        extendViewport.apply();
        
        spriteBatch = new SpriteBatch();
    
        // Player
        baccleanPlayer = new Player("sprites-player/player-idle.png", 4, 1, "sprites-player/player-run.png", 6, 1);

        baccleanPlayer.setSize(256, 256);
        baccleanPlayer.setPosition(extendViewport.getWorldWidth() / 2 - baccleanPlayer.getWidth() / 2, extendViewport.getWorldHeight() / 2 - baccleanPlayer.getHeight() / 2);


    }
    

    @Override
    public void resize(int width, int height) {
        extendViewport.update(width, height, true);  // true centers the camera
    }

    @Override
    public void render() {
        input();
        logic();
        draw();
    }


    private void input() {
        float speed = baccleanPlayer.getHeight();
        float delta = Gdx.graphics.getDeltaTime();
        baccleanPlayer.playerMove(speed, delta);

    }


    private void logic() {
        // Game logic here
    }


    private void draw() {
        ScreenUtils.clear(Color.BLUE); // Clear screen before drawing
        camera.update(); // Ensure the camera is updated
        spriteBatch.setProjectionMatrix(camera.combined); // Sync the SpriteBatch with the camera

        spriteBatch.begin();
        // Draw background first
        spriteBatch.draw(backgroundTexture, 0, 0, extendViewport.getWorldWidth(), extendViewport.getWorldHeight());

        // Draw the player
        TextureRegion currentFrame = baccleanPlayer.getCurrentFrame();
        spriteBatch.draw(currentFrame, baccleanPlayer.getX(), baccleanPlayer.getY(), baccleanPlayer.getWidth(), baccleanPlayer.getHeight());

        spriteBatch.end();
    }

    

    @Override
    public void dispose() {
        spriteBatch.dispose();
        backgroundTexture.dispose();
        baccleanPlayer.dispose(); // Call dispose for the player as well
    }
    

    
}
