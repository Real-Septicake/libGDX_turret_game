package com.mygdx.game.factories.enemies;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;

public class BasicEnemy extends EnemyRoot {
    public static final Texture TEXTURE = new Texture("BasicEnemy.png");
    public static final float ORIGIN_X = 24, ORIGIN_Y = 24;
    public static final float SPEED = 300f;
    public static final int HEALTH = 5;
    public static final int VALUE = 3;
    public static Entity create() {
        return create(TEXTURE, ORIGIN_X, ORIGIN_Y, SPEED, HEALTH, VALUE);
    }
}
