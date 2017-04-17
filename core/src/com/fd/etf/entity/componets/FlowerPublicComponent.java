package com.fd.etf.entity.componets;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool;
import com.fd.etf.stages.GameScreenScript;
import com.fd.etf.stages.GameStage;
import com.fd.etf.system.BugSystem;
import com.fd.etf.system.FlowerSystem;

import java.util.*;

public class FlowerPublicComponent implements Component, Pool.Poolable{

    public GameSettings settings = new GameSettings();

    public Rectangle boundsRect = new Rectangle();
    public boolean isCollision;
    public boolean isScary;

    public long bestScore;
    public long totalScore;
    public long curDay;
    public static float oldScore;
    public static float scoreDiff;
    public int score;
    public static FlowerComponent.State state = FlowerComponent.State.IDLE;

    public List<VanityComponent> vanities = new ArrayList<>();
    public List<PetComponent> pets = new ArrayList<>();
    public Map<Upgrade.UpgradeType, Upgrade> upgrades = new HashMap<>();
    public Level level = new Level();
    public PetComponent currentPet;

    public boolean petAndFlowerCollisionCheck(Rectangle rectangle) {
        return boundsRect.overlaps(rectangle) || petCollisionCheck(rectangle);
    }

    public boolean petCollisionCheck(Rectangle rectangle) {
        if (currentPet != null && currentPet.enabled) {
            PetComponent.eatThatBug(currentPet, rectangle);

            return boundsRect.overlaps(rectangle) ||
                    currentPet.boundsRect.overlaps(rectangle);
        }
        return false;
    }
    public boolean flowerCollisionCheck(Rectangle rectangle) {
        return boundsRect.overlaps(rectangle);
    }

    public boolean haveBugJuiceDouble() {
        return upgrades.get(Upgrade.UpgradeType.BJ_DOUBLE) != null &&
                upgrades.get(Upgrade.UpgradeType.BJ_DOUBLE).enabled;
    }

    public boolean havePhoenix() {
        return upgrades.get(Upgrade.UpgradeType.PHOENIX) != null &&
                upgrades.get(Upgrade.UpgradeType.PHOENIX).enabled;
    }

    public boolean canUsePhoenix() {
        Upgrade phoenix = upgrades.get(Upgrade.UpgradeType.PHOENIX);
        return phoenix != null && phoenix.enabled && phoenix.counter <= 0;
    }

    public void resetPhoenix() {
        Upgrade phoenix = upgrades.get(Upgrade.UpgradeType.PHOENIX);
        if (phoenix != null) {
            phoenix.counter = 0;
        }
    }

    public void addScore(int points) {
        score += haveBugJuiceDouble() ? 2 * points : points;
        oldScore = score - (haveBugJuiceDouble() ? 2 * points : points);
        scoreDiff = (float)(score - oldScore)/20;
       // totalScore += haveBugJuiceDouble() ? 2 * points : points;
        updateScoreGoal();
    }

    public void umbrellaMult(int pointsMult) {
        //totalScore -= score;
        score *= pointsMult;
        //totalScore += score;
        updateScoreGoal();
    }

    private void updateScoreGoal() {
        Goal scoreGoal = level.getGoalByType(Goal.GoalType.GET_N_POINTS);
        if (scoreGoal != null) {
            if (scoreGoal.periodType.equals(Goal.PeriodType.IN_ONE_LIFE)) {
                scoreGoal.counter = score;
            }
//            if (scoreGoal.periodType.equals(Goal.PeriodType.TOTAL)) {
//                scoreGoal.counter = (int) totalScore;
//            }
            scoreGoal.update();
        }
    }

    @Override
    public void reset() {

    }

    public boolean isSameDay(){
        Date d = new Date();

        if(d.getTime() - curDay > 86400000){
            curDay = d.getTime();
            return false;
        }else{
            return true;
        }
    }

//
//    public List<VanityComponent.VanityCollection> updateCollectionStates(){
//        List<VanityComponent.VanityCollection> res = new ArrayList();
//        for (VanityComponent vc : vanities){
//            if (vc.collection != null && vc.collection != ""){
//
//            }
//        }
//    }
}
