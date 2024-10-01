package io.github.bacclean;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {

    // Always
    SpriteBatch spriteBatch;
    ExtendViewport extendViewport;
    OrthographicCamera camera;

    // Player
    Player baccleanPlayer;


    @Override
    public void create() {
        setScreen(new FirstScreen());       
    
        // Initialize camera and viewport
        camera = new OrthographicCamera(); // Initialize the camera without size
        extendViewport = new ExtendViewport(1280, 720, camera);  // Set viewport dimensions
        camera.position.set(extendViewport.getWorldWidth() / 2, extendViewport.getWorldHeight() / 2, 0); // Center camera
        extendViewport.apply();

        // Set initial zoom
        camera.zoom = 1.0f;
        
        spriteBatch = new SpriteBatch();
    
        // Player
        baccleanPlayer = new Player("sprites-player/player-idle.png", 4, 1, "sprites-player/player-run.png", 6, 1, "sprites-player/player-attack-test.png", 11, 1);

        baccleanPlayer.setSize(200, 118); // original 100 x 59
        baccleanPlayer.setPosition(0, 0);

    }
    

    @Override
    public void resize(int width, int height) {
        extendViewport.update(width, height, true);  // true centers the camera
    }

    @Override
    public void render() {
        input(); // Handle player input
        logic(); // Update game logic
    
        // Center the camera on the player
        camera.position.set(baccleanPlayer.getX() + baccleanPlayer.getWidth()/2, baccleanPlayer.getY() + baccleanPlayer.getHeight(), 0);       
        camera.update(); // Update the camera
    
        draw(); // Draw the scene
    }



    private void input() {
        float speed = baccleanPlayer.getHeight(); // Use player height for speed
        float delta = Gdx.graphics.getDeltaTime();
        baccleanPlayer.playerMove(speed, delta);

        
        // Implement zooming functionality with key presses
        if (Gdx.input.isKeyPressed(Input.Keys.PAGE_UP)) {
            camera.zoom -= 0.02f; // Zoom in
            camera.zoom = MathUtils.clamp(camera.zoom, 0.5f, 2.0f); // Clamp the zoom levels
            Gdx.app.log("Zoom", "Zooming In: " + camera.zoom); // Debug log
        }
        if (Gdx.input.isKeyPressed(Input.Keys.PAGE_DOWN)) {
            camera.zoom += 0.01f; // Zoom out
            camera.zoom = MathUtils.clamp(camera.zoom, 0.5f, 1.0f); // Clamp the zoom levels
        }     


    }


    private void logic() {
        // Game logic here
    }


    private void draw() {
        ScreenUtils.clear(Color.GRAY); // Clear screen before drawing
        camera.update(); // Ensure the camera is updated
        spriteBatch.setProjectionMatrix(camera.combined); // Sync the SpriteBatch with the camera

        spriteBatch.begin();
        // Draw background first
        // no background

        // Draw the player
        TextureRegion currentFrame = baccleanPlayer.getCurrentFrame();
        spriteBatch.draw(currentFrame, baccleanPlayer.getX(), baccleanPlayer.getY(), baccleanPlayer.getWidth(), baccleanPlayer.getHeight());

        spriteBatch.end();
    }

    

    @Override
    public void dispose() {
        spriteBatch.dispose();
        //backgroundTexture.dispose();
        baccleanPlayer.dispose(); // Call dispose for the player as well
    }
    

    
}
