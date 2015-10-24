package com.mygdx.game.entity.componets;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.utils.GlobalConstants;
import com.uwsoft.editor.renderer.data.CompositeItemVO;

/**
 * Created by Teatree on 9/3/2015.
 */
public class FlowerComponent implements Component {
    public static long pointsAmount = 0L;
    public static long sessionPointsAmount = 0L;

    private int maxHp = GlobalConstants.DEFAULT_MAX_HP;
    private int curHp = maxHp;

    public Rectangle boundsRect = new Rectangle();

    private CompositeItemVO flowerLib;

    public boolean isMovingUp = false;
    public boolean isEating = false;
    public State state = State.IDLE;

    private int eatCounter;

    public enum State{
        IDLE,
        ATTACK,
        IDLE_BITE,
        ATTACK_BITE;
    }

}
