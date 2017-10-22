package com.fd.etf.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

import java.util.HashMap;
import java.util.Random;

public class SoundMgr {

    public static final String EAT_SOUND = "eat";
//    public static final String EAT_SOUND_1 = "eat_1";
//    public static final String EAT_SOUND_2 = "eat_2";
    public static final String BUTTON_TAP = "ui_button_tap";
    public static final String SPECIAL_OFFER = "special_offer";
    public static final String PAPER_FLIP_SHOP = "paper_flip_shop";
    public static SoundMgr soundMgr;
    public static boolean soundOn = false;
//    private Random rand;
    private int r = 0;

    public HashMap<String, Sound> fx = new HashMap<String, Sound>();

    private SoundMgr() {
//        rand = new Random();
        loadSound(EAT_SOUND);
//        loadSound(EAT_SOUND_1);
//        loadSound(EAT_SOUND_2);
        loadSound(BUTTON_TAP);
        loadSound(SPECIAL_OFFER);
        loadSound(PAPER_FLIP_SHOP);
     //   loadSound("tuturu");
    }

    public static SoundMgr getSoundMgr() {
        if (soundMgr == null) {
            soundMgr = new SoundMgr();
        }

        return soundMgr;
    }

    private void loadSound(String name) {
        fx.put(name, Gdx.audio.newSound(Gdx.files.internal("sound/" + name + ".mp3")));
    }

    public void play(String name) {
        if (soundOn) {
            if(name == EAT_SOUND){
                play(name, 0.2f);
            }else {
                fx.get(name).play();
            }
        }
    }

    public void play(String name, float volume) {
        long id = fx.get(name).play();
        fx.get(name).setVolume(id, volume);
    }
}
