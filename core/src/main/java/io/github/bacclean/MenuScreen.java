package io.github.bacclean;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class MenuScreen implements Screen {
    private final Main game;
    private SpriteBatch batch;
    private BitmapFont font;
    private Texture background;

    private final OrthographicCamera camera;
    private final ExtendViewport extendViewport;

    public MenuScreen(Main game, OrthographicCamera camera, ExtendViewport extendViewport) {
        this.game = game;
        this.camera = camera; // Guardar la referencia de la cámara
        this.extendViewport = extendViewport; // Guardar la referencia del viewport
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        background = new Texture("backgrounds/test_bg.png");
    }

    @Override
    public void render(float delta) {
        // Limpiar la pantalla
        ScreenUtils.clear(Color.BLACK);

        // Actualizar la cámara
        camera.update();
        
        // Establecer la matriz de proyección
        batch.setProjectionMatrix(camera.combined);
        
        // Dibujar el fondo y los textos
        batch.begin();
        batch.draw(background, 0, 0, extendViewport.getWorldWidth(), extendViewport.getWorldHeight());
        
        float titleX = extendViewport.getWorldWidth() / 2;
        float titleY = extendViewport.getWorldHeight() / 2;
        float instructionY = titleY - 60; // Ajustar la distancia desde el título        
        font.draw(batch, "Menu Principal", titleX, titleY);
        font.draw(batch, "Presiona ENTER para comenzar el juego", titleX - 90, instructionY);
        
        batch.end();

        // Comprobar entrada
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.setScreen(new GameScreen(game, camera, extendViewport));  // Al presionar ENTER, empieza el juego
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
        font.dispose();
        background.dispose(); // Dispose of the background texture
    }
}
