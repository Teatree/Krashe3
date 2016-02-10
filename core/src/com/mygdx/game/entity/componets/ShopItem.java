package com.mygdx.game.entity.componets;

public abstract class ShopItem {

    public String shopIcon;
    public String name;
    public long cost;
    public String description;
    public CurrencyType type;

    //true when was bought (could be not applied)
    public boolean bought;
    //true when is applied now
    public boolean enabled ;

    public abstract void apply (FlowerPublicComponent fpc);

    public abstract void disable(FlowerPublicComponent fpc);

    public abstract void buyAndUse(FlowerPublicComponent fpc);

    public enum CurrencyType {
        SOFT, HARD
    }
}
