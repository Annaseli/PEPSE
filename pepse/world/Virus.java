package pepse.world;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.components.ScheduledTask;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.Renderable;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;

import java.util.Objects;
import java.util.Random;

public class Virus extends GameObject {
    private GameObjectCollection gameObjects;
    int layer;

    public Virus(Vector2 pos, Renderable renderable, GameObjectCollection gameObjects, int layer) {
        super(pos, new Vector2(30f, 30f), renderable);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        this.gameObjects = gameObjects;
        this.layer = layer;
    }

    public static Virus create(GameObjectCollection gameObjects, int layer, ImageReader imageReader, int minX) {
        //Random rand = new Random(Objects.hash(minX,seed));
        //irus[] viruses = new Virus[4];
        //new Vector2(minX + rand.nextInt(maxX - minX), 0)
        Virus virus1 = new Virus(new Vector2(minX - 300, 50), imageReader.readImage("star.jpeg", true), gameObjects, layer);
//        Virus virus2 = new Virus(new Vector2(minX + rand.nextInt(maxX - minX), 0), imageReader.readImage("corona2.png", true), gameObjects, layer);
//        Virus virus3 = new Virus(new Vector2(minX + rand.nextInt(maxX - minX), 0), imageReader.readImage("corona3.png", true), gameObjects,layer);
//        Virus virus4 = new Virus(new Vector2(minX + rand.nextInt(maxX - minX), 0), imageReader.readImage("corona4.png", true), gameObjects,layer);
//        new ScheduledTask(virus1, rand.nextInt(10), false, () -> virus1.setVelocity(Vector2.DOWN.multY(100f)));
//        new ScheduledTask(virus1, rand.nextInt(10), false, () -> virus2.setVelocity(Vector2.DOWN.multY(100f)));
//        new ScheduledTask(virus1, rand.nextInt(10), false, () -> virus3.setVelocity(Vector2.DOWN.multY(100f)));
//        new ScheduledTask(virus1, rand.nextInt(10), false, () -> virus4.setVelocity(Vector2.DOWN.multY(100f)));
        virus1.setVelocity(new Vector2(100f, 100f));
//        virus2.setVelocity(Vector2.DOWN.multY(100f));
//        virus3.setVelocity(Vector2.DOWN.multY(100f));
//        virus4.setVelocity(Vector2.DOWN.multY(100f));
        virus1.setTag("virus");
//        virus2.setTag("virus");
//        virus3.setTag("virus");
//        virus4.setTag("virus");
        gameObjects.addGameObject(virus1, layer);
//        gameObjects.addGameObject(virus2, layer);
//        gameObjects.addGameObject(virus3, layer);
//        gameObjects.addGameObject(virus4, layer);
        return virus1;
    }

//    @Override
//    public void onCollisionEnter(GameObject other, Collision collision) {
//        super.onCollisionEnter(other, collision);
//        if (other.getTag().equals("avatar")) {
//            Avatar avatar = (Avatar) other;
//            WindowController windowController = avatar.getWindowController();
//            if (avatar.getLives() == 0) {
//                windowController.showMessageBox("You lost");
//                windowController.closeWindow();
//                return;
//            }
//            avatar.setLives(avatar.getLives() - 1);
//            gameObjects.removeGameObject(this, Layer.BACKGROUND + 50);
//            gameObjects.removeGameObject(avatar.getCurNum(), Layer.BACKGROUND + 60);
//            avatar.setCurNum(new GameObject(new Vector2(400, 300), new Vector2(30, 30),
//                    new TextRenderable(String.format("Lives left: %s", avatar.getLives()))));
//            gameObjects.addGameObject(avatar.getCurNum(), Layer.BACKGROUND + 60);
//            windowController.showMessageBox("You lost one life");
//            if (this.getTopLeftCorner().y() > avatar.getTerrain().groundHeightAt(other.getTopLeftCorner().x()) - 30) {
//                transform().setTopLeftCorner(new Vector2(this.getTopLeftCorner().x(), avatar.getTerrain().groundHeightAt(this.getTopLeftCorner().x())-30));
//            }
//        }
//    }

}