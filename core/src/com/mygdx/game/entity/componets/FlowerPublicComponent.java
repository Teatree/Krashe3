package com.mygdx.game.entity.componets;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class FlowerPublicComponent implements Component {

    public GameSettings settings = new GameSettings();

    public Rectangle boundsRect = new Rectangle();
    public boolean isCollision;

    public long bestScore;
    public long totalScore;
    public long score;

    public List<VanityComponent> vanities = new ArrayList<>();
    public List<DailyGoal> goals = new ArrayList<>();
    public List<PetComponent> pets = new ArrayList<>();
    public List<Upgrade> upgrades = new ArrayList<>();

    public PetComponent currentPet;

    public boolean checkGoals(int n) {
        boolean allAchieved = true;
        for (DailyGoal goal : goals) {
            allAchieved = allAchieved && goal.checkIfAchieved(n);
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
        for (Upgrade u : upgrades) {
            if (u.upgradeType.equals(Upgrade.UpgradeType.DOUBLE_JUICE)) {
                return true;
            }
        }
        return false;
    }

    public boolean canUsePhoenix() {
        for (Upgrade u : upgrades) {
            if (u.upgradeType.equals(Upgrade.UpgradeType.PHOENIX) && u.counter <= 0) {
                return true;
            }
        }
        return false;
    }

    public boolean havePhoenix() {
        for (Upgrade u : upgrades) {
            if (u.upgradeType.equals(Upgrade.UpgradeType.PHOENIX)) {
                return true;
            }
        }
        return false;
    }

    public void resetPhoenix() {
        for (Upgrade u : upgrades) {
            if (u.upgradeType.equals(Upgrade.UpgradeType.PHOENIX)) {
                u.counter = 0;
            }
        }
    }
}
          