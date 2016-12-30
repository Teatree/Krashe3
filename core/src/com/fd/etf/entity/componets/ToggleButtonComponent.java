package com.fd.etf.entity.componets;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

/**
 * Created by AnastasiiaRudyk on 7/1/2016.
 */
public class ToggleButtonComponent implements Component, Pool.Poolable{

    public static final String ON = "ON";
    public static final  String OFF = "OFF";

    private String state = ON;

    public boolean isOn(){
        return ON.equals(state);
    }

    public void setOff() {
        state = ToggleButtonComponent.OFF;
    }

    public void setOn() {
        state = ToggleButtonComponent.ON;
    }

    @Override
    public void reset() {
        state = ON;
    }
}
