package io.github.bacclean;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class IntroScreen implements Screen {

    private final Texture logo;
    private final SpriteBatch batch;
    private float elapsedTime = 0;
    private final float blackScreenDuration = 1f;  
    private final float fadeInDuration = 3f;       
    private final float fadeOutDuration = 1f;      
    private boolean soundPlayed = false;           
    private final Sound logoSound;                 
    private final Main game;

    public IntroScreen(Main game) {
        this.game = game;
        logo = new Texture(Gdx.files.internal("intro/logo.png"));
        logoSound = Gdx.audio.newSound(Gdx.files.internal("intro/intro_lovi.ogg"));
        batch = new SpriteBatch();
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        elapsedTime += delta;

        if (elapsedTime <= blackScreenDuration) {
            Gdx.gl.glClearColor(0, 0, 0, 1); 
        } else {
            Gdx.gl.glClearColor(250/255f, 241/255f, 232/255f, 1); 
        }
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (elapsedTime > blackScreenDuration) {
            float adjustedTime = elapsedTime - blackScreenDuration;
            float alpha;

            if (adjustedTime <= fadeInDuration) {
                alpha = Math.min(adjustedTime / fadeInDuration, 1f);

                if (!soundPlayed && adjustedTime >= 0.1f) {
                    logoSound.play();
                    logoSound.setVolume(0, 0.5f);
                    soundPlayed = true;
                }
            } else {
                alpha = Math.max(1f - ((adjustedTime - fadeInDuration) / fadeOutDuration), 0f);
            }

            batch.begin();
            batch.setColor(1, 1, 1, alpha);

            float x = (Gdx.graphics.getWidth() - logo.getWidth()) / 2;
            float y = (Gdx.graphics.getHeight() - logo.getHeight()) / 2;
            batch.draw(logo, x, y);

            batch.setColor(Color.WHITE);
            batch.end();

            if (adjustedTime >= fadeInDuration + fadeOutDuration) {
                game.setScreen(new MenuScreen(game, game.getCamera(), game.getExtendViewport()));
            }
            Gdx.input.setCursorCatched(true);
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        logo.dispose();
        logoSound.dispose();  
        batch.dispose();
    }
}
