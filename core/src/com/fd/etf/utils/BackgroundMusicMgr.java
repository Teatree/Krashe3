package com.fd.etf.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class BackgroundMusicMgr {

    public static BackgroundMusicMgr backgroundMusicMgr;
    public static boolean musicOn;

    private Music musicMenu;
    private Sound musicGame;

    private BackgroundMusicMgr() {
        musicMenu = Gdx.audio.newMusic(Gdx.files.internal("sound/background_menu.mp3"));
        musicMenu.setLooping(true);
        musicGame = Gdx.audio.newSound(Gdx.files.internal("sound/background_game.mp3"));
//        musicGame.setLooping(true);
//        musicGame.setVolume(0.2f);
        musicMenu.setVolume(0.2f);
    }

    public static BackgroundMusicMgr getBackgroundMusicMgr() {
        if (backgroundMusicMgr == null) {
            backgroundMusicMgr = new BackgroundMusicMgr();
        }

        return backgroundMusicMgr;
    }

    public void playMenu() {
        if (!musicMenu.isPlaying()) {
            stopGame();
            musicMenu.play();
        }
    }
    public void playGame() {
//        if (!musicGame.isPlaying()) {
            stopMenu();
            long idd = musicGame.play();
        musicGame.setPitch(idd, 2.2f);
        musicGame.setLooping(idd, true);
//        }
    }

    public void stopMenu() {
        musicMenu.stop();
    }

    public void stopGame() {
        musicGame.stop();
    }

}
