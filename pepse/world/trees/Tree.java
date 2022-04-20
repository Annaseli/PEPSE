package pepse.world.trees;

import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.world.Block;

import java.awt.*;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;

/**
 * This class responsible for the creation and management of trees.
 */
public class Tree {
    public static final String TRUNK_TAG = "trunk";
    public static final int THRESHOLD = 10;
    public static final int RANDOM_BOUND = 100;
    public static final int ADDITIONAL_LAYER_TRUNK = 10;
    public static final int RED = 100;
    public static final int GREEN = 50;
    public static final int BLUE = 20;
    public static final int MIN_RANDOM_BOUND = 5;
    public static final int DIFF_RANDOM_BOUND = 5;
    private final Function<Float, Float> groundHeightCallback;
    private final float initialAvatarPosX;
    private final GameObjectCollection gameCollection;
    private final int layer;
    private final int seed;


    /**
     * Constructor.
     * @param groundHeightCallback - callback for a terrain function that returns the ground height at a given
     *                             location - column in the current window.
     * @param initialAvatarPosX - initial avatar position.
     * @param gameCollection - collection of all the objects in the game.
     * @param layer - layer to add the tree's trunk to.
     * @param seed - the games seed to use in random.
     */
    public Tree(Function<Float, Float> groundHeightCallback, float initialAvatarPosX,
                GameObjectCollection gameCollection, int layer, int seed) {
        this.groundHeightCallback = groundHeightCallback;
        this.initialAvatarPosX = initialAvatarPosX;
        this.gameCollection = gameCollection;
        this.layer = layer;
        this.seed = seed;
    }

    /**
     * This method generates a number from 0 to 100 and returns true if it less than 60.
     * @param x - trees column in the window.
     * @return - true if it less than 60 anf false otherwise.
     */
    private boolean createObj(int x) {
        return new Random(Objects.hash(x,seed)).nextInt(RANDOM_BOUND) < THRESHOLD;
    }

    /**
     * This method constructs all the tree's trunk in the current window.
     * @param x - trees column in the window.
     */
    private void createTrunk(float x) {
        float groundHeight = groundHeightCallback.apply(x);
        int numBlocks = new Random(Objects.hash(x,seed)).nextInt(DIFF_RANDOM_BOUND) + MIN_RANDOM_BOUND;
        for (int y = (int)groundHeight - numBlocks * Block.SIZE ; y < groundHeight; y += Block.SIZE) {
            Vector2 blockLoc = new Vector2(x, y);
            Block block = new Block(blockLoc, new RectangleRenderable(new Color(RED, GREEN, BLUE)));
            block.setTag(TRUNK_TAG);
            gameCollection.addGameObject(block, layer);
        }
        createLeaves(x, (int)groundHeight - numBlocks * Block.SIZE);
    }

    /**
     * This method constructs all the leaves in a tree.
     * @param x - trees column in the window.
     * @param treeHeight - number of blocks in trees trunk.
     */
    private void createLeaves(float x, int treeHeight)
    {
        new LeavesPerTree(x, treeHeight, gameCollection, layer + ADDITIONAL_LAYER_TRUNK, seed);
    }

    /**
     * This method creates trees in a given range of x-values.
     * @param minX - The lower bound of the given range (will be rounded to a multiple of Block.SIZE).
     * @param maxX - The upper bound of the given range (will be rounded to a multiple of Block.SIZE).
     */
    public void createInRange(int minX, int maxX) {
        int newMinX = (int)(Math.floor(minX/(float)Block.SIZE)*Block.SIZE);
        int newMaxX = (int)(Math.ceil(maxX/(float)Block.SIZE)*Block.SIZE);

        for (int x = newMinX; x < newMaxX; x += Block.SIZE) {
            if (createObj(x) && x != initialAvatarPosX) {
                createTrunk(x);
            }
        }
    }
}