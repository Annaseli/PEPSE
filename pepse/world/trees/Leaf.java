package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.components.Transition;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Block;

/**
 * The class represents the leaf in the game.
 */
public class Leaf extends GameObject {
    public static final float ZERO_VELOCITY = 0f;
    public static final int SIZE = 30;

    private Transition<Float> transition;
    private final float trunkLocX;

    /**
     * Constructor.
     * @param topLeftCorner - the leaf's location.
     * @param renderable - the leaf's render.
     * @param trunkLoc - the trunk's location column.
     */
    public Leaf(Vector2 topLeftCorner, Renderable renderable, float trunkLoc) {
        super(topLeftCorner, Vector2.ONES.mult(SIZE), renderable);
        this.trunkLocX = trunkLoc;
    }

    /**
     * The function set the leaf's transition.
     * @param transition - leaf's horizontal transition.
     */
    public void setTransition(Transition<Float> transition)
    {
        this.transition = transition;
    }

    /**
     * The function gets the trunk that associates to that leaf.
     * @return the trunk's location.
     */
    public float getTrunkLocX()
    {
        return trunkLocX;
    }

    /**
     * The function stops the falling leaf's movement when the leaf collides with the ground.
     * @param other - the top of the ground
     * @param collision - collision event
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        removeComponent(transition);
        transform().setVelocityY(ZERO_VELOCITY);
        transform().setVelocityX(ZERO_VELOCITY);
        other.transform().setVelocityY(ZERO_VELOCITY);
        other.transform().setVelocityX(ZERO_VELOCITY);
    }
}
