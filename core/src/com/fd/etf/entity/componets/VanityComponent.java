package com.fd.etf.entity.componets;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Pool;
import com.fd.etf.stages.GameStage;
import com.fd.etf.utils.SaveMngr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VanityComponent extends ShopItem implements Component, Pool.Poolable{

    public static final String DEFAULT = "_default";
    public static final String PATH_PREFIX_VANITY = "vanity\\";
    public static final String PATH_PREFIX_LOCAL_ANI = "orig\\spriter_animations\\flower_idle\\";
    public static final String PATH_PREFIX_LOCAL_LEAVES_ANI = "orig\\spriter_animations\\flower_leafs_idle\\";
    public static final String TYPE_SUFFIX = ".png";

    public static final String CLASS = "class";

    //true when was presented in showcase
    public boolean advertised;
    public boolean leaves; //if true - than change flower_leafs_idle

    public boolean floatingText;
    public int bugsSpawnAmount;
    public int attackSpeed;
    public int cocoonChance;
    public int dandelionChance;
    public int angeredBeesDuration;

    public Map<String, String> assetsToChange = new HashMap<String, String>();

    public PetComponent pet;

    public String collection;

    public static Map<String, VanityCollection> vanityCollections;
    public static Map<String, List<VanityComponent>> vanityComponentsByChangedAssets;

    public VanityComponent() {
        currencyType = SOFT;
    }

    public VanityComponent(SaveMngr.VanityJson vc) {
        currencyType = SOFT;
        this.shopIcon = vc.shopIcon;
        this.name = vc.name;
        this.cost = vc.cost;
        this.description = vc.description;
        this.bought = vc.bought;
        this.advertised = vc.advertised;
        this.leaves = vc.leaves;

        this.enabled = vc.enabled;
        this.floatingText = vc.floatingText;
        this.bugsSpawnAmount = vc.bugsSpawnAmount;
        this.attackSpeed = vc.attackSpeed;
        this.cocoonChance = vc.cocoonChance;
        this.dandelionChance = vc.dandelionChance;
        this.angeredBeesDuration = vc.angeredBeesDuration;
        this.assetsToChange = vc.assetsToChange;
        this.pet = vc.pet != null ? new PetComponent(vc.pet) : pet;
        this.collection = vc.collection;
    }

    public void apply() {
        if (bought) {
            disableSimilarVanities();

            this.enabled = true;
            overrideAnimationFiles();
            if (this.pet != null) {
                GameStage.gameScript.fpc.currentPet = this.pet;
            }

            GameStage.changedFlower = true;
        }
    }

    private void disableSimilarVanities() {
        for (String fileName : assetsToChange.keySet()){
            for (VanityComponent vc : vanityComponentsByChangedAssets.get(fileName)){
                if (vc.enabled) {
                    vc.enabled = false;
                }
            }
        }
    }

    private void overrideAnimationFiles() {
        for (Map.Entry entry : assetsToChange.entrySet()) {
            if (!entry.getKey().equals(CLASS)) {
                String path = leaves ? PATH_PREFIX_LOCAL_LEAVES_ANI : PATH_PREFIX_LOCAL_ANI;
                Gdx.files.local(path + entry.getKey() + TYPE_SUFFIX).writeBytes(Gdx.files.internal(PATH_PREFIX_VANITY + entry.getValue() + TYPE_SUFFIX).readBytes(), false);
            }
        }
    }

    public void buy() {
        GameStage.gameScript.fpc.totalScore -= this.cost;
        this.bought = true;
    }

    public void buyAndUse() {
        buy();
        apply();
    }

    @Override
    public void buyHard() {
        //nothing I'm soft
    }

    @Override
    public void buyHardDiscount() {
        //nothing I'm soft
    }

    public boolean canBuy() {
        return GameStage.gameScript.fpc.totalScore >= this.cost;
    }

    public void disable() {
        this.enabled = false;

        backToDefaultAnimation();
        GameStage.changedFlower = true;
        if (this.pet != null) {
            GameStage.gameScript.fpc.currentPet = this.pet;
        }
    }

    public void backToDefaultAnimation() {
        for (Map.Entry entry : assetsToChange.entrySet()) {
            resetOneFileTodefault(entry);
        }
    }

    public void resetOneFileTodefault(Map.Entry entry) {
        if (!entry.getKey().equals(CLASS)) {
            String path = leaves ? PATH_PREFIX_LOCAL_LEAVES_ANI : PATH_PREFIX_LOCAL_ANI;
            Gdx.files.local(path + entry.getKey() + TYPE_SUFFIX)
                    .writeBytes(Gdx.files.internal(PATH_PREFIX_VANITY + entry.getKey()
                            + DEFAULT + TYPE_SUFFIX).readBytes(), false);
        }
    }

    public static void disableAllVanitiesAssets() {
        List<String> assetsToChangeFlower = new ArrayList<>();
        assetsToChangeFlower.add("head_top");

        List<String> assetsToChangeLeaves = new ArrayList<>();
        assetsToChangeLeaves.add("pot");
        assetsToChangeLeaves.add("leaf_left");
        assetsToChangeLeaves.add("leaf_right");

        for (String flowerPart : assetsToChangeFlower) {
            if (!flowerPart.equals(CLASS)) {
                Gdx.files.local(PATH_PREFIX_LOCAL_ANI + flowerPart + TYPE_SUFFIX)
                        .writeBytes(Gdx.files.internal(PATH_PREFIX_VANITY + flowerPart
                                + DEFAULT + TYPE_SUFFIX).readBytes(), false);
            }
        }

        for (String flowerPart : assetsToChangeLeaves) {
            if (!flowerPart.equals(CLASS)) {
                Gdx.files.local(PATH_PREFIX_LOCAL_LEAVES_ANI + flowerPart + TYPE_SUFFIX)
                        .writeBytes(Gdx.files.internal(PATH_PREFIX_VANITY + flowerPart
                                + DEFAULT + TYPE_SUFFIX).readBytes(), false);
            }

        }
        GameStage.changedFlower = true;
    }

    @Override
    public void reset() {

    }
}

