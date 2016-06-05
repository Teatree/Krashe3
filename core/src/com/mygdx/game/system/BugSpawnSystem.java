package com.mygdx.game.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.mygdx.game.entity.componets.BugComponent;
import com.mygdx.game.entity.componets.FlowerPublicComponent;
import com.mygdx.game.stages.GameStage;
import com.mygdx.game.utils.BugPool;
import com.uwsoft.editor.renderer.components.TransformComponent;

import java.util.List;
import java.util.Random;

import static com.mygdx.game.entity.componets.BugComponent.*;
import static com.mygdx.game.entity.componets.CocoonComponent.*;
import static com.mygdx.game.entity.componets.DandelionComponent.*;
import static com.mygdx.game.entity.componets.Goal.GoalType.SURVIVE_N_ANGERED_MODES;
import static com.mygdx.game.stages.GameScreenScript.*;

public class BugSpawnSystem extends EntitySystem {

    // spawn probability
    // combined has to be equal to 100
    public static final int DRUNK_SPAWN_PROB = 70;
    public static final int SIMPLE_SPAWN_PROB = 1;
    public static final int CHARGER_SPAWN_PROB = 1;
    public static final int QUEENBEE_SPAWN_PROB = 10;
    public static final int BEE_SPAWN_PROB = 18;

    public static int curDrunkProb = DRUNK_SPAWN_PROB;
    public static int curSimpleProb = SIMPLE_SPAWN_PROB;
    public static int curChargerProb = CHARGER_SPAWN_PROB;
    public static int curQueenBeeProb = QUEENBEE_SPAWN_PROB;
    public static int curBeeProb = BEE_SPAWN_PROB;

    public static int ANGERED_BEES_MODE_DURATION = 800;
    public static boolean isAngeredBeesMode = false;
    public static boolean queenBeeOnStage = false;
    public static int angeredBeesModeTimer = ANGERED_BEES_MODE_DURATION;
    public FlowerPublicComponent fcc;

    private int SPAWN_MAX_X = -400;
    private int SPAWN_MIN_X = 300;
    private int SPAWN_MIN_Y = 100;
    private int SPAWN_MAX_Y = 620;

    private float spawner = 0;
    private float break_counter = 0;
    private float SPAWN_INTERVAL_BASE = 1.5f;
    private float BREAK_FREQ_BASE_MIN = 10;
    private float BREAK_FREQ_BASE_MAX = 15;
    private float BREAK_LENGTH_BASE_MIN = 3;
    private float BREAK_LENGTH_BASE_MAX = 6;

    private float curSpawnInterval = SPAWN_INTERVAL_BASE;
    private float curBreakFreqMin = BREAK_FREQ_BASE_MIN;
    private float curBreakFreqMax = BREAK_FREQ_BASE_MAX;
    private float curBreakLengthMin = BREAK_LENGTH_BASE_MIN;
    private float curBreakLengthMax = BREAK_LENGTH_BASE_MAX;

    public static List<Multiplier> mulipliers;
    public static Multiplier currentMultiplier;
    public int bugsSpawned;

    private Random rand = new Random();

    public BugSpawnSystem(FlowerPublicComponent fcc) {
        this.fcc = fcc;
        this.fcc = fcc;
        init();
    }

    public static boolean isBlewUp() {
        return isAngeredBeesMode & angeredBeesModeTimer == ANGERED_BEES_MODE_DURATION - 10;
    }

    private void init() {
        SPAWN_MIN_X = -300;
        SPAWN_MIN_Y = 100;
        SPAWN_MAX_X = -200;
        SPAWN_MAX_Y = 700;

        break_counter = rand.nextInt((int)curBreakFreqMax- (int)curBreakFreqMin)+ curBreakFreqMin;
        currentMultiplier = mulipliers.get(0);
    }

    private TransformComponent getPos(BugComponent bc) {
        TransformComponent transformComponent = new TransformComponent();
        transformComponent.x = rand.nextInt(SPAWN_MAX_X - SPAWN_MIN_X) + SPAWN_MIN_X;
        transformComponent.y = rand.nextInt(SPAWN_MAX_Y - SPAWN_MIN_Y) + SPAWN_MIN_Y;
        bc.endX = 1450;
        bc.endY = transformComponent.y;

        return transformComponent;
    }

    public void spawn(float delta) {
        break_counter -= delta;
        if (spawner <= 0) {
            if (isAngeredBeesMode) {
                createBug(BEE, currentMultiplier);
            } else {
                int probabilityValue = rand.nextInt(100);
                if (probabilityValue <= curDrunkProb) {
                    createBug(DRUNK, currentMultiplier);
                } else if (probabilityValue > curDrunkProb && probabilityValue < curDrunkProb + curSimpleProb) {
                    createBug(SIMPLE, currentMultiplier);
                } else if (probabilityValue >= curDrunkProb + curSimpleProb + 1 && probabilityValue < curDrunkProb + curSimpleProb + curChargerProb) {
                    createBug(CHARGER, currentMultiplier);
                } else if (probabilityValue >= curDrunkProb + curSimpleProb + curChargerProb + 1 && probabilityValue < curDrunkProb + curSimpleProb + curChargerProb + curQueenBeeProb) {
                    if (!queenBeeOnStage) {
                        createBug(QUEENBEE, currentMultiplier);
                        queenBeeOnStage = true;
                    }
                } else if (probabilityValue >= curDrunkProb + curSimpleProb + curChargerProb + curQueenBeeProb + 1 &&
                        probabilityValue < curDrunkProb + curSimpleProb + curChargerProb + curQueenBeeProb + curBeeProb) {
                    createBug(BEE, currentMultiplier);
                }
                bugsSpawned++;
//                System.out.println("bugSpawned: " + bugsSpawned);
//                System.out.println("currentMultimplierDrunkBug: " + currentMultiplier.drunkBugSpawnChance);
            }

                if(break_counter > 0) {
                    spawner = curSpawnInterval;
                }else{
                    spawner = rand.nextInt((int)curBreakLengthMax-(int)curBreakLengthMin)+curBreakLengthMin;;
                    break_counter = rand.nextInt((int)curBreakFreqMax - (int)curBreakFreqMin)+ curBreakFreqMin;;
                }
            } else {
                spawner -= delta;
            }
//        System.out.println("spanwer: " + spawner);
//        System.out.println("break_counter: " + break_counter);
    }

    private void createBug(String tempType, Multiplier currentMultiplier) {
        Entity bugEntity = BugPool.getInstance().get(tempType);
        BugComponent bc = new BugComponent(tempType, currentMultiplier);
        bugEntity.add(bc);
        bugEntity.add(fcc);

        TransformComponent tc = getPos(bc);
        bc.startYPosition = tc.y;
        bugEntity.getComponent(TransformComponent.class).x = tc.x;
        bugEntity.getComponent(TransformComponent.class).y = tc.y;
    }

    @Override
    public void update(float deltaTime) {
        curSpawnInterval = SPAWN_INTERVAL_BASE * currentMultiplier.spawnInterval * GameStage.gameScript.fpc.level.spawnInterval;
        curBreakFreqMin = BREAK_FREQ_BASE_MIN * currentMultiplier.breakFreqMin * GameStage.gameScript.fpc.level.breakFreqMin;
        curBreakFreqMax = BREAK_FREQ_BASE_MAX * currentMultiplier.breakFreqMax * GameStage.gameScript.fpc.level.breakFreqMax;
        curBreakLengthMin = BREAK_LENGTH_BASE_MIN * currentMultiplier.breakLengthMin * GameStage.gameScript.fpc.level.breakLengthMin;
        curBreakLengthMax = BREAK_LENGTH_BASE_MAX * currentMultiplier.breakLengthMax * GameStage.gameScript.fpc.level.breakLengthMax;

        curDrunkProb = (int)(DRUNK_SPAWN_PROB * currentMultiplier.drunkBugSpawnChance * GameStage.gameScript.fpc.level.drunkBugSpawnChance);
        curSimpleProb = (int)(SIMPLE_SPAWN_PROB * currentMultiplier.simpleBugSpawnChance * GameStage.gameScript.fpc.level.simpleBugSpawnChance);
        curChargerProb = (int)(CHARGER_SPAWN_PROB * currentMultiplier.chargerBugSpawnChance * GameStage.gameScript.fpc.level.chargerBugSpawnChance);
        curQueenBeeProb = (int)(QUEENBEE_SPAWN_PROB * currentMultiplier.queenBeeSpawnChance * GameStage.gameScript.fpc.level.queenBeeSpawnChance);
        curBeeProb = (int)(BEE_SPAWN_PROB * currentMultiplier.beeSpawnChance * GameStage.gameScript.fpc.level.beeSpawnChance);

        if (!isPause && !isGameOver) {
            if (isStarted) {
                super.update(deltaTime);
                if(bugsSpawned >= currentMultiplier.finishOn){
                    int index = mulipliers.indexOf(currentMultiplier);
                    currentMultiplier = mulipliers.get(index < mulipliers.size()-1 ? index+1 : index);

                }
                if(bugsSpawned >= currentCocoonMultiplier.finishOn){
                    int indexC = cocoonMultipliers.indexOf(currentCocoonMultiplier);
                    currentCocoonMultiplier = cocoonMultipliers.get(indexC < cocoonMultipliers.size()-1 ? indexC+1 : indexC);
                }
                if(bugsSpawned >= currentDandelionMultiplier.finishOn){
                    int indexC = dandelionMultipliers.indexOf(currentDandelionMultiplier);
                    currentDandelionMultiplier = dandelionMultipliers.get(indexC < dandelionMultipliers.size()-1 ? indexC+1 : indexC);
                }
                spawn(deltaTime);
                updateAngeredBeesMode();
            }
        }
    }

    private void updateAngeredBeesMode() {
        if (isAngeredBeesMode) {
            angeredBeesModeTimer--;
            if (angeredBeesModeTimer <= 0) {
                isAngeredBeesMode = false;
                cameraShaker.initBlinking(40, 3);
                angeredBeesModeTimer = ANGERED_BEES_MODE_DURATION;

                checkAngeredBeesGoal();
            }
        }
    }

    private void checkAngeredBeesGoal() {
        if (GameStage.gameScript.fpc.level.getGoalByType(SURVIVE_N_ANGERED_MODES) != null) {
            GameStage.gameScript.fpc.level.getGoalByType(SURVIVE_N_ANGERED_MODES).update();
        }
    }

    public static class Multiplier{
        public int startOn = 0;
        public int finishOn = 20;

        public float spawnInterval = 1;
        public float breakFreqMin = 1;
        public float breakFreqMax = 1;
        public float breakLengthMin = 1;
        public float breakLengthMax = 1;
        public float simpleBugSpawnChance = 1;
        public float drunkBugSpawnChance = 1;
        public float chargerBugSpawnChance = 1;
        public float queenBeeSpawnChance = 1;
        public float beeSpawnChance = 1;

        public float simpleBugMoveDuration = 1;
        public float simpleBugAmplitude = 1;
        public float drunkBugMoveDuration = 1;
        public float drunkBugAmplitude = 1;
        public float beeMoveDuration = 1;
        public float beeAmplitude = 1;
        public float queenBeeMoveDuration = 1;
        public float queenBeeAmplitude = 1;
        public float chargerBugMove = 1;

    }
}
