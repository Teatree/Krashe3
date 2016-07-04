package com.mygdx.etf.entity.componets;

public abstract class ShopItem {

    public static String SOFT = "SOFT";
    public static String HARD = "HARD";

    public String shopIcon;
    public String name;
    public long cost;
    public String description;
    public String currencyType;

    public String transactionId;
    public String discountTransactionId;
    public String logoName;

    //true when was bought (could be not applied)
    public boolean bought;
    //true when is applied now
    public boolean enabled ;

    public abstract void apply ();

    public abstract void disable();

    public abstract void buyAndUse();

    public abstract void buyHard();

    public abstract void buyHardDiscount();
}
