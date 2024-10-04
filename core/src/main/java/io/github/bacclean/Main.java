package io.github.bacclean;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class Main extends Game {
    private OrthographicCamera camera;
    private ExtendViewport extendViewport;

    private static final int VIRTUAL_WIDTH = 1280; // Fixed virtual width
    private static final int VIRTUAL_HEIGHT = 720; // Fixed virtual height

    @Override
    public void create() {
        // Initialize camera and viewport
        camera = new OrthographicCamera();
        extendViewport = new ExtendViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
        extendViewport.apply();

        // Initially show the menu
        setScreen(new MenuScreen(this, camera, extendViewport));

        // Set input processor to handle key events
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                // Check if Alt + Enter is pressed
                if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT) && keycode == Input.Keys.ENTER) {
                    toggleFullscreen();
                    return true; // Event handled
                }
                return false; // Not handled
            }
        });
    }

    private void toggleFullscreen() {
        // Toggle fullscreen mode
        boolean isFullscreen = Gdx.graphics.isFullscreen();
        if (isFullscreen) {
            // Exit fullscreen
            Gdx.graphics.setWindowedMode(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        } else {
            // Enter fullscreen
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        }
    }

    // Getters for camera and viewport if needed
    public ExtendViewport getExtendViewport() {
        return extendViewport;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    @Override
    public void resize(int width, int height) {
        // Update the viewport to maintain aspect ratio
        extendViewport.update(width, height, true);
    }

    @Override
    public void render() {
        super.render();
        camera.update();
    }
}
