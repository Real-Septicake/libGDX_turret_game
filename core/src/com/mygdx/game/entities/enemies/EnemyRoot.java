package com.mygdx.game.entities.enemies;

import com.badlogic.gdx.graphics.Texture;

public abstract class EnemyRoot {
    public abstract Texture texture();
    public abstract float originX();
    public abstract float originY();
    public abstract float speed();
    public abstract int maxHealth();
    public abstract int value();
}
