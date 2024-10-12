package io.github.bacclean;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;


public class MenuScreen implements Screen {
    private final Main game;
    private SpriteBatch batch;
    private BitmapFont fontTitle, fontCredit;
    private Texture background;
    private Music menMusic;

    private final OrthographicCamera camera;
    private final ExtendViewport extendViewport;
    

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

        background = new Texture("backgrounds/test_bg.png");

        try {
            menMusic = Gdx.audio.newMusic(Gdx.files.internal("Death of a Ninja (intro).mp3"));
            menMusic.setLooping(false);
            menMusic.setVolume(1.0f); // Set volume to maximum (0.0f to 1.0f)
            menMusic.play();            
        } catch (Exception e) {
            Gdx.app.log("Music Error", "Could not load music file: " + e.getMessage());
        }
        // hide cursor
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
        
        if (elapsedTime >= 0.70f) {
            showText = !showText; 
            elapsedTime = 0f;
        }


        batch.begin();
        batch.draw(background, 0, 0, extendViewport.getWorldWidth(), extendViewport.getWorldHeight());
    
        GlyphLayout layout = new GlyphLayout(); 
    
        if (showText){
            layout.setText(fontTitle, "PRESS START.");
            float titleX = (extendViewport.getWorldWidth() - layout.width) / 2; 
            fontTitle.draw(batch, layout, titleX, 80);
        
        }
        // Texto "BY LOVI"
        layout.setText(fontCredit, "BY LOVI");
        float creditX = (extendViewport.getWorldWidth() - layout.width) / 2; 
        fontCredit.getData().setScale(0.6f); 
        fontCredit.draw(batch, layout, creditX, 45);
        
        batch.end();
    
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.setScreen(new GameScreen(game, camera, extendViewport));  
            this.dispose();
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
        background.dispose(); // Dispose of the background texture
        menMusic.stop(); // Stop the music if it's playing
        menMusic.dispose(); // Dispose of the music resource        
    }
}