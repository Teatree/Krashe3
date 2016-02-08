package com.mygdx.game.utils;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.mygdx.game.entity.componets.BugComponent;
import com.mygdx.game.entity.componets.BugType;
import com.mygdx.game.entity.componets.FlowerPublicComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.Stack;

import static com.mygdx.game.stages.GameStage.sceneLoader;

public class BugPool {

    public static final String SIMPLE = "SIMPLE";
    public static final String DRUNK = "DRUNK";
    public static final String CHARGER = "CHARGER";
    public static final String BEE = "BEE";
    public static final String QUEENBEE = "QUEENBEE";

    private  Stack<Entity> simpleBugs = new Stack<>();
    private  Stack<Entity> bees = new Stack<>();
    private  Stack<Entity> drunkBugs = new Stack<>();
    private  Stack<Entity> chargerBugs = new Stack<>();
    private  Entity queenBee;

    private static ComponentMapper<BugComponent> mapper = ComponentMapper.getFor(BugComponent.class);

    private static BugPool instance;

    public static BugPool getInstance(){
        if (instance == null){
            instance = new BugPool();
        }
        return instance;
    }

    public static void resetBugPool(){
        instance = new BugPool();
    }

    private BugPool () {
        ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());

        simpleBugs.add(root.getChild("simpleBugAni1").getEntity());
        simpleBugs.add(root.getChild("simpleBugAni2").getEntity());
        simpleBugs.add(root.getChild("simpleBugAni3").getEntity());
        simpleBugs.add(root.getChild("simpleBugAni4").getEntity());
        simpleBugs.add(root.getChild("simpleBugAni5").getEntity());
        simpleBugs.add(root.getChild("simpleBugAni6").getEntity());
        simpleBugs.add(root.getChild("simpleBugAni7").getEntity());
        simpleBugs.add(root.getChild("simpleBugAni8").getEntity());

        bees.add(root.getChild("beeAni1").getEntity());
        bees.add(root.getChild("beeAni2").getEntity());
        bees.add(root.getChild("beeAni3").getEntity());
        bees.add(root.getChild("beeAni4").getEntity());
        bees.add(root.getChild("beeAni5").getEntity());
        bees.add(root.getChild("beeAni6").getEntity());
        bees.add(root.getChild("beeAni7").getEntity());
        bees.add(root.getChild("beeAni8").getEntity());

        drunkBugs.add(root.getChild("drunkBugAni1").getEntity());
        drunkBugs.add(root.getChild("drunkBugAni2").getEntity());
        drunkBugs.add(root.getChild("drunkBugAni3").getEntity());
        drunkBugs.add(root.getChild("drunkBugAni4").getEntity());
        drunkBugs.add(root.getChild("drunkBugAni5").getEntity());
        drunkBugs.add(root.getChild("drunkBugAni6").getEntity());
        drunkBugs.add(root.getChild("drunkBugAni7").getEntity());
        drunkBugs.add(root.getChild("drunkBugAni8").getEntity());

        chargerBugs.add(root.getChild("chargerAni1").getEntity());
        chargerBugs.add(root.getChild("chargerAni2").getEntity());
        chargerBugs.add(root.getChild("chargerAni3").getEntity());
        chargerBugs.add(root.getChild("chargerAni4").getEntity());
        chargerBugs.add(root.getChild("chargerAni5").getEntity());
        chargerBugs.add(root.getChild("chargerAni6").getEntity());
        chargerBugs.add(root.getChild("chargerAni7").getEntity());
        chargerBugs.add(root.getChild("chargerAni8").getEntity());

        queenBee = root.getChild("queenBeeAni1").getEntity();
    }

    public Entity get(BugType type){
        switch (type.toString()){
            case SIMPLE : {
                return simpleBugs.pop();
            }
            case DRUNK : {
                return drunkBugs.pop();
            }
            case CHARGER : {
                return chargerBugs.pop();
            }
            case BEE : {
                return bees.pop();
            }
            case QUEENBEE : {
                return queenBee;
            }
        }
        return null;
    }

    public void release(Entity bug){
        BugComponent bc = mapper.get(bug);
        bc.velocity = 0;

        TransformComponent tc = ComponentRetriever.get(bug, TransformComponent.class);
        tc.x = -300;
        tc.y = -300;

        switch (bc.type.toString()){
            case SIMPLE : {
                simpleBugs.add(bug);
                break;
            }
            case DRUNK : {
                drunkBugs.add(bug);
                break;
            }
            case CHARGER : {
                chargerBugs.add(bug);
                break;
            }
            case BEE : {
                bees.add(bug);
                break;
            }
            case QUEENBEE : {
                break;
            }
        }
        bug.remove(BugComponent.class);
        bug.remove(FlowerPublicComponent.class);
    }
}
