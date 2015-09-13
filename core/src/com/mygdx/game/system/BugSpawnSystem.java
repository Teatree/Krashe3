package com.mygdx.game.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.mygdx.game.entity.componets.BugComponent;
import com.mygdx.game.entity.componets.BugType;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.sprite.SpriteAnimationStateComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.data.FrameRange;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.HashMap;
import java.util.Random;

/**
 * Created by Teatree on 9/3/2015.
 */
public class BugSpawnSystem extends EntitySystem {

    private int SPAWN_MAX_X = -400;
    private int SPAWN_MIN_X = 300;
    private int SPAWN_MIN_Y = -200;
    private int SPAWN_MAX_Y = 700;

    public static final String SIMPLE = "chargerAni";
    public static final String DRUNK = "drunkbugAni";
    public static final String CHARGER = "chargerAni";
    public static final String BEE = "chargerAni";
    public static final String QUEENBEE = "chargerAni";

    private HashMap<BugType, String> libBugsNameType = new HashMap<>();
    private HashMap<String, CompositeItemVO> libBugsNameComposite = new HashMap<>();

    private Random rand;
    private SceneLoader sl;

    private int spawnInterval = 200;

    public BugSpawnSystem( SceneLoader sl) {
        this.sl = sl;
        init();
    }

    private void init() {
        rand = new Random();

        SPAWN_MIN_X = -400;
        SPAWN_MIN_Y = 300;
        SPAWN_MAX_X = -200;
        SPAWN_MAX_Y = 700;

        libBugsNameType.put(BugType.SIMPLE, SIMPLE);
        libBugsNameType.put(BugType.DRUNK, DRUNK);
        libBugsNameType.put(BugType.CHARGER, CHARGER);
        libBugsNameType.put(BugType.BEE, BEE);
        libBugsNameType.put(BugType.QUEENBEE, QUEENBEE);

        libBugsNameComposite.put(SIMPLE, sl.loadVoFromLibrary(SIMPLE));
        libBugsNameComposite.put(DRUNK, sl.loadVoFromLibrary(DRUNK));
        libBugsNameComposite.put(CHARGER, sl.loadVoFromLibrary(CHARGER));
        libBugsNameComposite.put(BEE, sl.loadVoFromLibrary(BEE));
        libBugsNameComposite.put(QUEENBEE, sl.loadVoFromLibrary(QUEENBEE));
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
        int probabilityValue = rand.nextInt(100);
        if (probabilityValue < 10) {
            //Drunk
            tempC = libBugsNameComposite.get(DRUNK).clone();
            tempType = BugType.DRUNK;
        } else if (probabilityValue >= 10 && probabilityValue < 40) {
            //Simple
            tempC = libBugsNameComposite.get(SIMPLE).clone();
            tempType = BugType.SIMPLE;
        } else if (probabilityValue >= 41 && probabilityValue < 60) {
            //Charger
            tempC = libBugsNameComposite.get(CHARGER).clone();
            tempType = BugType.CHARGER;
        } else if (probabilityValue >= 61 && probabilityValue < 70 ){
            tempC = libBugsNameComposite.get(QUEENBEE).clone();
            tempType = BugType.QUEENBEE;
        } else {
            tempC = libBugsNameComposite.get(BEE).clone();
            tempType = BugType.BEE;
        }

        if(spawnInterval == 0){
            Entity bugEntity = sl.entityFactory.createEntity(sl.getRoot(), tempC);
            sl.entityFactory.initAllChildren(sl.getEngine(), bugEntity, tempC.composite);
            sl.getEngine().addEntity(bugEntity);

            BugComponent bc = new BugComponent();
            bc.startYPosition = getPos().y;
            bc.type = tempType;
//            SpriteAnimationStateComponent animationComponent = new SpriteAnimationStateComponent();
//            animationComponent.set(new FrameRange("ani", 1, 5), 20, Animation.PlayMode.LOOP_REVERSED);
            bugEntity.add(bc);
            bugEntity.add(getPos());

            spawnInterval = 100;
        } else {
            spawnInterval--;
        }
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        spawn();
    }
}
