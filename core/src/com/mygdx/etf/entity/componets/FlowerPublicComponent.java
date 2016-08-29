package com.mygdx.etf.entity.componets;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlowerPublicComponent implements Component {

    public GameSettings settings = new GameSettings();

    public Rectangle boundsRect = new Rectangle();
    public boolean isCollision;
    public boolean isScary;

    public long bestScore;
    public long totalScore;
    public long score;

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

    public boolean canUsePhoenix() {
        Upgrade phoenix = upgrades.get(Upgrade.UpgradeType.PHOENIX);
        return phoenix != null && phoenix.counter <= 0;
    }

    public void resetPhoenix() {
        Upgrade phoenix = upgrades.get(Upgrade.UpgradeType.PHOENIX);
        if (phoenix != null) {
            phoenix.counter = 0;
        }
    }

    public void addScore(int points) {
        score += haveBugJuiceDouble() ? 2 * points : points;
        totalScore += haveBugJuiceDouble() ? 2 * points : points;
        updateScoreGoal();
    }


    public void umbrellaMult(int pointsMult) {
        totalScore -= score;
        score *= pointsMult;
        totalScore += score;
        updateScoreGoal();
    }

    private void updateScoreGoal() {
        Goal scoreGoal = level.getGoalByType(Goal.GoalType.GET_N_POINTS);
        if (scoreGoal != null) {
            if (scoreGoal.periodType.equals(Goal.PeriodType.IN_ONE_LIFE)) {
                scoreGoal.counter = (int) score;
            }
            if (scoreGoal.periodType.equals(Goal.PeriodType.TOTAL)) {
                scoreGoal.counter = (int) totalScore;
            }
            scoreGoal.update();
        }
    }
}
