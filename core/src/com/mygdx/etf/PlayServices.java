package com.mygdx.etf;

/**
 * Created by ARudyk on 9/6/2016.
 */
public interface PlayServices {

    String ACH_HUNGER_STRIKE = "Hunger Strike";
    String ACH_BUTTEFLY_EATER = "Buttefly Eater";
    String ACH_COLLECTOR = "Collector";
    String ACH_DOG_PERSONE = "Dog persone";
    String ACH_QUEEN_SLAYER = "Queen Slayer";

    void signIn();

    void signOut();

    void submitScore(int highScore);

    void showScore();

    boolean isSignedIn();

    void setupPlayServices();

    void unlockAchievement(String achievementId);

    void revealAchievement(String achievementId);

    void incrementAchievement(String achievementId, int steps);

    void getLeaderboard();

    void getAchievements();
}
