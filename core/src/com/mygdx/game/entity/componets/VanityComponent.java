package com.mygdx.game.entity.componets;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by AnastasiiaRudyk on 12/14/2015.
 */
public class VanityComponent implements Component {

    public static final String DEFAULT = "_default";
    public static final String PATH_PREFIX = "orig\\spriter_animations\\flower_idle\\";
    public static final String TYPE_SUFFIX = ".png";

    public String name;
    public int cost;
    public String description;
    public boolean bought = true;
    public boolean available = true;
    public boolean enabled;

    public Map<String, String> assetsToChange = new HashMap<>();

    public VanityComponent() {
        assetsToChange.put("head_top", "head_top_deer");
    }

    public void apply (FlowerPublicComponent fc){

        if (bought) {
            fc.totalScore -= cost;
            this.enabled = true;

            for (Map.Entry entry : assetsToChange.entrySet()) {
                FileHandle fromDefault = Gdx.files.internal(PATH_PREFIX + entry.getKey() + TYPE_SUFFIX);
                fromDefault.copyTo(Gdx.files.local(PATH_PREFIX + entry.getKey() + DEFAULT + TYPE_SUFFIX));
                FileHandle newAsset = Gdx.files.internal(PATH_PREFIX + entry.getValue() + TYPE_SUFFIX);
                newAsset.copyTo(Gdx.files.local(PATH_PREFIX + entry.getKey() + TYPE_SUFFIX));

            }
            fc.vanities.add(this);
        }
    }

    public void disable(FlowerPublicComponent fc){
        fc.vanities.remove(this);
        this.enabled = false;

        for (Map.Entry entry : assetsToChange.entrySet()) {
            FileHandle fromDefault = Gdx.files.internal(PATH_PREFIX + entry.getKey() + DEFAULT + TYPE_SUFFIX);
            fromDefault.copyTo(Gdx.files.local(PATH_PREFIX + entry.getKey() + TYPE_SUFFIX));
        }
    }
}

