package pepse.world.daynight;
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;

/***
 * The class represents the halo sun.
 */
public class SunHalo {
    public static final float FACTOR = 2f;
    public static final String SUN_HALO_TAG = "sun halo";

    /**
     * This function creates a halo around a given object that represents the sun. The halo will be tied to
     * the given sun, and will always move with it.
     * @param gameObjects - The collection of all participating game objects.
     * @param layer - The number of the layer to which the created halo should be added.
     * @param sun - A game object representing the sun (it will be followed by the created game object).
     * @param color - The color of the halo.
     * @return A new game object representing the sun's halo.
     */
    public static GameObject create(GameObjectCollection gameObjects, int layer, GameObject sun, Color color)
    {
        GameObject sunHalo = new GameObject(Vector2.ZERO, sun.getDimensions().mult(FACTOR), new OvalRenderable(color));
        sunHalo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(sunHalo, layer);
        sunHalo.setTag(SUN_HALO_TAG);
        return sunHalo;
    }
}
