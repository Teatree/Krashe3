package com.mygdx.etf.entity.componets;

import com.badlogic.ashley.core.Component;

import java.util.List;

/**
 *
 * Created by Teatree on 9/3/2015.
 */
public class DandelionComponent implements Component {

    public static float SPAWN_INTERVAL_BASE = 10;

    public static List<DandelionMultiplier> dandelionMultipliers;
    public static DandelionMultiplier currentDandelionMultiplier;
    public State state;

    public enum State {
        IDLE,
        GROWING,
        DYING,
        DEAD;
    }

    public static class DandelionMultiplier {
        public float minSpawnCoefficient;
        public float maxSpawnCoefficient;
        public int startOn;
        public int finishOn;
    }
}
