package io.github.bacclean;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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

    // ShapeRenderer for rendering bounds
    private ShapeRenderer movementBoundRender;
    private ShapeRenderer attackBoundRender;

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
        baccleanPlayer.setPosition(0, 60);
        loadMap();

        // Initialize ShapeRenderer
        movementBoundRender = new ShapeRenderer();
        attackBoundRender = new ShapeRenderer();
    }

    private void loadMap() {
        TmxMapLoader mapLoader = new TmxMapLoader(new InternalFileHandleResolver());
        map = mapLoader.load("maps/map-1.tmx"); //each frame = 16px.

        // Crea el renderer para el mapa
        mapRenderer = new OrthogonalTiledMapRenderer(map, spriteBatch);
        mapRenderer.setView(camera);
    }
    
    @Override
    public void render(float delta) {
        input();
        logic();
        camera.position.set(baccleanPlayer.getX() + baccleanPlayer.getWidth() / 2, baccleanPlayer.getY() + 176, 0);       
        camera.update();
        draw();
    }


    private void input() {
        float delta = Gdx.graphics.getDeltaTime();
        baccleanPlayer.playerMove(delta);
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
        Color customColor = new Color(0, 0, 34 / 255f, 1);
        ScreenUtils.clear(customColor);
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
        // Render the bounds
        renderPlayerBounds();
    }


    private void renderPlayerBounds() {
        // Set the projection matrix for the movement bounds renderer and begin drawing
        movementBoundRender.setProjectionMatrix(camera.combined);
        movementBoundRender.begin(ShapeRenderer.ShapeType.Line);
        movementBoundRender.setColor(Color.RED); // Set color for movement bounds
    
        // Draw the collision bounds for movement
        movementBoundRender.rect(baccleanPlayer.movementBounds.x, baccleanPlayer.movementBounds.y, baccleanPlayer.movementBounds.width, baccleanPlayer.movementBounds.height);
        movementBoundRender.end(); // End the movement bounds rendering
    
        // Set the projection matrix for the attack bounds renderer and begin drawing
        attackBoundRender.setProjectionMatrix(camera.combined);
        attackBoundRender.begin(ShapeRenderer.ShapeType.Line);
        attackBoundRender.setColor(Color.BLUE); // Set color for attack bounds
    
        // Draw the collision bounds for attack
        attackBoundRender.rect(baccleanPlayer.attackBounds.x, baccleanPlayer.attackBounds.y, baccleanPlayer.attackBounds.width, baccleanPlayer.attackBounds.height);
        attackBoundRender.end(); // End the attack bounds rendering
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
