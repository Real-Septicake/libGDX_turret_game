package com.mygdx.game.components;

import com.badlogic.ashley.core.Component;
import com.mygdx.game.entities.turrets.Act;
import com.mygdx.game.entities.turrets.TurretRoot;

public class TurretComponent implements Component {
    public static final int FIRST = 0;
    public static final int LAST = 1;
    public static final int CLOSE = 2;
    public static final int FAR = 3;

    public int targeting = FIRST;
    public float range;
    public float damage;

    public Act act;

    public int value;

    public TurretRoot type;
}
