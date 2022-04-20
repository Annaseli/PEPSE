package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.*;
import danogl.util.Vector2;


import java.awt.event.KeyEvent;

/**
 * The class represents the avatar object in the game.
 */
public class Avatar extends GameObject {
    private static final float VELOCITY_X_RIGHT = 10 * Block.SIZE;
    private static final float VELOCITY_X_LEFT = -10 * Block.SIZE;
    private static final float VELOCITY_Y = -10 * Block.SIZE;
    private static final float GRAVITY = 300;
    private static final float INITIAL_ENERGY = 100f;
    public static final float AVATAR_SIZE = 30f;
    public static final float TIME_BETWEEN_CLIPS = 0.5f;
    public static final String AVATAR = "avatar";
    public static final float ENERGY_DIFF = 0.5f;
    public static final int MAX_ENERGY = 100;
    public static final int MIN_ENERGY = 0;
    public static final String STAY_PNG = "src/pepse/assets/stay.png";
    public static final String JUMP_1_PNG = "src/pepse/assets/jump1.png";
    public static final String JUMP_2_PNG = "src/pepse/assets/jump2.png";
    public static final String RUN_1_PNG = "src/pepse/assets/run1.png";
    public static final String RUN_2_PNG = "src/pepse/assets/run2.png";
    public static final String RUN_3_PNG = "src/pepse/assets/run3.png";
    public static final String FLY_1_PNG = "src/pepse/assets/fly1.png";
    public static final String FLY_2_PNG = "src/pepse/assets/fly2.png";
    public static final float ZERO_VELOCITY = 0f;

    private final UserInputListener inputListener;
    private final AnimationRenderable animationStand;
    private final AnimationRenderable animationRun;
    private final AnimationRenderable animationJump;
    private final AnimationRenderable animationFly;
    private float energy;
    private boolean flagJump;
    private boolean flagFly;


    /**
     * Constructor.
     * @param pos - avatar's positions.
     * @param inputListener - the input listener to the keys.
     * @param imageReader - the image reader.
     */
    public Avatar(Vector2 pos, UserInputListener inputListener, ImageReader imageReader) {
        super(pos, new Vector2(AVATAR_SIZE, AVATAR_SIZE),
                imageReader.readImage(STAY_PNG, true));
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        String[] standPics = {STAY_PNG};
        animationStand = new AnimationRenderable(standPics, imageReader, true, TIME_BETWEEN_CLIPS);
        String[] jumpPics = {STAY_PNG, JUMP_1_PNG, JUMP_2_PNG};
        animationJump = new AnimationRenderable(jumpPics, imageReader, true, TIME_BETWEEN_CLIPS);
        String[] runPics = {STAY_PNG, RUN_1_PNG, RUN_2_PNG, RUN_3_PNG};
        animationRun = new AnimationRenderable(runPics, imageReader, true, TIME_BETWEEN_CLIPS);
        String[] flyPics = {STAY_PNG, FLY_1_PNG, FLY_2_PNG};
        animationFly = new AnimationRenderable(flyPics, imageReader, true, TIME_BETWEEN_CLIPS);
        this.inputListener = inputListener;
        energy = INITIAL_ENERGY;
        flagJump = false;
        flagFly = false;
        this.setTag(AVATAR);
    }

    /**
     * This function creates an avatar that can travel the world and is followed by the camera.
     * The can stand, walk, jump and fly, and never reaches the end of the world.
     * @param gameObjects - The collection of all participating game objects.
     * @param layer - The number of the layer to which the created avatar should be added.
     * @param topLeftCorner - The location of the top-left corner of the created avatar.
     * @param inputListener - Used for reading input from the user.
     * @param imageReader - Used for reading images from disk or from within a jar.
     * @return  A newly created representing the avatar.
     */
    public static Avatar create(GameObjectCollection gameObjects, int layer, Vector2 topLeftCorner,
                                UserInputListener inputListener, ImageReader imageReader) {
        Avatar avatar = new Avatar(topLeftCorner, inputListener, imageReader);
        gameObjects.addGameObject(avatar, layer);
        return avatar;
    }

    /***
     * The function update the avatars state and position according to the user input.
     * @param deltaTime - time
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        float xVel = ZERO_VELOCITY;
        if ((flagFly) && getVelocity().x() != ZERO_VELOCITY && getVelocity().y() != ZERO_VELOCITY) {
            handleFlyState();
        }
        if (inputListener.isKeyPressed(KeyEvent.VK_LEFT)) {
            handleLeftKey(xVel);
        } else if (inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) {
            handleRightKey(xVel);
        } else if (inputListener.isKeyPressed(KeyEvent.VK_SPACE) &&
                inputListener.isKeyPressed(KeyEvent.VK_SHIFT)
                && getVelocity().x() == ZERO_VELOCITY) {
            handleFly();
        } else if (flagFly) {
            flagFly = false;
        } else if (inputListener.isKeyPressed(KeyEvent.VK_SPACE) && getVelocity().y() == ZERO_VELOCITY) {
            handleJump();
        } else if (flagJump && getVelocity().y() != ZERO_VELOCITY) {
            renderer().setRenderable(animationJump);
        } else if (flagJump) {
            flagJump = false;
        } else {
            if (energy < MAX_ENERGY) {
                energy += ENERGY_DIFF;
            }
            renderer().setRenderable(animationStand);
            transform().setVelocityX(ZERO_VELOCITY);
        }
    }

    /**
     * The function handles avatars jumping in the game by update his energy and setting the jump animation.
     */
    private void handleJump() {
        transform().setVelocityY(VELOCITY_Y);
        transform().setAccelerationY(GRAVITY);
        flagJump = true;
    }

    /**
     * The function handles avatars flying in the game by update his energy and setting the fly animation.
     */
    private void handleFlyState() {
        renderer().setRenderable(animationFly);
        energy -= ENERGY_DIFF;
        if (energy <= MIN_ENERGY) {
            transform().setVelocityX(ZERO_VELOCITY);
            transform().setAccelerationY(GRAVITY);
        }
    }

    /**
     * The function handles users enter and shift keys pressed by adding velocity in y direction and zero the
     * x velocity of the avatar.
     */
    private void handleFly() {
        if (energy <= 0f) {
            transform().setAccelerationY(GRAVITY);
            transform().setVelocityX(ZERO_VELOCITY);
            return;
        }
        transform().setVelocityY(VELOCITY_Y);
        transform().setAccelerationY(GRAVITY);
        flagFly = true;
    }

    /**
     * The function handles users right key pressed by adding velocity in x direction and setting the run
     * animation.
     * @param xVel - x velocity.
     */
    private void handleRightKey(float xVel) {
        xVel += VELOCITY_X_RIGHT;
        transform().setVelocityX(xVel);
        renderer().setIsFlippedHorizontally(false);
        renderer().setRenderable(animationRun);
        transform().setAccelerationY(GRAVITY);
    }

    /**
     * The function handles users left key pressed by adding velocity in x direction and setting the run
     * animation.
     * @param xVel - x velocity.
     */
    private void handleLeftKey(float xVel) {
        xVel += VELOCITY_X_LEFT;
        transform().setVelocityX(xVel);
        renderer().setIsFlippedHorizontally(true);
        renderer().setRenderable(animationRun);
        transform().setAccelerationY(GRAVITY);
    }

}