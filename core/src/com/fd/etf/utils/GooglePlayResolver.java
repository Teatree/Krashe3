package com.fd.etf.utils;

import com.badlogic.gdx.pay.PurchaseManagerConfig;
import com.fd.etf.Main;
import com.fd.etf.utils.PlatformResolver;

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