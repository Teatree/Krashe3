package com.mygdx.game.android;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import android.widget.Toast;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;

import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.*;
import com.mygdx.game.Main;
import com.mygdx.game.AdsController;

public class AndroidLauncher extends AndroidApplication  implements AdsController {
//        implements AndroidFragmentApplication.Callbacks{

    private static final String BANNER_AD_UNIT_ID = "ca-app-pub-4809397092315700/3739329274";
    private static final String INTERSTITIAL_UNIT_ID = "ca-app-pub-4809397092315700/1974891273";

    AdView bannerAd;
    InterstitialAd interstitialAd;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
//		initialize(new Main(), config);
        View gameView = initializeForView(new Main(this), config);
		setupAds();

		RelativeLayout layout = new RelativeLayout(this);
		layout.addView(gameView, ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
//
//		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
//				ViewGroup.LayoutParams.MATCH_PARENT,
//				ViewGroup.LayoutParams.WRAP_CONTENT);
//		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//		layout.addView(bannerAd, params);

		setContentView(layout);
	}

	public void setupAds() {
//		bannerAd = new AdView(this);
//		bannerAd.setVisibility(View.VISIBLE);
//		bannerAd.setBackgroundColor(0xff000000); // black
//		bannerAd.setAdUnitId(BANNER_AD_UNIT_ID);
//		bannerAd.setAdSize(AdSize.SMART_BANNER);

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(INTERSTITIAL_UNIT_ID);

        AdRequest.Builder builder = new AdRequest.Builder();
        AdRequest ad = builder.build();
        interstitialAd.loadAd(ad);
	}

	@Override
	public void showBannerAd() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				bannerAd.setVisibility(View.VISIBLE);
				AdRequest.Builder builder = new AdRequest.Builder();
				AdRequest ad = builder.build();
				bannerAd.loadAd(ad);
			}
		});
	}

	@Override
	public void hideBannerAd() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				bannerAd.setVisibility(View.INVISIBLE);
			}
		});
	}

    @Override
    public boolean isWifiConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return (ni != null && ni.isConnected());
    }

    @Override
    public void showInterstitialAd(final Runnable then) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (then != null) {
                    interstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdClosed() {
                            Gdx.app.postRunnable(then);
//                            AdRequest.Builder builder = new AdRequest.Builder();
//                            AdRequest ad = builder.build();
//                            interstitialAd.loadAd(ad);
                        }
                        @Override
                        public void onAdLoaded() {
                            Toast.makeText(getApplicationContext(), "Finished Loading Interstitial", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                interstitialAd.show();
            }
        });
    }
}
