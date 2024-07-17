package com.mygdx.game.entities.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.RandomXS128;
import com.mygdx.game.World;

public class SplitterEnemy extends EnemyRoot {
    private static final Texture TEXTURE = new Texture("BasicEnemy.png");
    private static final float ORIGIN_X = 24, ORIGIN_Y = 24;
    private static final float SPEED = 400f;
    private static final int HEALTH = 2;
    private static final int VALUE = 3;

    private static final EnemyListener LISTENER = new EnemyListener() {
        @Override
        public void onDamage(float damage) {

        }

        @Override
        public void onDeath(World world, float progress) {
            RandomXS128 ran = new RandomXS128();
            for(int i = 0; i < 3; i++) {
                world.createEnemy(BasicEnemy.INSTANCE, progress - ran.nextFloat(0, 0.01f));
            }
        }
    };

    public static final SplitterEnemy INSTANCE = new SplitterEnemy();

    private SplitterEnemy() {}

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

    public static SplitterEnemy getInstance() {
        return INSTANCE;
    }
}
