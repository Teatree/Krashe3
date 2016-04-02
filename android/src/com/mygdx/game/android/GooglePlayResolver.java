package com.mygdx.game.android;

import com.badlogic.gdx.pay.PurchaseManagerConfig;
import com.mygdx.game.Main;
import com.mygdx.game.PlatformResolver;

public class GooglePlayResolver extends PlatformResolver {

    private final static String GOOGLEKEY  = ".......MII...........Ttu.............Fctj......0A6W.........y.";


    static final int RC_REQUEST = 10001;	// (arbitrary) request code for the purchase flow

    public GooglePlayResolver(Main game) {
        super(game);

        PurchaseManagerConfig config = game.purchaseManagerConfig;
        config.addStoreParam(PurchaseManagerConfig.STORE_NAME_ANDROID_GOOGLE, GOOGLEKEY);
        initializeIAP(null, game.purchaseObserver, config);
    }
}