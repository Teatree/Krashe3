package com.mygdx.game.entity.componets;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.mygdx.game.stages.GameScreenScript;
import com.mygdx.game.utils.SaveMngr;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by AnastasiiaRudyk on 12/14/2015.
 */
public class VanityComponent implements Component {

    public static final String DEFAULT = "_default";
    public static final String PATH_PREFIX = "orig\\spriter_animations\\flower_idle\\";
    public static final String TYPE_SUFFIX = ".png";

    public String icon;
    public String shopIcon;
    public String name;
    public int cost;
    public String description;

    //true when was bought (could be not applied)
    public boolean bought;

    //true when was presented in showcase
    public boolean advertised;

    //true when is applied now
    public boolean enabled ;

    public boolean floatingText;
    public int bugsSpawnAmount;
    public int attackSpeed;
    public int cocoonChance;
    public int dandelionChance;
    public int angeredBeesDuration;

    public Map<String, String> assetsToChange = new HashMap<>();

    public VanityComponent() {

    }

    public VanityComponent(SaveMngr.VanityStats vc) {
        this.icon = vc.icon;
        this.shopIcon = vc.shopIcon;
        this.name = vc.name;
        this.cost = vc.cost;
        this.description = vc.description;
        this.bought = vc.bought;
        this.advertised = vc.advertised;

        this.enabled = vc.enabled;
        this.floatingText = vc.floatingText;
        this.bugsSpawnAmount = vc.bugsSpawnAmount;
        this.attackSpeed = vc.attackSpeed;
        this.cocoonChance = vc.cocoonChance;
        this.dandelionChance = vc.dandelionChance;
        this.angeredBeesDuration = vc.angeredBeesDuration;
        this.assetsToChange = vc.assetsToChange;
    }

    public void apply (FlowerPublicComponent fc){

        if (bought) {
            this.enabled = true;
            for (Map.Entry entry : assetsToChange.entrySet()) {
                FileHandle newAsset = Gdx.files.internal(PATH_PREFIX + entry.getValue() + TYPE_SUFFIX);
                newAsset.copyTo(Gdx.files.local(PATH_PREFIX + entry.getKey() + TYPE_SUFFIX));
            }
            this.enabled = true;
        }
    }

    public void buy(FlowerPublicComponent fc){
        fc.totalScore -= this.cost;
        this.bought = true;
    }

    public void buyAndUse(FlowerPublicComponent fc){
        buy(fc);
        apply(fc);
    }

    public boolean isAffordable(){
        return GameScreenScript.fpc.totalScore >= this.cost;
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

