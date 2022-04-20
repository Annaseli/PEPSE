package pepse.world;

import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.util.PerlinNoise;

import java.awt.*;

/**
 * This class responsible for the creation and management of terrain.
 */
public class Terrain {

    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private static final String GROUND_TAG = "ground";
    private static final int TERRAIN_DEPTH = 20;
    public static final int NOISE_COEFFICIENT = 250;
    public static final int NOISE_DIVIDE = 30;
    public static final float PARTIAL_WINDOW = 2 / 3f;
    public static final String GROUND_TOP_TAG = "ground top";
    public static final int ADDITIANL_LAYER_BOTTOM_GROUND = 3;

    private final GameObjectCollection gameObjects;
    private final int groundLayer;
    private final Vector2 windowDimensions;
    private final PerlinNoise perlinNoise;

    /**
     * Constructor.
     * @param gameObjects - The collection of all participating game objects.
     * @param groundLayer - The number of the layer to which the created ground objects should be added.
     * @param windowDimensions - The dimensions of the windows.
     * @param seed - A seed for a random number generator.
     */
    public Terrain(GameObjectCollection gameObjects, int groundLayer, Vector2 windowDimensions, int seed) {
        this.gameObjects = gameObjects;
        this.groundLayer = groundLayer;
        this.windowDimensions = windowDimensions;
        this.perlinNoise = new PerlinNoise(seed);
    }

    /**
     * This method return the ground height at a given location.
     * @param x -  A number.
     * @return - The ground height at the given location.
     */
    public float groundHeightAt(float x) {
        float noise =  NOISE_COEFFICIENT * perlinNoise.noise(x/ NOISE_DIVIDE) + windowDimensions.y() *
                PARTIAL_WINDOW;
        return (float) (Math.floor(noise / Block.SIZE)*Block.SIZE);
    }

    /**
     * This method creates terrain in a given range of x-values.
     * @param minX - The lower bound of the given range (will be rounded to a multiple of Block.SIZE).
     * @param maxX - The upper bound of the given range (will be rounded to a multiple of Block.SIZE).
     */
    public void createInRange(int minX, int maxX) {
        int newMinX = (int)(Math.floor(minX/(float)Block.SIZE)*Block.SIZE);
        int newMaxX = (int)(Math.ceil(maxX/(float)Block.SIZE)*Block.SIZE);
        for (int x = newMinX; x < newMaxX; x += Block.SIZE) {
            int groundHeight = (int)groundHeightAt(x);
            for (int y = groundHeight; y <  groundHeight + TERRAIN_DEPTH * Block.SIZE; y+=Block.SIZE)
            {
                Vector2 blockLoc = new Vector2(x, y);
                Block block = new Block(blockLoc,
                        new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR)));
                if (y==groundHeight || y==groundHeight + Block.SIZE || y==groundHeight + 2*Block.SIZE ||
                        y==groundHeight + 3*Block.SIZE)
                {
                    gameObjects.addGameObject(block, groundLayer);
                    block.setTag(GROUND_TOP_TAG);
                }
                else
                {
                    gameObjects.addGameObject(block, groundLayer + ADDITIANL_LAYER_BOTTOM_GROUND);
                    block.setTag(GROUND_TAG);

                }
            }
        }
    }
}