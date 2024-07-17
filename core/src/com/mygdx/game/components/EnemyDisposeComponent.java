package com.mygdx.game.components;

import com.badlogic.ashley.core.Component;

public class EnemyDisposeComponent implements Component {
    /** A state value representing an enemy that needs to be disposed because it made it to the end of the path */
    public static final int PATH = 1;
    /** A state value representing an enemy that needs to be disposed because it was killed by turrets */
    public static final int KILL = 2;

    public int state;
}

