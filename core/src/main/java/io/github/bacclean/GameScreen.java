package io.github.bacclean;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class GameScreen implements Screen {
    private SpriteBatch spriteBatch;
    private final ExtendViewport extendViewport;
    private final OrthographicCamera camera;
    private Player baccleanPlayer;

    // Map
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;

    public GameScreen(Main game, OrthographicCamera camera, ExtendViewport extendViewport) {
        this.camera = camera;
        this.extendViewport = extendViewport;
        this.camera.zoom = 0.6f;
    }

    @Override
    public void show() {
        spriteBatch = new SpriteBatch();
        
        // Player
        baccleanPlayer = new Player(
                                    "sprites-player/player-idle.png", 8, 1, 
                                    "sprites-player/player-run.png", 8, 1, 
                                    "sprites-player/player-attack1.png", 8, 1);
        baccleanPlayer.setSize(288, 128);
        baccleanPlayer.setPosition(0, 0);
    
        loadMap();
    }

    private void loadMap() {
        TmxMapLoader mapLoader = new TmxMapLoader(new InternalFileHandleResolver());
        map = mapLoader.load("maps/map-test.tmx");

        // Crea el renderer para el mapa
        mapRenderer = new OrthogonalTiledMapRenderer(map, spriteBatch);
        mapRenderer.setView(camera);
    }
    
    @Override
    public void render(float delta) {
        input();
        logic();

        camera.position.set(baccleanPlayer.getX() + baccleanPlayer.getWidth() / 2, baccleanPlayer.getY() + baccleanPlayer.getHeight(), 0);       
        camera.update();

        draw();
    }

    private void input() {
        float speed = baccleanPlayer.getHeight(); 
        float delta = Gdx.graphics.getDeltaTime();
        baccleanPlayer.playerMove(speed, delta);

        
        if (Gdx.input.isKeyPressed(Input.Keys.PAGE_UP)) {
            camera.zoom -= 0.01f;
            camera.zoom = MathUtils.clamp(camera.zoom, 0.4f, 0.6f);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.PAGE_DOWN)) {
            camera.zoom += 0.01f;
            camera.zoom = MathUtils.clamp(camera.zoom, 0.4f, 0.6f);
        }
    }

    private void logic() {
        // game logic
    }

    private void draw() {
        ScreenUtils.clear(Color.GRAY);
        camera.update();
    
        // Map
        mapRenderer.setView(camera);
        mapRenderer.render();
    
        // Player
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        TextureRegion currentFrame = baccleanPlayer.getCurrentFrame();
        spriteBatch.draw(currentFrame, baccleanPlayer.getX(), baccleanPlayer.getY(), baccleanPlayer.getWidth(), baccleanPlayer.getHeight());
        spriteBatch.end();
    
        baccleanPlayer.renderStaminaBar();
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
        spriteBatch.dispose();
        baccleanPlayer.dispose();
        map.dispose(); // Asegúrate de liberar recursos del mapa también
    }
}
