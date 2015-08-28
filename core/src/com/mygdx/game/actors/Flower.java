//package com.mygdx.game.actors;
//
//import com.badlogic.ashley.core.Entity;
//import com.badlogic.gdx.math.Rectangle;
//import com.mygdx.game.actors.controllers.FlowerController;
//import com.mygdx.game.stages.GameStage;
//import com.mygdx.game.utils.GlobalConstants;
//import com.uwsoft.editor.renderer.SceneLoader;
//import com.uwsoft.editor.renderer.components.NodeComponent;
//import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
//import com.uwsoft.editor.renderer.components.sprite.SpriteAnimationComponent;
//import com.uwsoft.editor.renderer.data.ProjectInfoVO;
//import com.uwsoft.editor.renderer.utils.ComponentRetriever;
//import com.uwsoft.editor.renderer.utils.ItemWrapper;
//
///**
// * Created by MainUser on 21/07/2015.
// */
//public class Flower {
//
//    public static long pointsAmount = 0L;
//    public static long sessionPointsAmount = 0L;
//
//    private int maxHp = GlobalConstants.DEFAULT_MAX_HP;
//    private int curHp = maxHp;
//    private SpriteAnimationComponent spriteAniComponent;
//
//    private FlowerController controller;
//    private ItemWrapper itemWrapper;
//    private ProjectInfoVO projectInfoVO;
//
//    public Flower(FlowerController controller, Entity item) {
//        this.controller = controller;
//        itemWrapper = new ItemWrapper(item);
//        itemWrapper.addScript(controller);
//
//        projectInfoVO = controller.stage.sceneLoader.getRm().getProjectVO();
//        projectInfoVO.libraryItems.get("flowerLib2");
//
////        controller.saFlower = itemWrapper.getChild("flowerC").getComponent("floweridle_ani", SpriteAnimationComponent.class);
//        controller.saFlower = flowerLib.getSpriterActorById("floweridle_ani");
//        controller.saFlower = controller.stage.sceneLoader.loadFromLibrary("floweridle_ani").getComponent(SpriteAnimationComponent.class);
//
//        controller.saHead = itemWrapper.getSpriterActorById("flower_head2");
//        controller.saHead.setVisible(false);
//        controller.itemPeduncleImg = flowerLib.getImageById("flower_peduncle");
//        controller.itemPeduncleImg.setVisible(false);
//    }
//
//    public static void init(GameStage stage, SceneLoader loader){
//        Flower flower = new Flower(new FlowerController(stage),
//                loader.getRm().getProjectVO().libraryItems.get("flowerLib2"));
//        stage.game.flower = flower;
//
//        flower.setPosition(1800, -585);
//
//        stage.addActor(flower.getFlowerLib());
//    }
//
//    public int getCurHp() {
//        return curHp;
//    }
//
//    public void setCurHp(int curHp) {
//        this.curHp = curHp;
//    }
//
//    public void addCurHp(int hpToAdd){
//        setCurHp(getCurHp()+hpToAdd);
//    }
//
//    public void setPosition(int x, int y) {
//        flowerLib.setPosition(x, y);
//    }
//
//    public Rectangle getBounds(){
//       return controller.headBoundsRect;
//    }
//
//    public FlowerController getController() {
//        return controller;
//    }
//
//    public void setController(FlowerController controller) {
//        this.controller = controller;
//    }
//
//    public CompositeItem getFlowerLib() {
//        return flowerLib;
//    }
//
//    public void setFlowerLib(CompositeItem flowerLib) {
//        this.flowerLib = flowerLib;
//    }
//}
