package com.mygdx.game.components;

import com.badlogic.ashley.core.Component;
import com.mygdx.game.entities.enemies.EnemyListener;

public class EnemyComponent implements Component {
    public float speed;
    public float current = 0.0f;

    public int maxHealth;
    public float health;

    public int value;

    public EnemyListener listener;
}
