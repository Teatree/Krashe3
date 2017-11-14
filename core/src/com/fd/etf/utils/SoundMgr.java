package com.fd.etf.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

import java.util.HashMap;
import java.util.Random;

public class SoundMgr {

    public static final String EAT_SOUND = "eat";
    public static final String EAT_SOUND_1 = "eat_1";
    public static final String EAT_SOUND_2 = "eat_2";
    public static final String BUTTON_TAP = "ui_button_tap";
    public static final String SPECIAL_OFFER = "special_offer";
    public static final String PAPER_FLIP_SHOP = "paper_flip_shop";
    public static final String WIND_POP_UP_OPEN = "wind_01_pop_ups";
    public static final String WIND_POP_UP_CLOSE = "wind_02_pop_ups";
    public static final String BUTTON_TAP_SHOP_BUY = "ui_button_tap_shop_buy";
    public static final String SPARKLE = "sparkle";
    public static final String PROGRESS_BAR_COUNT = "result_screen_progress_bar_count";
    public static final String SCORE_COUNT = "result_screen_score_count";
    public static final String GOAL_STAR_1 = "goal_star_1";
    public static final String GOAL_STAR_2 = "goal_star_2";
    public static final String GOAL_STAR_3 = "goal_star_3";
    public static final String GOAL_CHEST_OPEN = "goal_chestOpen";
    public static final String GIFT_SHOW = "gift_show";
    public static final String AP_LOSE_SOUND = "ap_lose_sound";
    public static final String COCOON_APPEAR = "cocoon_appear";
    public static final String COCOON_HIT = "cocoon_hit";
    public static final String COCOON_RELEASE_B = "cocoon_release_b";
    public static final String EAT_BUTTERFLY = "eat_butterfly";
    public static final String X2_APPEAR = "x2_appear";
    public static final String X2_EATEN = "x2_eaten";
    public static final String REVIVE = "revive";
    public static final String CANNON_FIRE = "cannon_fire";
    public static SoundMgr soundMgr;
    public static boolean soundOn = false;
//    private Random rand;
    private int r = 0;

    public HashMap<String, Sound> fx = new HashMap<String, Sound>();

    private SoundMgr() {
//        rand = new Random();
        loadSound(EAT_SOUND);
        loadSound(EAT_SOUND_1);
        loadSound(EAT_SOUND_2);
        loadSound(BUTTON_TAP);
        loadSound(SPECIAL_OFFER);
        loadSound(PAPER_FLIP_SHOP);
        loadSound(WIND_POP_UP_OPEN);
        loadSound(WIND_POP_UP_CLOSE);
        loadSound(BUTTON_TAP_SHOP_BUY);
        loadSound(SPARKLE);
        loadSound(PROGRESS_BAR_COUNT);
        loadSound(SCORE_COUNT);
        loadSound(GOAL_STAR_1);
        loadSound(GOAL_STAR_2);
        loadSound(GOAL_STAR_3);
        loadSound(GOAL_CHEST_OPEN);
        loadSound(GIFT_SHOW);
        loadSound(AP_LOSE_SOUND);
        loadSound(COCOON_APPEAR);
        loadSound(COCOON_HIT);
        loadSound(COCOON_RELEASE_B);
        loadSound(EAT_BUTTERFLY);
        loadSound(X2_APPEAR);
        loadSound(X2_EATEN);
        loadSound(REVIVE);
        loadSound(CANNON_FIRE);
     //   loadSound("tuturu");
    }

    public static SoundMgr getSoundMgr() {
        if (soundMgr == null) {
            soundMgr = new SoundMgr();
        }

        return soundMgr;
    }

    private void loadSound(String name) {
        fx.put(name, Gdx.audio.newSound(Gdx.files.internal("sound/" + name + ".ogg")));
    }

    public void play(String name) {
        if (soundOn) {
            if(name == EAT_SOUND){
                r++;
//                int r = rand.nextInt(3);
                if(r == 2){
                    name = EAT_SOUND_2;
                    play(name, 0.2f);
//                    System.out.println(name);
                }else if(r == 3){
                    name = EAT_SOUND_1;
                    play(name, 0.2f);
//                    System.out.println(name);
                    r = 0;
                }else{
                    play(name, 0.2f);
//                    System.out.println(name);
                }
            }else {
                fx.get(name).play();
            }
//            System.out.println("Playing sound: " + name);
        }
    }

    public void stop(String name) {
        fx.get(name).stop();
    }

    public void play(String name, boolean loop){
        if(loop) {
            fx.get(name).play();
            fx.get(name).setLooping(fx.get(name).play(), true);
        }else{
            fx.get(name).play();
        }
    }

    public void play(String name, float volume) {
        long id = fx.get(name).play();
        fx.get(name).setVolume(id, volume);
    }
}
