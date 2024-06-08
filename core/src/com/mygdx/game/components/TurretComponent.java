package com.mygdx.game.components;

import com.badlogic.ashley.core.Component;
import com.mygdx.game.factories.turrets.Act;

public class TurretComponent implements Component {
    public static final int FIRST = 1;
    public static final int LAST = 2;
    public static final int CLOSE = 3;
    public static final int FAR = 4;

    public int targeting = 1;
    public float range;

    public float damage;

    public Act act;
}
