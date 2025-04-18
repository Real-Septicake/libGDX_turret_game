package com.mygdx.game.entities.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.World;

public class BasicEnemy extends EnemyRoot {
    private static final Texture TEXTURE = new Texture("BasicEnemy.png");
    private static final float ORIGIN_X = 24, ORIGIN_Y = 24;
    private static final float SPEED = 300f;
    private static final int HEALTH = 5;
    private static final int VALUE = 3;

    public static final BasicEnemy INSTANCE = new BasicEnemy();

    public static final EnemyListener LISTENER = new EnemyListener() {
        @Override
        public void onDamage(float damage) {

        }

        @Override
        public void onDeath(World world, float progress) {

        }
    };

    private BasicEnemy() {}

    @Override
    public Texture texture() {
        return TEXTURE;
    }

    @Override
    public float originX() {
        return ORIGIN_X;
    }

    @Override
    public float originY() {
        return ORIGIN_Y;
    }

    @Override
    public float speed() {
        return SPEED;
    }

    @Override
    public int maxHealth() {
        return HEALTH;
    }

    @Override
    public int value() {
        return VALUE;
    }

    @Override
    public EnemyListener listener() {
        return LISTENER;
    }

    public static BasicEnemy getInstance() {
        return INSTANCE;
    }
}
