package com.mygdx.game;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.mygdx.game.components.*;
import com.mygdx.game.entities.enemies.EnemyRoot;
import com.mygdx.game.entities.turrets.TurretRoot;

public class World {
    public static int money;
    public static int lives;
    public static int round;

    private static ComponentMapper<TurretComponent> turretM = ComponentMapper.getFor(TurretComponent.class);

    private final PooledEngine engine;

    public World(PooledEngine engine) {
        money = 500;
        lives = 100;
        round = 0;

        this.engine = engine;
    }

    public void sellTurret(Entity e) {
        TurretComponent t = turretM.get(e);
        money += t.value;
        engine.removeEntity(e);
    }

    public void createTurret(TurretRoot turret, float x, float y) {
        Entity e = engine.createEntity();

        TurretTextureComponent tex = engine.createComponent(TurretTextureComponent.class);
        tex.turretGun = turret.initTex();
        tex.turretBase = turret.base();
        tex.originX = turret.originX();
        tex.originY = turret.originY();
        e.add(tex);

        TransformComponent trans = engine.createComponent(TransformComponent.class);
        trans.pos.x = x;
        trans.pos.y = y;
        e.add(trans);

        RectBoundsComponent bounds = engine.createComponent(RectBoundsComponent.class);
        bounds.bounds.x = x;
        bounds.bounds.y = y;
        bounds.bounds.width = turret.base().getWidth();
        bounds.bounds.height = turret.base().getHeight();
        e.add(bounds);

        TurretComponent t = engine.createComponent(TurretComponent.class);
        t.range = turret.range();
        t.damage = turret.damage();
        t.act = turret.act();
        t.value = turret.cost();
        t.type = turret;
        e.add(t);

        e.add(engine.createComponent(BuyingComponent.class));
        e.add(engine.createComponent(StateComponent.class));

        engine.addEntity(e);
    }

    public void createEnemy(EnemyRoot enemy) {
        Entity e = engine.createEntity();

        EnemyTextureComponent textureComp = engine.createComponent(EnemyTextureComponent.class);
        textureComp.texture = enemy.texture();
        textureComp.originX = enemy.originX();
        textureComp.originY = enemy.originY();
        e.add(textureComp);

        EnemyComponent enemyComp = engine.createComponent(EnemyComponent.class);
        enemyComp.current = 0.0f;
        enemyComp.speed = enemy.speed();
        enemyComp.maxHealth = enemy.maxHealth();
        enemyComp.health = enemy.maxHealth();
        enemyComp.value = enemy.value();
        e.add(enemyComp);

        e.add(engine.createComponent(TransformComponent.class));
        engine.addEntity(e);
    }
}
