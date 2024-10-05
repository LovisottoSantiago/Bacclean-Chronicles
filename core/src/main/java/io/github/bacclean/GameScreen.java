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
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
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

    // TileMap
    private TiledMapTileLayer groundtileLayer;

    // ShapeRenderer for rendering bounds
    private ShapeRenderer movementBoundRender;
    private ShapeRenderer attackBoundRender;
    private ShapeRenderer groundBoundRender;
    public boolean showBounds;

    public GameScreen(Main game, OrthographicCamera camera, ExtendViewport extendViewport) {
        this.camera = camera;
        this.extendViewport = extendViewport;
        this.camera.zoom = 0.65f;
    }

    @Override
    public void show() {
        spriteBatch = new SpriteBatch();

        // Initialize Player
        baccleanPlayer = new Player(
                "sprites-player/player-idle.png", 8, 1, 
                "sprites-player/player-run.png", 8, 1, 
                "sprites-player/player-attack1.png", 8, 1);
        baccleanPlayer.setSize(288, 128);
        baccleanPlayer.setPosition(0, 60);

        loadMap();

        // Initialize ShapeRenderers
        movementBoundRender = new ShapeRenderer();
        attackBoundRender = new ShapeRenderer();
        groundBoundRender = new ShapeRenderer ();
    }

    private void loadMap() {
        TmxMapLoader mapLoader = new TmxMapLoader(new InternalFileHandleResolver());
        map = mapLoader.load("maps/map-1.tmx");

        mapRenderer = new OrthogonalTiledMapRenderer(map, spriteBatch);
        mapRenderer.setView(camera);

        groundtileLayer = (TiledMapTileLayer) map.getLayers().get("ground");
    }

    @Override
    public void render(float delta) {
        handleInput();
        updateLogic();
        updateCamera();
        drawScene();
    }

    private void handleInput() {
        float delta = Gdx.graphics.getDeltaTime();
        baccleanPlayer.playerMove(delta);

        if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
            showBounds = !showBounds; // Toggle the boolean flag
        }
        /* 
        if (Gdx.input.isKeyPressed(Input.Keys.PAGE_UP)) {
            camera.zoom = MathUtils.clamp(camera.zoom - 0.01f, 0.4f, 0.6f);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.PAGE_DOWN)) {
            camera.zoom = MathUtils.clamp(camera.zoom + 0.01f, 0.4f, 0.6f);
        }  */
    }

private void updateLogic() {
    // Update player movement
    


}


    private void updateCamera() {
        camera.position.set(
                baccleanPlayer.getX() + baccleanPlayer.getWidth() / 2, 
                baccleanPlayer.getY() + 176, 
                0);
        camera.update();
    }

    private void drawScene() {
        Color backgroundColor = new Color(0, 0, 34 / 255f, 1);
        ScreenUtils.clear(backgroundColor);

        // Render the map
        mapRenderer.setView(camera);
        mapRenderer.render();

        // Render the player
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        TextureRegion currentFrame = baccleanPlayer.getCurrentFrame();
        spriteBatch.draw(currentFrame, baccleanPlayer.getX(), baccleanPlayer.getY(), 
                baccleanPlayer.getWidth(), baccleanPlayer.getHeight());
        spriteBatch.end();

        // Render stamina bar
        baccleanPlayer.renderStaminaBar();

        if (showBounds) {
            renderPlayerBounds();
            renderTileGroundBounds();
        }

    }

    private void renderPlayerBounds() {
        // Movement bounds
        movementBoundRender.setProjectionMatrix(camera.combined);
        movementBoundRender.begin(ShapeRenderer.ShapeType.Line);
        movementBoundRender.setColor(Color.RED);
        movementBoundRender.rect(
                baccleanPlayer.movementBounds.x, 
                baccleanPlayer.movementBounds.y, 
                baccleanPlayer.movementBounds.width, 
                baccleanPlayer.movementBounds.height);
        movementBoundRender.end();

        // Attack bounds
        attackBoundRender.setProjectionMatrix(camera.combined);
        attackBoundRender.begin(ShapeRenderer.ShapeType.Line);
        attackBoundRender.setColor(Color.BLUE);
        attackBoundRender.rect(
                baccleanPlayer.attackBounds.x, 
                baccleanPlayer.attackBounds.y, 
                baccleanPlayer.attackBounds.width, 
                baccleanPlayer.attackBounds.height);
        attackBoundRender.end();
    }

    private void renderTileGroundBounds() {
        if (groundtileLayer != null) {
            groundBoundRender.setProjectionMatrix(camera.combined);
            groundBoundRender.begin(ShapeRenderer.ShapeType.Line);
            groundBoundRender.setColor(Color.VIOLET); // Color for tile bounds
    
            // Loop through all tiles in the ground layer
            for (int tileY = 0; tileY < groundtileLayer.getHeight(); tileY++) {
                for (int tileX = 0; tileX < groundtileLayer.getWidth(); tileX++) {
                    // Get the tile at the current position
                    TiledMapTileLayer.Cell currentCell = groundtileLayer.getCell(tileX, tileY);
                    if (currentCell != null) {
                        // Calculate the position and size of the tile
                        float tileWidth = groundtileLayer.getTileWidth();
                        float tileHeight = groundtileLayer.getTileHeight();
                        float positionX = tileX * tileWidth;
                        float positionY = tileY * tileHeight;
    
                        // Draw the rectangle for the tile
                        groundBoundRender.rect(positionX, positionY, tileWidth, tileHeight);
                    }
                }
            }
            groundBoundRender.end();
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
        spriteBatch.dispose();
        baccleanPlayer.dispose();
        map.dispose();
        movementBoundRender.dispose();
        attackBoundRender.dispose();
        groundBoundRender.dispose();
    }
}
