package com.mygdx.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.components.*;
import com.mygdx.game.factories.turrets.Act;

public class World {
    public static int money;
    public static int lives;

    private final PooledEngine engine;

    public World(PooledEngine engine) {
        money = 500;
        lives = 100;

        this.engine = engine;
    }

    public Entity createTurret(Act act, TextureRegion initGun, Texture base,
                               float originX, float originY, float x, float y,
                               float centerX, float centerY, float range, float damage) {
        Entity e = engine.createEntity();

        TurretTextureComponent tex = engine.createComponent(TurretTextureComponent.class);
        tex.turretGun = initGun;
        tex.turretBase = base;
        tex.originX = originX;
        tex.originY = originY;
        tex.centerX = centerX;
        tex.centerY = centerY;
        e.add(tex);

        TransformComponent trans = engine.createComponent(TransformComponent.class);
        trans.pos.x = x;
        trans.pos.y = y;
        e.add(trans);

        RectBoundsComponent bounds = engine.createComponent(RectBoundsComponent.class);
        bounds.bounds.x = x;
        bounds.bounds.y = y;
        bounds.bounds.width = base.getWidth();
        bounds.bounds.height = base.getHeight();
        e.add(bounds);

        TurretComponent turret = engine.createComponent(TurretComponent.class);
        turret.range = range;
        turret.damage = damage;
        turret.act = act;
        e.add(turret);

        e.add(engine.createComponent(BuyingComponent.class));
        e.add(engine.createComponent(StateComponent.class));

        return e;
    }

    public Entity createEnemy(Texture texture, float originX, float originY, float speed, int maxHealth, int value) {
        Entity e = new Entity();

        EnemyTextureComponent textureComp = new EnemyTextureComponent();
        textureComp.texture = texture;
        textureComp.originX = originX;
        textureComp.originY = originY;
        e.add(textureComp);

        EnemyComponent enemyComp = new EnemyComponent();
        enemyComp.speed = speed;
        enemyComp.maxHealth = maxHealth;
        enemyComp.health = maxHealth;
        enemyComp.value = value;
        e.add(enemyComp);

        e.add(new TransformComponent());
        return e;
    }
}
