package com.mygdx.game.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.mygdx.game.entity.componets.BugComponent;
import com.mygdx.game.entity.componets.BugType;
import com.mygdx.game.entity.componets.FlowerComponent;
import com.mygdx.game.entity.componets.FlowerPublicComponent;
import com.mygdx.game.stages.GameScreenScript;
import com.mygdx.game.utils.BugPool;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;

import java.util.HashMap;
import java.util.Random;

import static com.mygdx.game.utils.BugPool.*;

/**
 * Created by Teatree on 9/3/2015.
 */
public class BugSpawnSystem extends EntitySystem {

    private int SPAWN_MAX_X = -400;
    private int SPAWN_MIN_X = 300;
    private int SPAWN_MIN_Y = -200;
    private int SPAWN_MAX_Y = 700;

    public static boolean isAngeredBeesMode = false;
    public static boolean queenBeeOnStage = false;
    public static int angeredBeesModeTimer = 800;

//    public static final String SIMPLE = "simpleLib";
//    public static final String DRUNK = "drunkbugLib";
//    public static final String CHARGER = "chargerLib";
//    public static final String BEE = "beeLib";
//    public static final String QUEENBEE = "queenbeeLib";

    public FlowerPublicComponent fcc;
    private HashMap<BugType, String> libBugsNameType = new HashMap<>();
//    private HashMap<String, CompositeItemVO> libBugsNameComposite = new HashMap<>();

    private Random rand;

    private int spawnInterval = 200;

    public BugSpawnSystem(FlowerPublicComponent fcc) {
        this.fcc = fcc;
        init();
    }

    private void init() {

        rand = new Random();

        SPAWN_MIN_X = -400;
        SPAWN_MIN_Y = 100;
        SPAWN_MAX_X = -200;
        SPAWN_MAX_Y = 700;

        libBugsNameType.put(BugType.SIMPLE, SIMPLE);
        libBugsNameType.put(BugType.DRUNK, DRUNK);
        libBugsNameType.put(BugType.CHARGER, CHARGER);
        libBugsNameType.put(BugType.BEE, BEE);
        libBugsNameType.put(BugType.QUEENBEE, QUEENBEE);

//        libBugsNameComposite.put(SIMPLE, GameStage.sceneLoader.loadVoFromLibrary(SIMPLE));
//        libBugsNameComposite.put(DRUNK, GameStage.sceneLoader.loadVoFromLibrary(DRUNK));
//        libBugsNameComposite.put(CHARGER, GameStage.sceneLoader.loadVoFromLibrary(CHARGER));
//        libBugsNameComposite.put(BEE, GameStage.sceneLoader.loadVoFromLibrary(BEE));
//        libBugsNameComposite.put(QUEENBEE, GameStage.sceneLoader.loadVoFromLibrary(QUEENBEE));

    }

    private TransformComponent getPos(){
        TransformComponent transformComponent = new TransformComponent();
        transformComponent.x = rand.nextInt(SPAWN_MAX_X-SPAWN_MIN_X)+SPAWN_MIN_X;
        transformComponent.y = rand.nextInt(SPAWN_MAX_Y-SPAWN_MIN_Y)+SPAWN_MIN_Y;
        return transformComponent;
    }

    public void spawn() {
        CompositeItemVO tempC;
        BugType tempType;

        if(spawnInterval == 0){
            if (isAngeredBeesMode){
//            tempC = libBugsNameComposite.get(BEE).clone();
                tempType = BugType.BEE;
            }
            else{
                int probabilityValue = rand.nextInt(100);
                if (probabilityValue < 10) {
                    //Drunk
//                tempC = libBugsNameComposite.get(DRUNK).clone();
                    tempType = BugType.DRUNK;
                } else if (probabilityValue >= 10 && probabilityValue < 40) {
                    //Simple
//                tempC = libBugsNameComposite.get(SIMPLE).clone();
                    tempType = BugType.SIMPLE;
                } else if (probabilityValue >= 41 && probabilityValue < 60 ) {
                    //Charger
//                tempC = libBugsNameComposite.get(CHARGER).clone();
                    tempType = BugType.CHARGER;
                } else if (probabilityValue >= 61 && probabilityValue < 70 ){
//                tempC = libBugsNameComposite.get(QUEENBEE).clone();
                    tempType = BugType.QUEENBEE;
                    queenBeeOnStage = true;
                } else {
//                tempC = libBugsNameComposite.get(BEE).clone();
                    tempType = BugType.BEE;
                }
            }
//            Entity bugEntity = GameStage.sceneLoader.entityFactory.createEntity(GameStage.sceneLoader.getRoot(), tempC);
            Entity bugEntity = BugPool.getInstance().get(tempType);
//            GameStage.sceneLoader.entityFactory.initAllChildren(GameStage.sceneLoader.getEngine(), bugEntity, tempC.composite);
//            GameStage.sceneLoader.getEngine().addEntity(bugEntity);

            BugComponent bc = new BugComponent();
            TransformComponent tc = getPos();
            bc.startYPosition = tc.y;
            bc.type = tempType;
            bc.state = BugComponent.State.IDLE;
            bugEntity.add(bc);
            bugEntity.add(fcc);

            bugEntity.remove(TransformComponent.class);
            bugEntity.add(tc);

            spawnInterval = 100;
        } else {
            spawnInterval--;
        }
    }

    @Override
    public void update(float deltaTime) {
        if (!GameScreenScript.isPause && !GameScreenScript.isGameOver) {
            if(GameScreenScript.isStarted) {
                super.update(deltaTime);
                spawn();

                if (isAngeredBeesMode) {
                    angeredBeesModeTimer--;
                    if (angeredBeesModeTimer <= 0) {
                        isAngeredBeesMode = false;
                        angeredBeesModeTimer = 800;
                    }
                }
            }
        }
    }
}
