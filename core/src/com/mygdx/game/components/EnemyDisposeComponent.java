package com.mygdx.game.components;

import com.badlogic.ashley.core.Component;

public class EnemyDisposeComponent implements Component {
    public static final int PATH = 1;
    public static final int KILL = 2;

    public int state;
}
