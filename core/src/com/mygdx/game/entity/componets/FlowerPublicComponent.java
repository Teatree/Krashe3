package com.mygdx.game.entity.componets;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AnastasiiaRudyk on 27/10/2015.
 */
public class FlowerPublicComponent implements Component {

    public boolean noAds = false;
    public boolean noSound;
    public boolean noMusic;

    public Rectangle boundsRect = new Rectangle();
    public boolean isCollision;

    public int bestScore;
    public int totalScore;
    public int score;

    public List<VanityComponent> vanities = new ArrayList<>();
    public List<DailyGoal> goals = new ArrayList<>();
    public List<PetComponent> pets = new ArrayList<>();

    public PetComponent currentPet;

    public boolean checkGoals(int n){
        boolean allAchieved = true;
        for (DailyGoal goal : goals){
            allAchieved = allAchieved && goal.checkIfAchieved(n);
        }
        return allAchieved;
    }

    public boolean petAndFlowerCollisionCheck(Rectangle rectangle){
        if (currentPet != null) {
            return boundsRect.overlaps(rectangle) ||
                    currentPet.boundsRect.overlaps(rectangle);
        } else {
            return boundsRect.overlaps(rectangle);
        }
    }
}
          