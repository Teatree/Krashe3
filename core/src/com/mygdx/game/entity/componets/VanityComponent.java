package com.mygdx.game.entity.componets;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.mygdx.game.stages.GameScreenScript;
import com.mygdx.game.stages.GameStage;
import com.mygdx.game.utils.SaveMngr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VanityComponent extends ShopItem implements Component {

    public static final String DEFAULT = "_default";
    public static final String PATH_PREFIX_VANITY = "vanity\\";
    public static final String PATH_PREFIX_LOCAL_ANI = "orig\\spriter_animations\\flower_idle\\";
    public static final String TYPE_SUFFIX = ".png";

    public static final String HEAD_TOP = "head_top";
    public static final String HEAD_BOTTOM = "head_bottom";
    public static final String HEAD_MID = "head_mid";
    public static final String leaf_left = "leaf_left";
    public static final String leaf_right = "leaf_right";
    public static final String peduncle_bottom = "peducle_bottom";
    public static final String peducle_middle = "peducle_middle";
    public static final String peducle_middle_aboveLeaf = "peducle_middle_aboveLeaf";
    public static final String peducle_top = "peducle_top";
    public static final String peducle_top_under = "peducle_top_under";
    public static final String item_back_shine = "item_back_shine";
    public static final String flower_peducle = "flower_peducle";
    public static final String flower_idle = "flower_idle.scml";


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
        currencyType = CurrencyType.SOFT;
    }

    public VanityComponent(SaveMngr.VanityJson vc) {
        currencyType = CurrencyType.SOFT;
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
//        moveToLocal();

        if (bought) {
            this.enabled = true;
            for (Map.Entry entry : assetsToChange.entrySet()) {
//                FileHandle newAsset = Gdx.files.internal(PATH_PREFIX + entry.getValue() + TYPE_SUFFIX);
//                newAsset.copyTo(Gdx.files.local(entry.getKey() + TYPE_SUFFIX));
                Gdx.files.local(PATH_PREFIX_LOCAL_ANI + entry.getKey() + TYPE_SUFFIX).writeBytes(Gdx.files.internal(PATH_PREFIX_VANITY + entry.getValue() + TYPE_SUFFIX).readBytes(), false);
            }

            if (this.pet != null){
                GameScreenScript.fpc.currentPet = this.pet;
            }
            GameStage.updateFlowerAni();
            GameStage.changedFlower = true;
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
            Gdx.files.local(PATH_PREFIX_LOCAL_ANI + entry.getKey() + TYPE_SUFFIX).writeBytes(Gdx.files.internal(PATH_PREFIX_VANITY + entry.getKey() + DEFAULT + TYPE_SUFFIX).readBytes(), false);

        }
        GameStage.updateFlowerAni();
        GameStage.changedFlower = true;
    }
}

