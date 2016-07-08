package xyz.vec3d.game;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import xyz.vec3d.game.entities.Player;
import xyz.vec3d.game.entities.listeners.EntityTextureListener;
import xyz.vec3d.game.messages.RogueInputProcessor;
import xyz.vec3d.game.systems.RenderingSystem;

/**
 * Created by Daron on 7/5/2016.
 * Copyright vec3d.xyz 2016
 * All rights reserved
 *
 * Game state representation. Has a stage for UI and an Ashley engine for entity
 * related matters. Also manages the messaging system between the UI and engine.
 */
public class GameScreen implements Screen {

    /**
     * {@link PocketRogue} instance.
     */
    private PocketRogue pocketRogue;

    /**
     * The Ashley {@link com.badlogic.ashley.core.Engine} instance.
     */
    private Engine engine;

    /**
     * The {@link Stage} instance for UI.
     */
    private Stage uiStage;

    /**
     * The {@link TiledMapRenderer} responsible for drawing the world's map.
     */
    private TiledMapRenderer tiledMapRenderer;

    /**
     * The {@link OrthographicCamera} responsible for looking into the world map.
     */
    private OrthographicCamera worldCamera;

    /**
     * The {@link Player} of the game loaded for the game state. This is an
     * individual variable field so it can be readily accessed rather than having
     * to find it each time from the array of entities in the engine.
     */
    private Player player;

    /**
     * The {@link SpriteBatch} used to draw the entities. Each update tick the
     * projection matrix is set to that of the camera so that it is properly
     * drawing entities based on world units. This is passed to the
     * {@link RenderingSystem} to be used for drawing.
     */
    private SpriteBatch spriteBatch;

    /**
     * Creates a new {@link GameScreen} object and sets up the stage, engine and
     * any other initialization needed.
     *
     * @param pocketRogue The {@link PocketRogue} instance.
     */
    public GameScreen(PocketRogue pocketRogue) {
        this.pocketRogue = pocketRogue;
        this.engine = new Engine();
        this.uiStage = new Stage();
        this.spriteBatch = new SpriteBatch();
        setUpGui();
        setUpEngine();
    }

    /**
     * Initializes all the UI components and registers and listeners or handlers
     * for the components.
     */
    private void setUpGui() {
        //Create the stage and viewport for the UI.
        uiStage = new Stage(new StretchViewport(Settings.WIDTH, Settings.HEIGHT));

        //Set up input multiplexer
        InputMultiplexer im = new InputMultiplexer(uiStage, new RogueInputProcessor(this));
        Gdx.input.setInputProcessor(im);
    }

    /**
     * Initializes the engine and registers systems for the engine as well as
     * loads the map and camera.
     */
    private void setUpEngine() {
        //Create engine instance, attach listeners and systems.
        engine = new Engine();
        engine.addSystem(new RenderingSystem(spriteBatch));
        engine.addEntityListener(new EntityTextureListener(this));
        player = new Player(10, 10);
        engine.addEntity(player);

        //Create camera and load map and bind them together.
        tiledMapRenderer = new OrthogonalTiledMapRenderer(
                pocketRogue.getAssetManager().get("map.tmx", TiledMap.class),
                Settings.WORLD_SCALE);
        worldCamera = new OrthographicCamera();
        worldCamera.setToOrtho(false, 25, 14);
        worldCamera.update();
    }

    public PocketRogue getPocketRogue() {
        return pocketRogue;
    }

    /**
     * Called when this screen becomes the current screen for a {@link com.badlogic.gdx.Game}.
     */
    @Override
    public void show() {

    }

    /**
     * Called when the screen should render itself.
     *
     * Renders the TiledMap, updates the UI stage, draws the UI stage then finally
     * updates the engine.
     *
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        worldCamera.update();
        tiledMapRenderer.setView(worldCamera);
        tiledMapRenderer.render();
        uiStage.act(delta);
        uiStage.draw();

        spriteBatch.setProjectionMatrix(worldCamera.combined);
        spriteBatch.begin();
        engine.update(delta);
        spriteBatch.end();
    }

    /**
     * @param width
     * @param height
     * @see com.badlogic.gdx.ApplicationListener#resize(int, int)
     */
    @Override
    public void resize(int width, int height) {

    }

    /**
     * @see com.badlogic.gdx.ApplicationListener#pause()
     */
    @Override
    public void pause() {

    }

    /**
     * @see com.badlogic.gdx.ApplicationListener#resume()
     */
    @Override
    public void resume() {

    }

    /**
     * Called when this screen is no longer the current screen for a {@link com.badlogic.gdx.Game}.
     */
    @Override
    public void hide() {

    }

    /**
     * Called when this screen should release all resources.
     */
    @Override
    public void dispose() {

    }
}
