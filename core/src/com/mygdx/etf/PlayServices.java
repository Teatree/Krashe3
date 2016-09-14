package com.mygdx.etf;

/**
 * Created by ARudyk on 9/6/2016.
 */
public interface PlayServices {
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
