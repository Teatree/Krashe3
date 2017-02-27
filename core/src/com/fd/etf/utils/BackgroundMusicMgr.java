package com.fd.etf.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class BackgroundMusicMgr {

    public static BackgroundMusicMgr backgroundMusicMgr;
    public static boolean musicOn;

    private Music musicMenu;
    private Music musicGame;

    private BackgroundMusicMgr() {
        musicMenu = Gdx.audio.newMusic(Gdx.files.internal("sound/background_menu.mp3"));
        musicMenu.setLooping(true);
        musicGame = Gdx.audio.newMusic(Gdx.files.internal("sound/background_game.mp3"));
        musicGame.setLooping(true);
        musicGame.setVolume(0.2f);
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
        if (!musicGame.isPlaying()) {
            stopMenu();
            musicGame.play();
        }
    }

    public void stopMenu() {
        musicMenu.stop();
    }

    public void stopGame() {
        musicMenu.stop();
    }

}
