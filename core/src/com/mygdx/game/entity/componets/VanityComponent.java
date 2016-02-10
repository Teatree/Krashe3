package com.mygdx.game.entity.componets;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.mygdx.game.stages.GameScreenScript;
import com.mygdx.game.stages.GameStage;
import com.mygdx.game.utils.SaveMngr;
import com.uwsoft.editor.renderer.components.ZIndexComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;

import java.util.HashMap;
import java.util.Map;

public class VanityComponent extends ShopItem implements Component {

    public static final String DEFAULT = "_default";
    public static final String PATH_PREFIX = "orig\\spriter_animations\\flower_idle\\";
    public static final String TYPE_SUFFIX = ".png";

    //true when was presented in showcase
    public boolean advertised;

    public boolean floatingText;
    public int bugsSpawnAmount;
    public int attackSpeed;
    public int cocoonChance;
    public int dandelionChance;
    public int angeredBeesDuration;

    public Map<String, String> assetsToChange = new HashMap<>();

    public PetComponent pet;

    public VanityComponent() {
        type = CurrencyType.SOFT;
    }

    public VanityComponent(SaveMngr.VanityStats vc) {
        type = CurrencyType.SOFT;
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
        this.pet = vc.pet != null ? new PetComponent(vc.pet) : pet;
    }

    public void apply (FlowerPublicComponent fc){

        if (bought) {
            this.enabled = true;
            for (Map.Entry entry : assetsToChange.entrySet()) {
                FileHandle newAsset = Gdx.files.internal(PATH_PREFIX + entry.getValue() + TYPE_SUFFIX);
                newAsset.copyTo(Gdx.files.local(PATH_PREFIX + entry.getKey() + TYPE_SUFFIX));
            }
            if (this.pet != null){
                GameScreenScript.fpc.currentPet = this.pet;
            }
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

    public boolean canBuy(){
        return GameScreenScript.fpc.totalScore >= this.cost;
    }

    public void disable(FlowerPublicComponent fc){
        this.enabled = false;

        for (Map.Entry entry : assetsToChange.entrySet()) {
            FileHandle fromDefault = Gdx.files.internal(PATH_PREFIX + entry.getKey() + DEFAULT + TYPE_SUFFIX);
            fromDefault.copyTo(Gdx.files.local(PATH_PREFIX + entry.getKey() + TYPE_SUFFIX));
        }
    }
}

