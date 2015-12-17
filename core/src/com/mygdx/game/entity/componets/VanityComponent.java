package com.mygdx.game.entity.componets;

import com.badlogic.ashley.core.Component;

/**
 * Created by AnastasiiaRudyk on 12/14/2015.
 */
public class VanityComponent implements Component {

    public String name;
    public int cost;
    public String description;
    public boolean bought;
    public boolean enabled;

    public void apply (FlowerComponent fc){

    }
}

