package com.fd.etf.entity.componets;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by ARudyk on 11/9/2016.
 */
public class DebugComponent implements Component {
    public boolean trace = true;
    public Rectangle boundingBox;

    public DebugComponent(){
        trace = true;
    }

    public DebugComponent(Rectangle box){
        trace = true;
        boundingBox = box;
    }
}
