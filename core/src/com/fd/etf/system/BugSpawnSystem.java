package com.fd.etf.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.fd.etf.entity.componets.BugComponent;
import com.fd.etf.entity.componets.DebugComponent;
import com.fd.etf.stages.GameStage;
import com.fd.etf.utils.BugPool;
import com.uwsoft.editor.renderer.components.TransformComponent;

import java.util.List;
import java.util.Random;

import static com.fd.etf.entity.componets.BugComponent.*;
import static com.fd.etf.stages.GameScreenScript.*;
import static com.fd.etf.system.CocoonSystem.cocoonMultipliers;
import static com.fd.etf.system.CocoonSystem.currentCocoonMultiplier;

public class BugSpawnSystem extends EntitySystem {

    // spawn probability
    // combined has to be equal to 100
    public static final int DRUNK_SPAWN_PROB = 10;
    public static final int SIMPLE_SPAWN_PROB = 1;
    public static final int CHARGER_SPAWN_PROB = 27;
    public static final int QUEENBEE_SPAWN_PROB = 29;
    public static final int BEE_SPAWN_PROB = 30;
    public static final int ANGERED_BEE_PATTERN_1_y_1 = 32;
    public static final int ANGERED_BEE_PATTERN_1_y_2 = 500;
    public static final int ANGERED_BEE_PATTERN_2_y_1 = 300;
    public static final int ANGERED_BEE_PATTERN_2_y_1Stage = 200;
    public static final int ANGERED_BEE_PATTERN_2_y_2 = 100;
    public static final int ANGERED_BEE_PATTERN_2_y_2Stage = 500;

    public static int curDrunkProb = DRUNK_SPAWN_PROB;
    public static int curSimpleProb = SIMPLE_SPAWN_PROB;
    public static int curChargerProb = CHARGER_SPAWN_PROB;
    public static int curQueenBeeProb = QUEENBEE_SPAWN_PROB;
    public static int curBeeProb = BEE_SPAWN_PROB;
    public static int angeredBeePattern1Y1 = ANGERED_BEE_PATTERN_1_y_1;
    public static int angeredBeePattern1Y2 = ANGERED_BEE_PATTERN_1_y_2;
    public static int angeredBeePattern2Y1 = ANGERED_BEE_PATTERN_2_y_1;
    public static int angeredBeePattern2Y1stage = ANGERED_BEE_PATTERN_2_y_1Stage;
    public static int angeredBeePattern2Y2 = ANGERED_BEE_PATTERN_2_y_2;
    public static int angeredBeePattern2Y2stage = ANGERED_BEE_PATTERN_2_y_2Stage;

    public static int ANGERED_BEES_MODE_DURATION = 800;
    public static boolean queenBeeOnStage = false;
    public static int angerBeePattern;
    public static int angerBeePattern1case;
    public static int angerBeePattern2case;
    private final GameStage gameStage;

    private int SPAWN_MAX_X = -200;
    private int SPAWN_MIN_X = -300;
    private int SPAWN_MIN_Y = 100;
    private int SPAWN_MAX_Y = 360;

    private float spawner = 0;
    public static float break_counter = 0;
    private float SPAWN_INTERVAL_BASE = 2.5f;
    public static float BREAK_FREQ_BASE_MIN = 20;
    public static float BREAK_FREQ_BASE_MAX = 28;
    public static float BREAK_LENGTH_BASE_MIN = 2;
    public static float BREAK_LENGTH_BASE_MAX = 5;

    private float curSpawnInterval = SPAWN_INTERVAL_BASE;
    public static float curBreakFreqMin = BREAK_FREQ_BASE_MIN;
    public static float curBreakFreqMax = BREAK_FREQ_BASE_MAX;
    public static float curBreakLengthMin = BREAK_LENGTH_BASE_MIN;
    public static float curBreakLengthMax = BREAK_LENGTH_BASE_MAX;

    public static List<Multiplier> mulipliers;
    public static Multiplier currentMultiplier;
    public static boolean isFirst;
    public int bugsSpawned;

    public static Random rand = new Random();
    private float angryBeeLinePosY = 150;
    private float angryBeeLinePosX = SPAWN_MAX_X;

    public BugSpawnSystem(GameStage gameStage) {
        this.gameStage = gameStage;
        init();
    }

    public static boolean isBlewUp() {
        return isAngeredBeesMode & angeredBeesModeTimer == ANGERED_BEES_MODE_DURATION - 10;
    }

    private void init() {
        SPAWN_MIN_X = -300;
        SPAWN_MIN_Y = 100;
        SPAWN_MAX_X = -200;
        SPAWN_MAX_Y = 650;

        BugSpawnSystem.break_counter = BugSpawnSystem.rand.nextInt((int) (BugSpawnSystem.curBreakFreqMax * 100) - (int) (BugSpawnSystem.curBreakFreqMin * 100)) + (BugSpawnSystem.curBreakFreqMin * 100);
        BugSpawnSystem.break_counter /= 100;
        System.out.println("init() + break_counter: " + BugSpawnSystem.break_counter);
        currentMultiplier = mulipliers.get(0);
    }

    public static void resetBreakCounter() {
        curBreakFreqMin = 0.12f;
        curBreakFreqMax = 0.16f;
//                curBreakLengthMin = BREAK_LENGTH_BASE_MIN/3;
//                curBreakLengthMax = BREAK_LENGTH_BASE_MAX/3;
    }

    private TransformComponent getPos(BugComponent bc) {
        TransformComponent transformComponent = gameStage.sceneLoader.engine.createComponent(TransformComponent.class);
        transformComponent.x = rand.nextInt(SPAWN_MAX_X - SPAWN_MIN_X) + SPAWN_MIN_X;
        transformComponent.y = rand.nextInt(SPAWN_MAX_Y - SPAWN_MIN_Y) + SPAWN_MIN_Y;
        bc.endX = 1450;
        bc.endY = transformComponent.y;

        return transformComponent;
    }

    public void spawn(float delta) {
        if(!isAngeredBeesMode) {
            break_counter -= delta;
        }
        if (spawner <= 0 && !BugSystem.blowUpAllBugs) {
            if (isAngeredBeesMode) {
                if(angerBeePattern == 0) {
                    curSpawnInterval = 0.3f;
                }else if (angerBeePattern == 1){
                    curSpawnInterval = 0.6f;
                }else{
                    curSpawnInterval = 0.6f;
                }
                resetBreakCounter();
//                createBug(BEE, currentMultiplier);
                createAngryBee(currentMultiplier);
            } else {
                int probabilityValue = rand.nextInt(100);
                if (probabilityValue <= curDrunkProb) {
                    createBug(BEE, currentMultiplier);
                } else if (probabilityValue > curDrunkProb && probabilityValue < curDrunkProb + curSimpleProb) {
                    createBug(BEE, currentMultiplier);
                } else if (probabilityValue >= curDrunkProb + curSimpleProb + 1 && probabilityValue < curDrunkProb + curSimpleProb + curChargerProb) {
                    createBug(BEE, currentMultiplier);
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

            System.out.println(" spawn() everytime + break_counter: " + break_counter);
            if (break_counter > 0.1f) {
                if(isAngeredBeesMode) {
                    break_counter -= delta;
                }
                spawner = curSpawnInterval;
                isFirst = true;
            } else {
                spawner = rand.nextInt((int) curBreakLengthMax - (int) curBreakLengthMin) + curBreakLengthMin;
                BugSpawnSystem.break_counter = BugSpawnSystem.rand.nextInt((int) (BugSpawnSystem.curBreakFreqMax * 100) - (int) (BugSpawnSystem.curBreakFreqMin * 100)) + (BugSpawnSystem.curBreakFreqMin * 100);
                BugSpawnSystem.break_counter /= 100;
                angerBeePattern1case = rand.nextInt(2);
                angerBeePattern2case = rand.nextInt(2);
                angeredBeePattern1Y1 = ANGERED_BEE_PATTERN_1_y_1;
                angeredBeePattern1Y2 = ANGERED_BEE_PATTERN_1_y_2;
                angeredBeePattern2Y1 = ANGERED_BEE_PATTERN_2_y_1;
                angeredBeePattern2Y1stage = ANGERED_BEE_PATTERN_2_y_1Stage;
                angeredBeePattern2Y2 = ANGERED_BEE_PATTERN_2_y_2;
                angeredBeePattern2Y2stage = ANGERED_BEE_PATTERN_2_y_2Stage;
                System.out.println("spawn() + break_counter: " + BugSpawnSystem.break_counter);

                //new angry bee row
                angryBeeLinePosY = rand.nextInt(SPAWN_MAX_Y - SPAWN_MIN_Y) + SPAWN_MIN_Y;
            }

        } else {
            spawner -= delta;
        }
//        System.out.println("spanwer: " + spawner);
//        System.out.println("break_counter: " + break_counter);
    }

    private void createBug(String tempType, Multiplier currentMultiplier) {
        Entity bugEntity = BugPool.getInstance(gameStage).get(tempType);
        BugComponent bc = new BugComponent(gameStage, tempType, currentMultiplier);
        if (bugEntity == null) {
            System.out.println("temp bug type " + tempType);
        }
        bugEntity.add(bc);

        TransformComponent tc = getPos(bc);
        bc.startYPosition = tc.y;
        bugEntity.getComponent(TransformComponent.class).x = tc.x;
        bugEntity.getComponent(TransformComponent.class).y = tc.y;

        bugEntity.add(new DebugComponent(bugEntity.getComponent(BugComponent.class).boundsRect));
    }

    private void createAngryBee(Multiplier currentMultiplier) {
        Entity bugEntity = BugPool.getInstance(gameStage).get(BEE);
        BugComponent bc = new BugComponent(gameStage, BEE, currentMultiplier);
        bc.isAngeredBee = true;
        bc.duration = bc.duration/1.2f;
        bc.interpolation = null;
        bugEntity.add(bc);

        TransformComponent tc = bugEntity.getComponent(TransformComponent.class);
        tc.x = angryBeeLinePosX;

        if(angerBeePattern == 0) {
            if (isFirst) {
//            System.out.println("YES, I AM WORKING!");
                tc.y = angryBeeLinePosY - 60;
                isFirst = false;
            } else {
                tc.y = angryBeeLinePosY;
            }

            bc.endX = 1450;
            bc.endY = tc.y;

            bc.startYPosition = tc.y;
        }else if(angerBeePattern == 1){
            if(angerBeePattern1case == 1) {
                tc.y = angeredBeePattern1Y1 += 100;
                bc.endX = 1450;
                bc.endY = tc.y;
            }else {
                tc.y = angeredBeePattern1Y2 -= 100;
                bc.endX = 1450;
                bc.endY = tc.y;
            }
        }else {
            if(angerBeePattern2case == 1) {
                if(angeredBeePattern2Y1 < 490) {
                    tc.y = angeredBeePattern2Y1 += 100;
                }else{
                    tc.y = angeredBeePattern2Y1stage -= 100;
                }

                bc.endX = 1450;
                bc.endY = tc.y;
            }else {
                if(angeredBeePattern2Y2 < 290) {
                    tc.y = angeredBeePattern2Y2 += 100;
                }else{
                    tc.y = angeredBeePattern2Y2stage -= 100;
                }
                bc.endX = 1450;
                bc.endY = tc.y;
            }
        }
    }

    @Override
    public void update(float deltaTime) {
        curSpawnInterval = SPAWN_INTERVAL_BASE * currentMultiplier.spawnInterval * gameStage.gameScript.fpc.level.spawnInterval;
        curBreakFreqMin = BREAK_FREQ_BASE_MIN * currentMultiplier.breakFreqMin * gameStage.gameScript.fpc.level.breakFreqMin;
        curBreakFreqMax = BREAK_FREQ_BASE_MAX * currentMultiplier.breakFreqMax * gameStage.gameScript.fpc.level.breakFreqMax;
        curBreakLengthMin = BREAK_LENGTH_BASE_MIN * currentMultiplier.breakLengthMin * gameStage.gameScript.fpc.level.breakLengthMin;
        curBreakLengthMax = BREAK_LENGTH_BASE_MAX * currentMultiplier.breakLengthMax * gameStage.gameScript.fpc.level.breakLengthMax;

        curDrunkProb = (int) (DRUNK_SPAWN_PROB * currentMultiplier.drunkBugSpawnChance * gameStage.gameScript.fpc.level.drunkBugSpawnChance);
        curSimpleProb = (int) (SIMPLE_SPAWN_PROB * currentMultiplier.simpleBugSpawnChance * gameStage.gameScript.fpc.level.simpleBugSpawnChance);
        curChargerProb = (int) (CHARGER_SPAWN_PROB * currentMultiplier.chargerBugSpawnChance * gameStage.gameScript.fpc.level.chargerBugSpawnChance);
        curQueenBeeProb = (int) (QUEENBEE_SPAWN_PROB * currentMultiplier.queenBeeSpawnChance * gameStage.gameScript.fpc.level.queenBeeSpawnChance);
        curBeeProb = (int) (BEE_SPAWN_PROB * currentMultiplier.beeSpawnChance * gameStage.gameScript.fpc.level.beeSpawnChance);

//        System.out.println("curSpawnInterval: " + curSpawnInterval);

        if (!isPause.get() && !isGameOver.get()) {
            if (isStarted) {
                super.update(deltaTime);
                if (bugsSpawned >= currentMultiplier.finishOn) {
                    int index = mulipliers.indexOf(currentMultiplier);
                    currentMultiplier = mulipliers.get(index < mulipliers.size() - 1 ? index + 1 : index);

           }
                if (bugsSpawned >= currentCocoonMultiplier.finishOn) {
                    int indexC = cocoonMultipliers.indexOf(currentCocoonMultiplier);
                    currentCocoonMultiplier = cocoonMultipliers.get(indexC < cocoonMultipliers.size() - 1 ? indexC + 1 : indexC);
                }
//                if(bugsSpawned >= currentDandelionMultiplier.finishOn){
//                    int indexC = dandelionMultipliers.indexOf(currentDandelionMultiplier);
//                    currentDandelionMultiplier = dandelionMultipliers.get(indexC < dandelionMultipliers.size()-1 ? indexC+1 : indexC);
//                }
                spawn(deltaTime);
//                updateAngeredBeesMode();
            }
        }
    }

    public static class Multiplier {
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
