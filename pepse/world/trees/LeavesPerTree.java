package pepse.world.trees;

import danogl.collisions.GameObjectCollection;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.world.Block;

import java.awt.*;
import java.util.Objects;
import java.util.Random;

/**
 * The class represents the leaves per tree in the window.
 */
public class LeavesPerTree {
    private static final int FADEOUT_TIME = 10;
    public static final int[] LEAVES_LOCATIONS = {0, 3, 6, 7, 9, 11, 15, 17, 20, 22, 23, 24, 27, 28, 30,
            32, 33, 36, 39, 42, 45, 48};
    public static final int LEAVES_SQUARE_DIM = 7;
    public static final int LEAVES_DIFF_DIM_LEFT = 3;
    public static final int LEAVES_DIFF_DIM_RIGHT = 4;
    public static final Color COLOR_GREEN = new Color(50, 200, 30);
    public static final String LEAF_TAG = "leaf";
    public static final String FALLING_LEAF_TAG = "falling leaf";
    public static final int ITERATION_COUNTER = 0;
    public static final int LOC_IN_ARRAY = 0;
    public static final int LEAF_SIZE = 30;
    public static final int LEAF_NEW_SIZE = 35;
    public static final int TRANSITION_TIME_WIDTH = 5;
    public static final int TRANSITION_TIME_ANGLE = 5;
    public static final float SET_VELOCITY_Y_ = 0f;
    public static final float SET_VELOCITY_Y_RESURRECT_LEAVES = SET_VELOCITY_Y_;
    public static final float INITIAL_VALUE_ANGLE = 0f;
    public static final float FINAL_VALUE_ANGLE = 30f;
    public static final int BOUND_TIME_MOVE_LEAVES = 10;
    public static final int FADE_IN_TIME = 0;
    public static final int BOUND_TIME_DEATH = 50;
    public static final int BOUND_TIME_LIFE = 150;
    public static final int SUBTRUCT_LAYER_FALLING_LEAVES = 5;
    public static final float VELOCITY_Y_FALLING_LEAF = 70f;
    public static final float INITIAL_VALUE_FALLING = -60f;
    public static final float FINAL_VALUE_FALLING = 60f;
    public static final int TRANSITION_TIME_FALLING = 2;

    private final float treesTrunkCol;
    private final int treeHeight;
    private final GameObjectCollection gameCollection;
    private final int layer;
    private final Random rand;

    /**
     * Constructor.
     * @param treesTrunkCol - the column in the current window of the trunk.
     * @param treeHeight - number of blocks in the tree trunk
     * @param gameCollection - the game collection of all the objects  in the game.
     * @param layer - the layer of the leave.
     * @param seed - the seed of the game.
     */
    public LeavesPerTree(float treesTrunkCol, int treeHeight, GameObjectCollection gameCollection, int layer,
                         int seed) {
        this.treesTrunkCol = treesTrunkCol;
        this.treeHeight = treeHeight;
        this.gameCollection = gameCollection;
        this.layer = layer;
        this.rand = new Random(Objects.hash(treesTrunkCol, seed));
        createLeaves();
    }

    /**
     * The function constructors all the leaves in the constant location, by calling the Leaf class.
     */
    private void createLeaves() {
        int iterationCounter = ITERATION_COUNTER;
        int locInArray = LOC_IN_ARRAY;
        for(int row = treeHeight - LEAVES_SQUARE_DIM * Block.SIZE; row < treeHeight; row += Block.SIZE)
        {
            for(int col = (int)(treesTrunkCol - LEAVES_DIFF_DIM_LEFT * Block.SIZE); col <
                    (int)(treesTrunkCol + LEAVES_DIFF_DIM_RIGHT * Block.SIZE); col += Block.SIZE )
            {
                if (iterationCounter == LEAVES_LOCATIONS[locInArray]) {
                    Vector2 blockLoc = new Vector2(col, row);
                    Leaf leaf = new Leaf(blockLoc, new RectangleRenderable(COLOR_GREEN),
                            treesTrunkCol);
                    leaf.setTag(LEAF_TAG);
                    moveLeaves(leaf);
                    lifeTime(leaf);
                    gameCollection.addGameObject(leaf, layer);
                    locInArray++;
                }
                iterationCounter++;
            }
        }
    }

    /**
     * The function represents the leaf growth in the width.
     * @param leaf - the current leaf
     */
    private void transitionWidth(Leaf leaf)
    {
        new Transition<Vector2>(leaf, leaf::setDimensions, new Vector2(LEAF_SIZE, LEAF_SIZE),
                new Vector2(LEAF_NEW_SIZE, LEAF_SIZE),
                Transition.LINEAR_INTERPOLATOR_VECTOR, TRANSITION_TIME_WIDTH,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
    }

    /**
     * The function represents the leaf angel transition.
     * @param leaf - the current leaf
     */
    private void transitionAngle(Leaf leaf)
    {
        new Transition<Float>(leaf, leaf.renderer()::setRenderableAngle, INITIAL_VALUE_ANGLE,
                FINAL_VALUE_ANGLE, Transition.LINEAR_INTERPOLATOR_FLOAT, TRANSITION_TIME_ANGLE,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
    }

    /**
     * The function represents movements the leaves in the wind.
     * @param leaf - the current leaf
     */
    private void moveLeaves(Leaf leaf) {
        new ScheduledTask(leaf, rand.nextInt(BOUND_TIME_MOVE_LEAVES), true,
                () -> {
            transitionAngle(leaf);
            transitionWidth(leaf);
        } );
    }

    /**
     * After a random time, the leaf restores its location in its original location in the tree.
     * @param leaf leaf to get back to the tree.
     */
    private void resurrectLeaf(Leaf leaf, Vector2 loc) {
        leaf.renderer().fadeIn(FADE_IN_TIME);
        leaf.setCenter(loc);
        leaf.transform().setVelocityY(SET_VELOCITY_Y_RESURRECT_LEAVES);
        gameCollection.removeGameObject(leaf, layer - SUBTRUCT_LAYER_FALLING_LEAVES);
        gameCollection.addGameObject(leaf, layer);
        leaf.setTag(LEAF_TAG);
        lifeTime(leaf);
    }

    /**
     * Schedule time for the leaf to be dead - faded out.
     * @param leaf leaf that is being dead per random time until it resurrects.
     */
    private void deathTime(Leaf leaf, Vector2 loc) {
        new ScheduledTask(leaf, rand.nextInt(BOUND_TIME_DEATH), false, () -> resurrectLeaf(leaf, loc));
    }

    /**
     * The leaf drops and fades out for FADEOUT_TIME time from the tree to the ground.
     * @param leaf leaf that drops and fades out
     */
    private void fallingTime(Leaf leaf) {
        gameCollection.removeGameObject(leaf, layer);
        gameCollection.addGameObject(leaf, layer - SUBTRUCT_LAYER_FALLING_LEAVES);
        leaf.setTag(FALLING_LEAF_TAG);
        Vector2 loc = leaf.getCenter();
        leaf.renderer().fadeOut(FADEOUT_TIME, () -> deathTime(leaf, loc));
        leaf.transform().setVelocityY(VELOCITY_Y_FALLING_LEAF);
        Transition<Float> transition = new Transition<Float>(leaf, leaf.transform()::setVelocityX,
                INITIAL_VALUE_FALLING, FINAL_VALUE_FALLING,
                Transition.LINEAR_INTERPOLATOR_FLOAT, TRANSITION_TIME_FALLING,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null);
        leaf.setTransition(transition);
    }

    /**
     * Schedules time for a life cycle per one leaf.
     * @param leaf leaf that waits till death
     */
    private void lifeTime(Leaf leaf) {
        int temp = rand.nextInt(BOUND_TIME_LIFE);
        new ScheduledTask(leaf, temp, false, () -> fallingTime(leaf));
    }
}
