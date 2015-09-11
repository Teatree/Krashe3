package com.mygdx.game.system;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.stages.GameStage;
import com.mygdx.game.utils.GlobalConstants;

/**
 * Created by Teatree on 9/3/2015.
 */
public class CollisionSystem extends EntitySystem {

//    public static void checkCollisions(GameStage stage) {
//        checkCollisionBugs(stage);
//        checkCollisionUmbrella(stage);
//        checkCollisionButterfly(stage);
//    }
//
//    private static void checkCollisionBugs(GameStage stage) {
//        Flower flower = stage.game.flower;
//        Iterator<Bug> itr = ((GameStage) stage).game.getBugs().iterator();
//        while (itr.hasNext()) {
//            Bug bug = itr.next();
//            Rectangle posXrect = flower.getBounds();
//            Rectangle posXbug = bug.getController().getBoundsRectangle();
//            Rectangle posXbugBehind = new Rectangle(bug.getBounds().getX()-300, bug.getBounds().getY(),
//                    bug.getBounds().getWidth(), bug.getBounds().getHeight());
//
//            if (posXrect.overlaps(posXbug)) {
//                itr.remove();
//                stage.removeActor(bug.getCompositeItem());
//                flower.sessionPointsAmount += bug.getPoints();
//                if (bug.getController() instanceof QueenBeeBugController) {
//                    stage.game.angeredBeesTimer = GlobalConstants.ANGERED_BEES_MODE_DURATION;
//                    stage.game.isAngeredBeesMode = true;
//                    System.out.println("BEE MODE ACTIVATED");
//                }
//                System.out.println("I have " + flower.sessionPointsAmount + " points!");
//                flower.getController().eat();
//            }
//
//
//            if (isOutOfBounds(stage, posXbugBehind)) {
//                System.err.println("Current HP was: " + flower.getCurHp());
//                flower.addCurHp(-1);
//                System.err.println("TAKING SOME HP OFF OF YOU!");
//                System.err.println("Current HP: " + flower.getCurHp());
//                System.err.println("removing that bug who just took hp off of you, take that bitch...");
//                stage.removeActor(bug.getCompositeItem());
//                itr.remove();
//            }
//        }
//    }
//
//    private static void checkCollisionUmbrella(GameStage stage) {
//        if (stage.game.umbrellaPowerUp != null && stage.game.umbrellaPowerUp.getUmbrellaController() != null) {
//            Flower flower = stage.game.flower;
//            UmbrellaController uc = stage.game.umbrellaPowerUp.getUmbrellaController();
//
//            Rectangle posXrect = flower.getBounds();
//            Rectangle posXumbrella = uc.getBoundsRectangle();
//
//            if (posXrect.overlaps(posXumbrella)) {
//                flower.sessionPointsAmount *= 2;
//                stage.removeActor(uc.getCompositeItem());
//                stage.game.umbrellaPowerUp = null;
//                System.out.println("Doubling points!");
//                System.out.println("I now have " + flower.sessionPointsAmount + " points!");
//                flower.getController().eat();
//
//                Iterator<Bug> itr = ((GameStage) stage).game.getBugs().iterator();
//                while (itr.hasNext()) {
//                    Bug bug = itr.next();
//                    itr.remove();
//                    stage.removeActor(bug.getCompositeItem());
//                }
//            }
//
//            if (isOutOfBounds(stage, posXumbrella)) {
//                uc.pushUmbrella(450, 500, 45, 55);
//            }
//        }
//    }
//
//    private static void checkCollisionButterfly(GameStage stage) {
//        if (stage.game.butterflyPowerUp != null && stage.game.butterflyPowerUp.getButterflyController() != null) {
//            Flower flower = stage.game.flower;
//            ButterflyController bc = stage.game.butterflyPowerUp.getButterflyController();
//
//            Rectangle posXrect = flower.getBounds();
//            Rectangle posXbutterfly = bc.getBoundsRectangle();
//            Rectangle posXbutterflyRectBehind = new Rectangle(posXbutterfly.getX() - 300, posXbutterfly.getY(), posXbutterfly.getWidth(), posXbutterfly.getHeight());
//
//            if (posXrect.overlaps(posXbutterfly)) {
//                flower.sessionPointsAmount += 200;
//                stage.removeActor(bc.getCompositeItem());
//                stage.game.butterflyPowerUp = null;
//                System.out.println("Giving 200!");
//                System.out.println("I now have " + flower.sessionPointsAmount + " points!");
//                flower.getController().eat();
//            }
//
//            if (isOutOfBounds(stage, posXbutterflyRectBehind)) {
//                stage.removeActor(stage.game.butterflyPowerUp.getCompositeItem());
//                System.out.println("Removing the lost butterfly, farewell");
//            }
//        }
//    }
//
//    public static boolean isOutOfBounds(GameStage stage, Rectangle boundsRect) {
//        if (boundsRect.getX() >= stage.game.flower.getBounds().getX() + stage.game.flower.getBounds().getWidth() + 100) {
//            return true;
//        }
//        return false;
//    }
}
