package com.mygdx.game.entities.turrets;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.components.StateComponent;

public class BasicTurret extends TurretRoot {
    private static final Texture TURRET_ASSETS = new Texture("TurretBasic.png");

    private static final TextureRegion TURRET_STATIC = new TextureRegion(TURRET_ASSETS, 104, 64);
    private static final TextureRegion TURRET_FIRE = new TextureRegion(TURRET_ASSETS, 0, 64, 134, 64);
    private static final Texture BASE = new Texture("BasicBase.png");

    private static final float CENTER_X = 40f, CENTER_Y = 40f;

    private static final float STATIC_ORIGIN_X = 32, STATIC_ORIGIN_Y = 32;
    private static final float FIRE_ORIGIN_X = 40, FIRE_ORIGIN_Y = 32;

    private static final float RANGE = 200;
    private static final float DAMAGE = 3;
    private static final int COST = 200;

    private static final String NAME = "Basic Turret";

    // Default delay values
    private static final int READY = 0,
                            ANIM  = 1,
                            WAIT  = 2;

    private static final float ANIMATION = 0.1f,
                            DELAY = 0.9f;

    public static final BasicTurret INSTANCE = new BasicTurret();

    private BasicTurret() {}

    private static void act(Entity entity, Entity target, float delta) {
        StateComponent state = stateM.get(entity);
        switch(state.get()) {
            case READY -> {
                if(target != null) {
                    TurretUtils.rotateTowards(entity, target);
                    TurretUtils.damage(target, DAMAGE);
                    state.set(ANIM);
                    TurretUtils.setTurretTex(entity, TURRET_FIRE, FIRE_ORIGIN_X, FIRE_ORIGIN_Y);
                }
            }
            case ANIM -> {
                if(state.time >= ANIMATION) {
                    TurretUtils.setTurretTex(entity, TURRET_STATIC, STATIC_ORIGIN_X, STATIC_ORIGIN_Y);
                    state.set(WAIT);
                }
            }
            case WAIT -> {
                if(state.time >= DELAY)
                    state.set(READY);
            }
        }
    }

    @Override
    public Act act() {
        return BasicTurret::act;
    }

    @Override
    public TextureRegion initTex() {
        return TURRET_STATIC;
    }

    @Override
    public Texture base() {
        return BASE;
    }

    @Override
    public float originX() {
        return STATIC_ORIGIN_X;
    }

    @Override
    public float originY() {
        return STATIC_ORIGIN_Y;
    }

    @Override
    public float centerX() {
        return CENTER_X;
    }

    @Override
    public float centerY() {
        return CENTER_Y;
    }

    @Override
    public float range() {
        return RANGE;
    }

    @Override
    public float damage() {
        return DAMAGE;
    }

    @Override
    public int cost() {
        return COST;
    }

    @Override
    public String name() {
        return NAME;
    }
}
