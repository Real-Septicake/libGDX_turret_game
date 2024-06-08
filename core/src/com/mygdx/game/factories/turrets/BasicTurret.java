package com.mygdx.game.factories.turrets;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.components.StateComponent;

public class BasicTurret extends TurretFactory {
    public static final Texture TURRET_ASSETS = new Texture("TurretBasic.png");

    public static final TextureRegion TURRET_STATIC = new TextureRegion(TURRET_ASSETS, 104, 64);
    public static final TextureRegion TURRET_FIRE = new TextureRegion(TURRET_ASSETS, 0, 64, 134, 64);
    public static final Texture BASE = new Texture("BasicBase.png");

    public static final float CENTER_X = 40f, CENTER_Y = 40f;

    public static final float STATIC_ORIGIN_X = 32, STATIC_ORIGIN_Y = 32;
    public static final float FIRE_ORIGIN_X = 40, FIRE_ORIGIN_Y = 32;

    public static final float RANGE = 200;
    public static final float DAMAGE = 3;

    public static final int READY = 0,
                            ANIM  = 1,
                            WAIT  = 2;

    public static final float ANIMATION = 0.1f,
                            DELAY = 0.9f;

    protected static void act(Entity entity, Entity target, float delta) {
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

    public static Entity create(float x, float y) {
        return create(BasicTurret::act, TURRET_STATIC, BASE, STATIC_ORIGIN_X,
                STATIC_ORIGIN_Y, x, y, CENTER_X, CENTER_Y, RANGE, DAMAGE);
    }
}
