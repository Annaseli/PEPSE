package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.components.ScheduledTask;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;
import pepse.world.*;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.trees.Leaf;
import pepse.world.trees.Tree;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

/**
 * The class manage the game.
 */
public class PepseGameManager extends GameManager {
    public static final String SKY_TAG = "sky";
    public static final String SUN_TAG = "sun";
    public static final String NIGHT_TAG = "night";
    public static final String SUN_HALO_TAG = "sun halo";
    public static final String AVATAR_TAG = "avatar";
    public static final String TRUNK_TAG = "trunk";
    public static final String LEAF_TAG = "leaf";
    public static final String FALLING_LEAF_TAG = "falling leaf";
    public static final String GROUND_TAG = "ground";
    public static final String GROUND_TOP_TAG = "ground top";
    public static final String STAR_TAG = "star";
    public static final String CLOUD_TAG = "cloud";
    public static final String TEXT_TAG = "text";
    public static final String DEFAULT_TAG = "default";
    public static final String PRESS_S_MESSAGE = "Press S in rest for a falling star.";
    private static final float CYCLE_LENGTH = 30f;
    public static final int WAIT_TIME = 5;
    public static final int ADDITIONAL_LAYER_AVATAR = 50;
    public static final int MAX_STARS = 10;
    public static final int TARGET_FRAMERATE = 180;
    public static final int ADDITIONAL_LAYER_SUN = 10;
    public static final int ADDITIONAL_LAYER_SUN_HALO = 20;
    public static final int ADDITIONAL_LAYER_TRUNK = 30;
    public static final int ADDITIONAL_LAYER_LEAF = 40;
    public static final int ADDITIONAL_LAYER_GROUND = 3;
    public static final int ADDITIONAL_LAYER_STAR = 50;
    public static final int ADDITIONAL_LAYER_CLOUD = 60;
    public static final int ADDITIONAL_LAYER_TEXT = 70;
    public static final int STAR_COUNTER = 0;
    public static final int SEED_BOUND = 100;
    public static final int BUFFER = 600;
    public static final int BUFFER_FACTOR = 2;
    public static final int TEXT_X_MOVEMENT = 150;
    public static final int TEXT_Y = 100;
    public static final int TEXT_SIZE = 15;
    public static final float HALF_WINDOW = 0.5f;
    public static final float AVATAR_SIZE = 30f;
    public static final int ADDITIONAL_LAYER_FALLING_LEAF = 35;
    public static final Color COLOR_SUN_HALO = new Color(255, 255, 0, 20);
    public static final float HALF_DIM = 2f;
    public static final int FACTOR_BLOCK = 3;

    private static GameObjectCollection gameObjects;
    private static Vector2 windowDimensions;
    private static Terrain terrain;
    private static Tree tree;
    private static Avatar avatar;
    private static Vector2 prevWindow;
    private static HashMap<String, Integer> tagToLayer;
    private static Vector2 initialLoc;
    private static int seed;
    private static Random random;
    private static ImageReader imageReader;
    private static Vector2 newLocWindow;
    private static GameObject cloud;
    private static GameObject text;
    private UserInputListener inputListener;
    private boolean flagStar;
    private int starCounter;

    /**
     * The main function.
     * @param args - input user.
     */
    public static void main(String[] args)
    {
        new PepseGameManager().run();
    }

    /**
     * The function initialize all the game in the object in the range of the current window.
     * @param imageReader - the image reader.
     * @param soundReader = the sound reader.
     * @param inputListener - the input listener.
     * @param windowController - the window controller.
     */
    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader,
                               UserInputListener inputListener, WindowController windowController)
    {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        windowController.setTargetFramerate(TARGET_FRAMERATE);
        createLayersMap();
        prevWindow = Vector2.ZERO;
        windowDimensions = windowController.getWindowDimensions();
        gameObjects = gameObjects();
        this.imageReader = imageReader;
        this.inputListener = inputListener;
        flagStar = false;
        starCounter = STAR_COUNTER;
        seed = new Random().nextInt(SEED_BOUND);
        GameObject sky = Sky.create(gameObjects, windowDimensions, Layer.BACKGROUND);
        dayNightCreate();
        treesAndGroundCreate();
        defaultCreate();
        gameObjects().layers().shouldLayersCollide(Layer.BACKGROUND + ADDITIONAL_LAYER_FALLING_LEAF,
                Layer.STATIC_OBJECTS, true);
        avatarCreate();
        BonusCreate(imageReader);
    }

    /**
     * This method responsible for creating the avatar and setting the camera to avatar's center.
     */
    private void avatarCreate() {
        int x = (int) Math.floor((windowDimensions.x() / HALF_DIM) / Block.SIZE) * Block.SIZE;
        Vector2 avatarPos = new Vector2(x, terrain.groundHeightAt(x) - AVATAR_SIZE);
        avatar = Avatar.create(gameObjects, Layer.DEFAULT, avatarPos, inputListener, imageReader);
        setCamera(new Camera(avatar, windowDimensions.mult(HALF_WINDOW).add(avatarPos.mult(-1f)),
                windowDimensions, windowDimensions));
        gameObjects().layers().shouldLayersCollide(Layer.BACKGROUND + ADDITIONAL_LAYER_TRUNK,
                Layer.DEFAULT, true);
    }

    /**
     * This method responsible for creating the avatar and setting the camera to avatar's center.
     */
    private void defaultCreate() {
        GameObject defaultLeaf = new GameObject(Vector2.ZERO, Vector2.ZERO,
                new RectangleRenderable(Color.YELLOW));
        defaultLeaf.setTag(DEFAULT_TAG);
        gameObjects().addGameObject(defaultLeaf, Layer.BACKGROUND + ADDITIONAL_LAYER_FALLING_LEAF);
        gameObjects().addGameObject(defaultLeaf, Layer.STATIC_OBJECTS + ADDITIONAL_LAYER_GROUND);
    }

    /**
     * This method responsible for creating the clouds and the informative msg.
     */
    private void BonusCreate(ImageReader imageReader) {
        cloud = Cloud.create(gameObjects, Layer.BACKGROUND + ADDITIONAL_LAYER_CLOUD, imageReader, -
                        BUFFER, (int)windowDimensions.x() + BUFFER, seed);
        TextRenderable textRenderable = new TextRenderable(PRESS_S_MESSAGE);
        text = new GameObject(new Vector2(TEXT_X_MOVEMENT, TEXT_Y), new Vector2(TEXT_SIZE, TEXT_SIZE),
                textRenderable);
        text.setTag(TEXT_TAG);
        gameObjects.addGameObject(text, Layer.BACKGROUND + ADDITIONAL_LAYER_TEXT);
    }

    /**
     * This method responsible for creating the trees ang terrain.
     */
    private void treesAndGroundCreate() {
        terrain = new Terrain(gameObjects, Layer.STATIC_OBJECTS, windowDimensions, seed);
        terrain.createInRange(-BUFFER, (int)windowDimensions.x() + BUFFER);
        float xDiff = (int) Math.floor((windowDimensions.x() / HALF_DIM) / Block.SIZE) * Block.SIZE+
                Block.SIZE* FACTOR_BLOCK;
        float yDiff = terrain.groundHeightAt(xDiff) - AVATAR_SIZE;
        initialLoc = new Vector2(-xDiff, -yDiff);
        tree = new Tree(terrain::groundHeightAt, xDiff, gameObjects(),Layer.BACKGROUND +
                ADDITIONAL_LAYER_TRUNK, seed);
        tree.createInRange(-BUFFER, (int)windowDimensions.x() + BUFFER);
    }

    /**
     * This method responsible for creating the darkness object for day night effect.
     */
    private void dayNightCreate() {
        GameObject night = Night.create(gameObjects, Layer.FOREGROUND, windowDimensions, CYCLE_LENGTH);
        GameObject sun = Sun.create(gameObjects, Layer.BACKGROUND + ADDITIONAL_LAYER_SUN,
                windowDimensions, CYCLE_LENGTH);
        GameObject sunHalo = SunHalo.create(gameObjects, Layer.BACKGROUND + ADDITIONAL_LAYER_SUN_HALO,
                sun, COLOR_SUN_HALO);
        sunHalo.addComponent(deltaTime -> sunHalo.setCenter(sun.getCenter()));
    }

    /**
     * This method responsible for creating the map that maps objects to its layer.
     */
    private void createLayersMap() {
        tagToLayer = new HashMap<String, Integer>();
        tagToLayer.put(SKY_TAG, Layer.BACKGROUND);
        tagToLayer.put(SUN_TAG, Layer.BACKGROUND + ADDITIONAL_LAYER_SUN);
        tagToLayer.put(NIGHT_TAG, Layer.FOREGROUND);
        tagToLayer.put(SUN_HALO_TAG, Layer.BACKGROUND + ADDITIONAL_LAYER_SUN_HALO);
        tagToLayer.put(AVATAR_TAG, Layer.DEFAULT);
        tagToLayer.put(TRUNK_TAG, Layer.BACKGROUND + ADDITIONAL_LAYER_TRUNK);
        tagToLayer.put(LEAF_TAG, Layer.BACKGROUND + ADDITIONAL_LAYER_LEAF);
        tagToLayer.put(FALLING_LEAF_TAG, Layer.BACKGROUND + ADDITIONAL_LAYER_FALLING_LEAF);
        tagToLayer.put(GROUND_TAG, Layer.STATIC_OBJECTS + ADDITIONAL_LAYER_GROUND);
        tagToLayer.put(GROUND_TOP_TAG, Layer.STATIC_OBJECTS);
        tagToLayer.put(STAR_TAG, Layer.BACKGROUND + ADDITIONAL_LAYER_STAR);
        tagToLayer.put(CLOUD_TAG, Layer.BACKGROUND + ADDITIONAL_LAYER_CLOUD);
        tagToLayer.put(TEXT_TAG, Layer.BACKGROUND + ADDITIONAL_LAYER_TEXT);
    }

    /**
     * The function update all the objects in the game. The function create new objects from left and from
     * right to the window. In addition, the function removes objects.
     * @param deltaTime - time
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (inputListener.isKeyPressed(KeyEvent.VK_S) && !flagStar) {
            handleStar();
        }

        Vector2 avatarLoc = avatar.getTopLeftCorner();
        newLocWindow = avatarLoc.add(initialLoc);
        text.setTopLeftCorner(new Vector2(newLocWindow.x() + TEXT_X_MOVEMENT, text.getTopLeftCorner().y()));

        if(newLocWindow.x() - prevWindow.x() > BUFFER) {
            handleRightWindow();
        } else if(prevWindow.x() - newLocWindow.x() > BUFFER) { //left
            handleLeftWindow();
        }
    }

    /**
     * The function handles users S key pressed by adding to the game falling star animation.
     */
    private void handleStar() {
        starCounter++;
        if (starCounter < MAX_STARS) {
            Star star = Star.create(gameObjects, Layer.BACKGROUND + ADDITIONAL_LAYER_AVATAR,
                    imageReader, (int) avatar.getCenter().x());
        }
        flagStar = true;
        new ScheduledTask(avatar, WAIT_TIME, false, () -> flagStar = false);
    }

    /**
     * This function creates new trees, ground and clouds in left end of the world.
     */
    private void handleLeftWindow() {
        terrain.createInRange((int) (prevWindow.x() - BUFFER_FACTOR * BUFFER),
                (int) (prevWindow.x()) - BUFFER);
        tree.createInRange((int) (prevWindow.x() - BUFFER_FACTOR * BUFFER),
                (int) (prevWindow.x()) - BUFFER);
        cloud = Cloud.create(gameObjects, Layer.BACKGROUND + ADDITIONAL_LAYER_CLOUD, imageReader,
                (int) (prevWindow.x() - 2 * BUFFER), (int) (prevWindow.x()) - BUFFER, seed);
        random = new Random(Objects.hash(newLocWindow.x(), seed));
        removeObjects();
        prevWindow = newLocWindow;
    }

    /**
     * This function creates new trees, ground and clouds in right end of the world.
     */
    private void handleRightWindow() {
        terrain.createInRange((int) (prevWindow.x() + windowDimensions.x() + BUFFER),
                (int) (prevWindow.x() + windowDimensions.x() + BUFFER_FACTOR * BUFFER));
        tree.createInRange((int) (prevWindow.x() + windowDimensions.x() + BUFFER),
                (int) (prevWindow.x() + windowDimensions.x() + BUFFER_FACTOR * BUFFER));
        cloud = Cloud.create(gameObjects, Layer.BACKGROUND + ADDITIONAL_LAYER_CLOUD, imageReader,
                (int) (prevWindow.x() + windowDimensions.x() + BUFFER),
                (int) (prevWindow.x() + windowDimensions.x() + BUFFER_FACTOR * BUFFER), seed);
        random = new Random(Objects.hash(newLocWindow.x(), seed));
        removeObjects();
        prevWindow = newLocWindow;
    }

    /**
     * This function removes all the objects in the old window - objects that are no longer in the current
     * window.
     */
    private void removeObjects() {
        for (GameObject object : gameObjects) {
            if (object.getTag().equals(LEAF_TAG) || object.getTag().equals(FALLING_LEAF_TAG)) {
                Leaf leaf = (Leaf) object;
                if (leaf.getTrunkLocX() > (newLocWindow.x() + windowDimensions.x() + BUFFER) ||
                        leaf.getTrunkLocX() < newLocWindow.x() - BUFFER) {
                    gameObjects.removeGameObject(object, tagToLayer.get(object.getTag()));
                }
            } else if (object.getTopLeftCorner().x() > (newLocWindow.x() + windowDimensions.x() + BUFFER) ||
                    object.getTopLeftCorner().x() < newLocWindow.x() - BUFFER) {
                if (object.getTag().equals(GROUND_TAG) || object.getTag().equals(GROUND_TOP_TAG) ||
                        object.getTag().equals(TRUNK_TAG) || object.getTag().equals(CLOUD_TAG) ||
                        object.getTag().equals(TEXT_TAG)) {
                    gameObjects.removeGameObject(object, tagToLayer.get(object.getTag()));
                }
            }
        }
    }
}
