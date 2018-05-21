package com.fd.etf.android.util;

import android.content.Intent;
import android.util.Log;
import com.badlogic.gdx.Gdx;
import com.fd.etf.android.AndroidLauncher;
import com.fd.etf.android.R;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.GameHelper;

public class  EtfPlayServicesHelper {
    private GameHelper gameHelper;

    AndroidLauncher app;
    private final static int requestCode = 1;


    public EtfPlayServicesHelper(AndroidLauncher app) {
        this.app = app;
    }

    public void setupPlayServices() {
        gameHelper = new GameHelper(app, GameHelper.CLIENT_GAMES);
        gameHelper.enableDebugLog(true);

        final GameHelper.GameHelperListener gameHelperListener = new GameHelper.GameHelperListener() {
            @Override
            public void onSignInFailed() {
                Log.e("PS", "onSignInFailed");
                gameHelper.showFailureDialog();
            }


            @Override
            public void onSignInSucceeded() {
                Log.e("PS", "onSignInSucceeded");
            }
        };

        gameHelper.setup(gameHelperListener);
    }

    public void onStart() {
        Log.e("PS", "onStart");
        gameHelper.onStart(app);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        gameHelper.onActivityResult(requestCode, resultCode, data);
    }

    public void signIn() {
        Log.e("PS", "signIn");
        try {
            app.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gameHelper.beginUserInitiatedSignIn();
                    Log.e("PS", "gameHelper.beginUserInitiatedSignIn");
                }
            });
        } catch (Exception e) {
            Gdx.app.log("MainActivity", "Log in failed: " + e.getMessage() + ".");
        }
    }

    public void signOut() {
        try {
            app.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gameHelper.signOut();
                }
            });
        } catch (Exception e) {
            Gdx.app.log("MainActivity", "Log out failed: " + e.getMessage() + ".");
        }
    }

    public void submitScore(int highScore) {
        if (isSignedIn()) {
            Games.Leaderboards.submitScore(gameHelper.getApiClient(),
                    app.getString(R.string.leaderboard_leaderboard), highScore);
        }
    }

    public void showScore() {
        if (isSignedIn()) {
            app.startActivityForResult(Games.Leaderboards.getLeaderboardIntent(gameHelper.getApiClient(),
                    app.getString(R.string.leaderboard_leaderboard)), requestCode);
        } else {
            signIn();
        }
    }

    public boolean isSignedIn() {
        return gameHelper.isSignedIn();
    }

    public void unlockAchievement(String achievementId) {
        Games.Achievements.unlock(gameHelper.getApiClient(),
                app.getString(R.string.achievement_queens_slayer));
    }

    public void getLeaderboard() {
        if (isSignedIn()) {
            app.startActivityForResult(Games.Leaderboards.getLeaderboardIntent(gameHelper.getApiClient(),
                    app.getString(R.string.leaderboard_leaderboard)), requestCode);
        } else {
            signIn();
        }
    }

    public void getAchievements() {
        if (isSignedIn()) {
            app.startActivityForResult(Games.Achievements.getAchievementsIntent(gameHelper.getApiClient()), requestCode);
        } else {
            signIn();
        }
    }

//    private boolean checkPlayServices() {
//        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(app);
//        //...
//    }
}
