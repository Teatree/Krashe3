package com.mygdx.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class BackgroundMusicMgr {

    public static BackgroundMusicMgr backgroundMusicMgr;
    public static boolean musicOn;

    private Music music;

    private BackgroundMusicMgr() {
        music = Gdx.audio.newMusic(Gdx.files.internal("sound/back_ground.mp3"));
        music.setLooping(true);
    }

    public static BackgroundMusicMgr getBackgroundMusicMgr() {
        if(backgroundMusicMgr == null) {
            backgroundMusicMgr = new BackgroundMusicMgr();
        }

        return backgroundMusicMgr;
    }

    public void play() {
        if(!music.isPlaying()) {
            music.play();
        }
    }

    public void stop() {
        music.stop();
    }


}
