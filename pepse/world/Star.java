package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * This class represents the start in the game that appears when the user presses 'S'.
 */
public class Star extends GameObject {
    private static final String STAR_JPEG = "src/pepse/assets/star.jpeg";
    private static final int LEFT_MOV_FACTOR = 300;
    private static final int STAR_HEIGHT = 30;
    private static final String STAR_TAG = "star";
    public static final Vector2 STAR_VELOCITY = new Vector2(100f, 100f);
    public static final Vector2 STAR_DIMENSIONS = new Vector2(30f, 30f);

    /**
     * Constructor.
     * @param pos - stars position
     * @param renderable - stars render
     */
    public Star(Vector2 pos, Renderable renderable) {
        super(pos, STAR_DIMENSIONS, renderable);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
    }

    /**
     * The function creates the star object and sets velocity.
     * @param gameObjects - all the objects in the game.
     * @param layer - stars layer
     * @param imageReader - stars image
     * @param minX - the current window minimal x
     * @return star object
     */
    public static Star create(GameObjectCollection gameObjects, int layer, ImageReader imageReader, int minX)
    {
        Star star = new Star(new Vector2(minX - LEFT_MOV_FACTOR, STAR_HEIGHT),
                imageReader.readImage(STAR_JPEG, true));
        star.setVelocity(STAR_VELOCITY);
        star.setTag(STAR_TAG);
        gameObjects.addGameObject(star, layer);
        return star;
    }
}