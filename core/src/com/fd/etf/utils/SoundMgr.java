package com.fd.etf.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

import java.util.HashMap;

public class SoundMgr {

    public static final String EAT_SOUND = "eat";
    public static SoundMgr soundMgr;
    public static boolean soundOn = false;

    public HashMap<String, Sound> fx = new HashMap<String, Sound>();

    private SoundMgr() {
        loadSound("eat");
        loadSound("tuturu");
    }

    public static SoundMgr getSoundMgr() {
        if(soundMgr == null) {
            soundMgr = new SoundMgr();
        }

        return soundMgr;
    }

    private void loadSound(String name) {
        fx.put(name, Gdx.audio.newSound(Gdx.files.internal("sound/" + name + ".mp3")));
    }

    public void play(String name) {
        if (soundOn) {
            fx.get(name).play();
        }
    }

    public void play(String name, float volume) {
        long id = fx.get(name).play();
        fx.get(name).setVolume(id, volume);
    }
}
