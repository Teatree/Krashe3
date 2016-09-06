package com.mygdx.etf;

/**
 * Created by ARudyk on 9/6/2016.
 */
public interface PlayServices {
    public void signIn();
    public void signOut();
    public void rateGame();
    public void submitScore(int highScore);
    public void showScore();
    public boolean isSignedIn();
}
