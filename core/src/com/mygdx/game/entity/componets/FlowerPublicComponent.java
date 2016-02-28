package com.mygdx.game.entity.componets;

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

    public long bestScore;
    public long totalScore;
    public long score;
    public int level;

    public List<VanityComponent> vanities = new ArrayList<>();
    public HashMap<DailyGoal.GoalType, DailyGoal> goals = new HashMap<>();
    public List<PetComponent> pets = new ArrayList<>();
    public Map<Upgrade.UpgradeType, Upgrade> upgrades = new HashMap<>();

    public PetComponent currentPet;

    public boolean checkAllGoals() {
        boolean allAchieved = true;
        for (DailyGoal goal : goals.values()) {
            allAchieved = allAchieved && goal.achieved;
        }
        return allAchieved;
    }

    public boolean petAndFlowerCollisionCheck(Rectangle rectangle) {
        if (currentPet != null) {
            PetComponent.eatThatBug(currentPet, rectangle);

            return boundsRect.overlaps(rectangle) ||
                    currentPet.boundsRect.overlaps(rectangle);
        } else {
            return boundsRect.overlaps(rectangle);
        }
    }

    public boolean flowerCollisionCheck(Rectangle rectangle) {
        return boundsRect.overlaps(rectangle);
    }

    public boolean haveBugJuiceDouble() {
        return upgrades.get(Upgrade.UpgradeType.DOUBLE_JUICE) != null;
    }

    public boolean canUsePhoenix() {
        Upgrade phoenix = upgrades.get(Upgrade.UpgradeType.PHOENIX);
        return phoenix != null && phoenix.counter <= 0;
    }

    public boolean havePhoenix() {
        return upgrades.get(Upgrade.UpgradeType.PHOENIX) != null;
    }

    public void resetPhoenix() {
        Upgrade phoenix = upgrades.get(Upgrade.UpgradeType.PHOENIX);
        if (phoenix != null) {
            phoenix.counter = 0;
        }
    }
}
          