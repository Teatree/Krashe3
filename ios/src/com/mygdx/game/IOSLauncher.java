package com.mygdx.game;

import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;

import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;
import com.mygdx.game.Main;

public class IOSLauncher extends IOSApplication.Delegate implements AdsController {
    @Override
    protected IOSApplication createApplication() {
        IOSApplicationConfiguration config = new IOSApplicationConfiguration();
        return new IOSApplication(new Main(this), config);
    }

    public static void main(String[] argv) {
        NSAutoreleasePool pool = new NSAutoreleasePool();
        UIApplication.main(argv, null, IOSLauncher.class);
        pool.close();
    }

    @Override
    public boolean isWifiConnected() {
        return false;
    }

    @Override
    public void showReviveVideoAd(Runnable then) {

    }

    @Override
    public void showGetMoneyVideoAd(Runnable then) {

    }

    @Override
    public void showLaunchAd(Runnable then) {

    }

    @Override
    public boolean shouldShowGetMoneyVideoBtnAd(long need) {
        return false;
    }

    @Override
    public void showResultScreenAd(Runnable then) {

    }

    @Override
    public void showGeneralShopAd(Runnable then) {

    }

    @Override
    public boolean shouldShowLaunchAd() {
        return false;
    }

    @Override
    public boolean shouldShowShopAd() {
        return false;
    }

    @Override
    public boolean shouldShowResultAd() {
        return false;
    }

    @Override
    public boolean shouldShowReviveVideoBtnAd() {
        return false;
    }
}