package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * The class represents the sun - moves across the sky in an elliptical path.
 */
public class Sun {

    private static final String SUN_TAG = "sun";
    private static final float SIZE = 100f;
    private static final float INITIAL_ANGEL = 0f;
    private static final float FINAL_ANGEL = 360f;
    private static final float FACTOR1 = 2f;
    private static final float FACTOR2 = 1.9f;
    public static final float HALF = 0.5f;

    /**
     * This function creates a yellow circle that moves in the sky in an elliptical path (in camera coordinates).
     *
     * @param gameObjects - The collection of all participating game objects.
     * @param layer - The number of the layer to which the created sun should be added.
     * @param windowDimensions - The dimensions of the windows.
     * @param cycleLength - The amount of seconds it should take the created game object to complete a full
     * cycle.
     * @return A new game object representing the sun.
     */
    public static GameObject create(GameObjectCollection gameObjects, int layer,
                                    Vector2 windowDimensions, float cycleLength) {
        GameObject sun = new GameObject(windowDimensions, new Vector2(SIZE, SIZE),
                new OvalRenderable(Color.YELLOW));
        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(sun, layer);
        sun.setTag(SUN_TAG);
        new Transition<Float>(sun, angle -> sun.setCenter(new Vector2(
                windowDimensions.x()/FACTOR1 - (float) Math.cos(Math.toRadians(angle)+ Math.PI * HALF) *
                        (windowDimensions.x() - SIZE)/FACTOR1,
                windowDimensions.y()/FACTOR2 - (float) Math.sin(Math.toRadians(angle) + Math.PI * HALF) *
                        (windowDimensions.y() - SIZE)/FACTOR1)),
                INITIAL_ANGEL, FINAL_ANGEL, Transition.LINEAR_INTERPOLATOR_FLOAT, cycleLength,
                Transition.TransitionType.TRANSITION_LOOP, null);
        return sun;
    }
}
