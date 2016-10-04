package com.mygdx.etf.utils;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.mygdx.etf.entity.componets.BugComponent;
import com.mygdx.etf.stages.GameStage;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.Stack;

import static com.mygdx.etf.stages.GameStage.sceneLoader;

public class BugPool {

    public static final String SIMPLE = "SIMPLE";
    public static final String DRUNK = "DRUNK";
    public static final String CHARGER = "CHARGER";
    public static final String BEE = "BEE";
    public static final String QUEENBEE = "QUEENBEE";

    public static final String SIMPLE_BUG_ANI_1 = "simpleBugAni1";
    public static final String BEE_ANI_1 = "beeAni1";
    public static final String DRUNK_BUG_ANI_1 = "drunkBugAni1";
    public static final String CHARGER_ANI_1 = "chargerAni1";
    public static final String QUEEN_BEE_ANI_1 = "queenBeeAni1";
    public static final String SIMPLE_BUG_ANI_2 = "simpleBugAni2";
    public static final String BEE_ANI_2 = "beeAni2";
    public static final String DRUNK_BUG_ANI_2 = "drunkBugAni2";
    public static final String SIMPLE_BUG_ANI_3 = "simpleBugAni3";
    public static final String BEE_ANI_3 = "beeAni3";
    private static BugPool instance;
    private ComponentMapper<BugComponent> mapper = ComponentMapper.getFor(BugComponent.class);
    private static Stack<Entity> simpleBugs = new Stack<>();
    private static Stack<Entity> bees = new Stack<>();
    private static Stack<Entity> drunkBugs = new Stack<>();
    private static Stack<Entity> chargerBugs = new Stack<>();
    private static Entity queenBee;

    private BugPool() {
        final ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());
        simpleBugs.add(root.getChild(SIMPLE_BUG_ANI_1).getEntity());
        bees.add(root.getChild(BEE_ANI_1).getEntity());
        drunkBugs.add(root.getChild(DRUNK_BUG_ANI_1).getEntity());
        chargerBugs.add(root.getChild(CHARGER_ANI_1).getEntity());
        queenBee = root.getChild(QUEEN_BEE_ANI_1).getEntity();

        simpleBugs.add(root.getChild(SIMPLE_BUG_ANI_2).getEntity());
        bees.add(root.getChild(BEE_ANI_2).getEntity());
        drunkBugs.add(root.getChild(DRUNK_BUG_ANI_2).getEntity());

        simpleBugs.add(root.getChild(SIMPLE_BUG_ANI_3).getEntity());
        bees.add(root.getChild(BEE_ANI_3).getEntity());
    }

    public static BugPool getInstance() {
        if (instance == null) {
            instance = new BugPool();
        }
        return instance;
    }

    public static void resetBugPool() {
        instance = new BugPool();
    }

    public Entity get(String type) {
        switch (type) {
            case SIMPLE: {
                if(simpleBugs.isEmpty()){
                    return loadBugFromLib("simpleBugAni");
                } else {
                    return simpleBugs.pop();
                }
            }
            case DRUNK: {
                if(drunkBugs.isEmpty()){
                    return loadBugFromLib("drunkBugAni");
                } else {
                    return drunkBugs.pop();
                }
            }
            case CHARGER: {
//                if(chargerBugs.isEmpty()){
                    return loadBugFromLib("chargerBugAni");
//                } else {
//                    return chargerBugs.pop();
//                }
            }
            case BEE: {
                if(bees.isEmpty()){
                    return loadBugFromLib("beeAni");
                } else {
                    return bees.pop();
                }
            }
            case QUEENBEE: {
                return queenBee;
            }
        }
        return null;
    }

    private Entity loadBugFromLib(String bugLib) {
        CompositeItemVO tempItemC = GameStage.sceneLoader.loadVoFromLibrary(bugLib);
        sceneLoader.rm.addSPRITEtoLoad(bugLib);
        Entity bugE = GameStage.sceneLoader.entityFactory.createSPRITEentity(GameStage.sceneLoader.getRoot(), tempItemC);
        GameStage.sceneLoader.getEngine().addEntity(bugE);
        return bugE;
    }

    public void release(Entity bug) {
        BugComponent bc = mapper.get(bug);
        if (bc != null) {
            bc.state = BugComponent.IDLE;
            bc.velocity = 0;

            bug.getComponent(TransformComponent.class).x = GlobalConstants.FAR_FAR_AWAY_X;
            bug.getComponent(TransformComponent.class).y = GlobalConstants.FAR_FAR_AWAY_Y;

            switch (bc.type) {
                case SIMPLE: {
                    simpleBugs.add(bug);
                    break;
                }
                case DRUNK: {
                    drunkBugs.add(bug);
                    break;
                }
                case CHARGER: {
                    chargerBugs.add(bug);
                    break;
                }
                case BEE: {
                    bees.add(bug);
                    break;
                }
                case QUEENBEE: {
                    break;
                }
            }
            bug.remove(BugComponent.class);
        }
    }

    public static void resetAllBugs(){
        releaseAll(simpleBugs);
        releaseAll(drunkBugs);
        releaseAll(bees);
        releaseAll(chargerBugs);
        if (queenBee.getComponent(TransformComponent.class) != null){
            queenBee.getComponent(TransformComponent.class).x = GlobalConstants.FAR_FAR_AWAY_X;
            queenBee.getComponent(TransformComponent.class).y = GlobalConstants.FAR_FAR_AWAY_Y;
        }
    }

    public static void sendFarFarAway(Stack<Entity> bugs){
        for (Entity e: bugs){
            if (e.getComponent(TransformComponent.class) != null){
                e.getComponent(TransformComponent.class).x = GlobalConstants.FAR_FAR_AWAY_X;
                e.getComponent(TransformComponent.class).y = GlobalConstants.FAR_FAR_AWAY_Y;
            }
        }
    }

    public static void releaseAll(Stack<Entity> bugs){
        for (Entity e: bugs){
            if (e.getComponent(BugComponent.class) != null){
                instance.release(e);
            }
        }
    }
}
