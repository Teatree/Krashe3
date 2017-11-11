package com.fd.etf.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Interpolation;
import com.brashmonkey.spriter.Entity;
import com.fd.etf.stages.GameStage;
import com.uwsoft.editor.renderer.components.ActionComponent;
import com.uwsoft.editor.renderer.systems.action.Actions;

public class BackgroundMusicMgr {

    public static BackgroundMusicMgr backgroundMusicMgr;
    public static boolean musicOn;

    public Music musicMenu;
    public Music musicGame;

    private BackgroundMusicMgr() {
        musicMenu = Gdx.audio.newMusic(Gdx.files.internal("sound/background_menu.ogg"));
        musicMenu.setLooping(true);
        musicGame = Gdx.audio.newMusic(Gdx.files.internal("sound/background_game.ogg"));
//        musicGame.setLooping(true);
//        musicGame.setVolume(0.2f);
    }

    public static BackgroundMusicMgr getBackgroundMusicMgr() {
        if (backgroundMusicMgr == null) {
            backgroundMusicMgr = new BackgroundMusicMgr();
        }

        return backgroundMusicMgr;
    }

    public void playMenu() {
        if (musicOn) {
            if (!musicMenu.isPlaying()) {
                stopGame();
                musicMenu.play();
                musicMenu.setVolume(0.2f);

                musicMenu.setLooping(true);
            }
        }
    }
    public void playGame() {
        if (musicOn) {
            stopMenu();
//            long idd = musicGame.play();
            //musicGame.setPitch(idd, 2.2f);
            musicGame.play();
            musicGame.setVolume(0.2f);

            musicGame.setLooping(true);
        }
    }

    public void stopMenu() {
        musicMenu.stop();
    }

    public void stopGame() {
        musicGame.stop();
    }

}
