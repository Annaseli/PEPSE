package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.util.Objects;
import java.util.Random;

/**
 * This function responsible for placing cloud in the game.
 */
public class Cloud extends GameObject {
    public static final float CLOUD_SIZE = 100f;
    public static final String CLOUD_1_JPEG = "src/pepse/assets/cloud1.jpeg";
    public static final String CLOUD_TAG = "cloud";
    public static final int MIN_RANDOM_X_BOUND = 800;
    public static final int DIIF_RANDOM_X = 1000;
    public static final int MIN_RANDOM_Y_BOUND = 70;
    public static final int DIFF_RANDOM_Y = 100;

    /**
     * Constructor.
     * @param pos - cloud position in the game in Vector2.
     * @param renderable - cloud render.
     */
    public Cloud(Vector2 pos, Renderable renderable) {
        super(pos, new Vector2(CLOUD_SIZE, CLOUD_SIZE), renderable);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
    }

    /**
     * Creates clouds in range between the minX and maxX each x steps in random and in y random height.
     * @param gameObjects - collection of all the game objects.
     * @param layer - layer to place the cloud in the game.
     * @param imageReader - cloud image reader.
     * @param minX - min current window size.
     * @param maxX - max current window size.
     * @param seed - game's random seed.
     * @return the last cloud that was created.
     */
    public static Cloud create(GameObjectCollection gameObjects, int layer, ImageReader imageReader, int minX,
                               int maxX, int seed) {
        Cloud cloud = null;
        int newMinX = (int)(Math.floor(minX/(float)Block.SIZE)*Block.SIZE);
        int newMaxX = (int)(Math.ceil(maxX/(float)Block.SIZE)*Block.SIZE);
        int x = newMinX;
        while (x < newMaxX) {
            Random rand = new Random(Objects.hash(x, seed));
            cloud = new Cloud(new Vector2(x, - MIN_RANDOM_Y_BOUND + rand.nextInt(DIFF_RANDOM_Y)),
                    imageReader.readImage(CLOUD_1_JPEG, true));
            cloud.setTag(CLOUD_TAG);
            gameObjects.addGameObject(cloud, layer);
            x += MIN_RANDOM_X_BOUND + rand.nextInt(DIIF_RANDOM_X);
        }
        return cloud;
    }
}