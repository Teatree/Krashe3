package com.mygdx.game.entity.componets;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;

import java.util.List;
import java.util.Random;

import static com.mygdx.game.utils.GlobalConstants.*;

public class CocoonComponent implements Component {

    public static float SPAWN_INTERVAL_BASE = 10;
    public static final int COCOON_HIT_AMOUNT = 3;

    public static List<CocoonMultiplier> cocoonMultipliers;
    public static CocoonMultiplier currentCocoonMultiplier;

    public int hitCounter = 0;
    public State state;
    public Rectangle boundsRect;
    public boolean canHit;

    public CocoonComponent() {
        this.boundsRect = new Rectangle();
        state = State.SPAWNING;
    }

    public enum State {
        IDLE,
        SPAWNING,
        HIT,
        DEAD;
    }

    public static class CocoonMultiplier {
        public float minSpawnCoefficient;
        public float maxSpawnCoefficient;
        public int startOn;
        public int finishOn;
    }
}
