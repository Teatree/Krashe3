package com.mygdx.etf.entity.componets;

public class VanityCollection {
    public int total;
    public int unlocked;
    public String name;

    public VanityCollection(VanityComponent vc) {
        this.name = vc.collection;
        this.unlocked = vc.bought ? 1 : 0;
        this.total = 1;
    }
}
