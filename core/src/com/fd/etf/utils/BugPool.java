package com.fd.etf.utils;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.fd.etf.entity.componets.BugComponent;
import com.fd.etf.stages.GameStage;
import com.fd.etf.system.BugSystem;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

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
    private static final String SIMPLE_BUG_ANI = "simpleBugAni";
    private static final String DRUNK_BUG_ANI = "drunkBugAni";
    private static final String CHARGER_BUG_ANI = "chargerBugAni";
    private static final String BEE_ANI = "beeAni";
    private static BugPool instance;
    private ComponentMapper<BugComponent> mapper = ComponentMapper.getFor(BugComponent.class);
    private static Stack<Entity> simpleBugs = new Stack<>();
    private static Stack<Entity> bees = new Stack<>();
    private static Stack<Entity> drunkBugs = new Stack<>();
    private static Stack<Entity> chargerBugs = new Stack<>();
    private static Entity queenBee;

    public static List<Entity> bugsOnStage = new ArrayList<>();

    private GameStage gameStage;

    private BugPool(GameStage gameStage) {
        this.gameStage = gameStage;
        final ItemWrapper root = new ItemWrapper(gameStage.sceneLoader.getRoot());
        simpleBugs.add(root.getChild(SIMPLE_BUG_ANI_1).getEntity());
        bees.add(root.getChild(BEE_ANI_1).getEntity());
        drunkBugs.add(root.getChild(DRUNK_BUG_ANI_1).getEntity());
        chargerBugs.add(root.getChild(CHARGER_ANI_1).getEntity());
        queenBee = root.getChild(QUEEN_BEE_ANI_1).getEntity();

        simpleBugs.add(root.getChild(SIMPLE_BUG_ANI_2).getEntity());
//        bees.add(root.getChild(BEE_ANI_2).getEntity());
        drunkBugs.add(root.getChild(DRUNK_BUG_ANI_2).getEntity());

        simpleBugs.add(root.getChild(SIMPLE_BUG_ANI_3).getEntity());
//        bees.add(root.getChild(BEE_ANI_3).getEntity());
    }

    public static BugPool getInstance(GameStage gameStage) {
        if (instance == null) {
            instance = new BugPool(gameStage);
        }

//        System.out.println("SIMPLE  " + simpleBugs.size());
//        System.out.println("DRUNK  " + drunkBugs.size());
//        System.out.println("BEE  " + bees.size());
//        System.out.println("CHARGER  " + chargerBugs.size());
        return instance;
    }

    public static void resetBugPool(GameStage gameStage) {
        instance = new BugPool(gameStage);
    }

    public Entity get(String type) {
        Entity newBug = null;

        switch (type) {
            case SIMPLE: {
                if (simpleBugs.isEmpty()) {
                    newBug = loadBugFromLib(SIMPLE_BUG_ANI);
                } else {
                    newBug =  simpleBugs.pop();
                }
                break;
            }
            case DRUNK: {
                if (drunkBugs.isEmpty()) {
                    newBug =  loadBugFromLib(DRUNK_BUG_ANI);
                } else {
                    newBug =  drunkBugs.pop();
                }
                break;
            }
            case CHARGER: {
                if (chargerBugs.isEmpty()) {
                    newBug =  loadBugFromLib(CHARGER_BUG_ANI);
                } else {
                    newBug =  chargerBugs.pop();
                }
                break;
            }
            case BEE: {
                if (bees.isEmpty()) {
                    newBug =  loadBugFromLib(BEE_ANI);
                } else {
                    newBug =  bees.pop();
                }
                break;
            }
            case QUEENBEE: {
                newBug =  queenBee;
                break;
            }
        }
        bugsOnStage.add(newBug);
        return newBug;
    }

    public void removeBugsFromStage (){
        for (Entity bug : bugsOnStage){
            release(bug);
        }
    }

    private Entity loadBugFromLib(String bugLib) {
        CompositeItemVO tempItemC = gameStage.gameScript.gameStage.sceneLoader.loadVoFromLibrary(bugLib);
        gameStage.gameScript.gameStage.sceneLoader.rm.addSPRITEtoLoad(bugLib);
        Entity bugE = gameStage.gameScript.gameStage.sceneLoader.entityFactory.
                createSPRITEentity(gameStage.gameScript.gameStage.sceneLoader.getRoot(), tempItemC);
        gameStage.gameScript.gameStage.sceneLoader.getEngine().addEntity(bugE);
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

//    public static void resetAllBugs() {
//        releaseAll(simpleBugs);
//        releaseAll(drunkBugs);
//        releaseAll(bees);
//        releaseAll(chargerBugs);
//        if (queenBee != null && queenBee.getComponent(TransformComponent.class) != null) {
//            queenBee.getComponent(TransformComponent.class).x = GlobalConstants.FAR_FAR_AWAY_X;
//            queenBee.getComponent(TransformComponent.class).y = GlobalConstants.FAR_FAR_AWAY_Y;
//        }
//    }

    public static void sendFarFarAway(Stack<Entity> bugs) {
        for (Entity e : bugs) {
            if (e.getComponent(TransformComponent.class) != null) {
                e.getComponent(TransformComponent.class).x = GlobalConstants.FAR_FAR_AWAY_X;
                e.getComponent(TransformComponent.class).y = GlobalConstants.FAR_FAR_AWAY_Y;
            }
        }
    }

    public static void releaseAll(Stack<Entity> bugs) {
        for (Entity e : bugs) {
            if (e.getComponent(BugComponent.class) != null) {
                instance.release(e);
            }
        }
    }
}
