package io.github.bacclean;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
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
    private final List<Rectangle> groundTileRectangles = new ArrayList<>();

    // ShapeRenderer for rendering bounds
    private ShapeRenderer playerBoundRender;
    private ShapeRenderer attackBoundRender;
    private ShapeRenderer groundBoundRender;
    private ShapeRenderer enemyBoundRender;
    public boolean showBounds;

    // Cursor
    private Cursor customCursor;

    // Lights
    LightsController lights;

    // Music
    private MusicController musicController;


    // Enemies
    private Enemy enemy;


    public GameScreen(Main game, OrthographicCamera camera, ExtendViewport extendViewport) {
        this.camera = camera;
        this.extendViewport = extendViewport;
        this.camera.zoom = 0.65f;
    }

    @Override
    public void show() {
        spriteBatch = new SpriteBatch();

        baccleanPlayer = new Player(
            "sprites_player/charles_idle.png", 6, 1,
            "sprites_player/charles_walk.png", 8, 1,
            "sprites_player/charles_attack.png", 10, 1,
            "sprites_player/charles_jump.png", 3, 1,
            "sprites_player/charles_fall.png", 5, 1);

        baccleanPlayer.setSize(160, 160);
        baccleanPlayer.setPosition(2500, 150); 

        enemy = new Enemy("enemies/skeleton/idle.png", 4, 1);
        enemy.setSize(73, 54);
        enemy.setPosition(2400, 64);
        

        loadMap();

        // Initialize ShapeRenderers
        playerBoundRender = new ShapeRenderer();
        attackBoundRender = new ShapeRenderer();
        groundBoundRender = new ShapeRenderer();
        enemyBoundRender = new ShapeRenderer();

        // Change cursor
        Gdx.input.setCursorCatched(false);
        Pixmap cursorTexture = new Pixmap(Gdx.files.internal("ui/cursor.png"));
        customCursor = Gdx.graphics.newCursor(cursorTexture, 0, 0);         
        Gdx.graphics.setCursor(customCursor);

        // Lights
        lights = new LightsController(camera);

        // Music
        musicController = new MusicController();
        musicController.playRandomMusic();
    }

    private void loadMap() {
        TmxMapLoader mapLoader = new TmxMapLoader(new InternalFileHandleResolver());
        map = mapLoader.load("maps/map-1.tmx");

        mapRenderer = new OrthogonalTiledMapRenderer(map, spriteBatch);
        mapRenderer.setView(camera);

        groundtileLayer = (TiledMapTileLayer) map.getLayers().get("ground");
        loadGroundTileRectangles(); // Load rectangles for collision detection
    }

    private void loadGroundTileRectangles() {
        if (groundtileLayer != null) {
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

                        // Add the rectangle to the list for collision detection
                        groundTileRectangles.add(new Rectangle(positionX, positionY, tileWidth, tileHeight));
                    }
                }
            }
        }
    }

    @Override
    public void render(float delta) {        
        handleInput();
        handlePlayerCollision();
        updateCamera();
        drawScene();
        lights.update(baccleanPlayer.playerBounds.x + (baccleanPlayer.playerBounds.width / 2), baccleanPlayer.getY());
        lights.render(camera);
        renderUI();
    }

    private void handleInput() {
        float delta = Gdx.graphics.getDeltaTime();
        baccleanPlayer.playerMove(delta);

        if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
            showBounds = !showBounds; // Toggle the boolean flag
        }
    }
        

    private void handlePlayerCollision() {
        baccleanPlayer.checkGroundCollision(groundTileRectangles);
        baccleanPlayer.damageEnemy(enemy.enemyBounds);
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

        // Player
        TextureRegion currentFrame = baccleanPlayer.getCurrentFrame();
        spriteBatch.draw(currentFrame, baccleanPlayer.getX(), baccleanPlayer.getY(),
                baccleanPlayer.getWidth(), baccleanPlayer.getHeight());
        
        // Enemy
        TextureRegion enemyFrame = enemy.getCurrentFrame(); 
        spriteBatch.draw(enemyFrame, enemy.getX(), enemy.getY(),
                enemy.getWidth(), enemy.getHeight());              
        enemy.updateEnemyBounds();  
        spriteBatch.end();

        if (showBounds) {
            renderPlayerBounds();
            renderTileGroundBounds();
            renderEnemyBounds();
        }

        
    }

    private void renderPlayerBounds() {
        // Movement bounds
        playerBoundRender.setProjectionMatrix(camera.combined);
        playerBoundRender.begin(ShapeRenderer.ShapeType.Line);
        playerBoundRender.setColor(Color.WHITE);
        playerBoundRender.rect(
                baccleanPlayer.playerBounds.x,
                baccleanPlayer.playerBounds.y,
                baccleanPlayer.playerBounds.width,
                baccleanPlayer.playerBounds.height);
        playerBoundRender.end();

        // Attack bounds
        attackBoundRender.setProjectionMatrix(camera.combined);
        attackBoundRender.begin(ShapeRenderer.ShapeType.Line);
        attackBoundRender.setColor(Color.YELLOW);
        attackBoundRender.rect(
                baccleanPlayer.attackBounds.x,
                baccleanPlayer.attackBounds.y,
                baccleanPlayer.attackBounds.width,
                baccleanPlayer.attackBounds.height);
        attackBoundRender.end();
    }

    private void renderTileGroundBounds() {
        groundBoundRender.setProjectionMatrix(camera.combined);
        groundBoundRender.begin(ShapeRenderer.ShapeType.Line);
        groundBoundRender.setColor(Color.VIOLET); // Color for tile bounds

        // Draw the rectangle for each tile
        for (Rectangle tile : groundTileRectangles) {
            groundBoundRender.rect(tile.x, tile.y, tile.width, tile.height);
        }

        groundBoundRender.end();
    }

    private void renderEnemyBounds() {
        // Movement bounds
        enemyBoundRender.setProjectionMatrix(camera.combined);
        enemyBoundRender.begin(ShapeRenderer.ShapeType.Line);
        enemyBoundRender.setColor(Color.RED);
        enemyBoundRender.rect(
                enemy.enemyBounds.x,
                enemy.enemyBounds.y,
                enemy.enemyBounds.width,
                enemy.enemyBounds.height);
        enemyBoundRender.end();
    }


    public void renderUI(){
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
        map.dispose();
        playerBoundRender.dispose();
        attackBoundRender.dispose();
        groundBoundRender.dispose();
        customCursor.dispose();
        lights.dispose();
        musicController.dispose();
        enemy.dispose();
    }
}