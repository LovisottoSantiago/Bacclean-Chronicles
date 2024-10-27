package io.github.bacclean.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils; // Import Interpolation for fade effect
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import io.github.bacclean.Main;

public class MenuScreen implements Screen {
    private final Main game;
    private SpriteBatch batch;
    private BitmapFont fontTitle, fontCredit;
    private Texture background;
    private Music menMusic;
    private Sound enterSound;

    private final OrthographicCamera camera;
    private final ExtendViewport extendViewport;

    private boolean isTransitioning = false; 
    private float transitionTimer = 0f; 
    private float fadeValue = 1f; // Fading value for the transition

    public MenuScreen(Main game, OrthographicCamera camera, ExtendViewport extendViewport) {
        this.game = game;
        this.camera = camera;
        this.extendViewport = extendViewport;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        fontTitle = new BitmapFont(Gdx.files.internal("fonts/PatuaOne.fnt"));
        fontTitle.setColor(new Color(0xb4dea2ff));

        fontCredit = new BitmapFont(Gdx.files.internal("fonts/PatuaOne.fnt"));
        fontCredit.setColor(new Color(0xf9febcff));

        background = new Texture("main_menu/background.png");

        try {
            menMusic = Gdx.audio.newMusic(Gdx.files.internal("main_menu/music.mp3"));
            menMusic.setLooping(true);
            menMusic.setVolume(0.6f);
            menMusic.play();
        } catch (Exception e) {
            Gdx.app.log("Music Error", "Could not load music file: " + e.getMessage());
        }

        enterSound = Gdx.audio.newSound(Gdx.files.internal("main_menu/enter.mp3"));
        Gdx.input.setCursorCatched(true);
    }

    private float elapsedTime = 0f; 
    private boolean showText = true; 

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        elapsedTime += delta;

        // Toggle visibility of the "PRESS START." text before pressing Enter
        if (!isTransitioning) {
            if (elapsedTime >= 0.5f) {  // Blink every 0.1 seconds
                showText = !showText;
                elapsedTime = 0f;
            }
        } else {
            // If transitioning, fade out effect
            fadeValue -= delta / 1.5f; // Gradually reduce the fade value during transition
            if (fadeValue < 0) fadeValue = 0;
        }

        batch.begin();
        batch.draw(background, 0, 0, extendViewport.getWorldWidth(), extendViewport.getWorldHeight());

        GlyphLayout layout = new GlyphLayout();

        // Draw the "PRESS START." text if it's supposed to be visible
        if (!isTransitioning && showText) {
            layout.setText(fontTitle, "PRESS START");
            float titleX = (extendViewport.getWorldWidth() - layout.width) / 2;
            fontTitle.setColor(1, 1, 1, 1); // Fully visible before transition
            fontTitle.draw(batch, layout, titleX, 80);
        } else if (isTransitioning && fadeValue > 0) {
            layout.setText(fontTitle, "PRESS START");
            float titleX = (extendViewport.getWorldWidth() - layout.width) / 2;
            fontTitle.setColor(1, 1, 1, fadeValue); // Apply fade effect
            fontTitle.draw(batch, layout, titleX, 80);
        }

        // Draw "BY LOVI" text (this remains static)
        layout.setText(fontCredit, "BY LOVI");
        float creditX = (extendViewport.getWorldWidth() - layout.width) / 2;
        fontCredit.getData().setScale(0.6f);
        fontCredit.draw(batch, layout, creditX, 45);

        batch.end();

        // Check for Enter key press
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && !isTransitioning) {
            isTransitioning = true; // Start transitioning
            enterSound.play(); // Play sound effect
            enterSound.setVolume(1, 0.8f);
        }

        // Handle the transition
        if (isTransitioning) {
            transitionTimer += delta;
            if (transitionTimer >= 1.5f) { // After the fade-out completes (1.5 seconds)
                game.setScreen(new GameScreen(game, camera, extendViewport));
                this.dispose();
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        extendViewport.update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        fontTitle.dispose();
        fontCredit.dispose();
        background.dispose();
        menMusic.stop();
        menMusic.dispose();
        enterSound.dispose();
    }
}
