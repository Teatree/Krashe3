package com.mygdx.etf.entity.componets;

import com.badlogic.ashley.core.Component;

/**
 * Created by AnastasiiaRudyk on 7/1/2016.
 */
public class ToggleButtonComponent implements Component {

    public static String ON = "ON";
    public static String OFF = "OFF";

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
}
