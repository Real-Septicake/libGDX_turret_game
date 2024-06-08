package com.mygdx.game.factories.turrets;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.mygdx.game.components.*;

public final class TurretUtils {
    private static final ComponentMapper<TransformComponent> transM = ComponentMapper.getFor(TransformComponent.class);
    private static final ComponentMapper<TurretTextureComponent> turretTexM = ComponentMapper.getFor(TurretTextureComponent.class);

    private static final Family TURRETS = Family.all(TurretTextureComponent.class, TransformComponent.class, TurretComponent.class).get();

    public static void rotateTowards(Entity entity, Entity target) {
        if(!TURRETS.matches(entity))
            throw new IllegalArgumentException("Cannot rotate a non-turret entity");

        TurretTextureComponent tex = turretTexM.get(entity);
        TransformComponent trans = transM.get(entity);

        TransformComponent pos = transM.get(target);

        float a = MathUtils.radiansToDegrees * MathUtils.atan2(pos.pos.y - (trans.pos.y + tex.originX), pos.pos.x - (trans.pos.x + tex.originX));
        trans.rotation = a < 0 ? a + 360 : a;
    }

    public static void setTurretTex(Entity entity, TextureRegion texture, float originX, float originY) {
        if(!TURRETS.matches(entity))
            throw new IllegalArgumentException("Cannot set texture of non-turret entity");

        TurretTextureComponent tex = turretTexM.get(entity);

        tex.turretGun = texture;
        tex.originX = originX;
        tex.originY = originY;
    }

    private static final ComponentMapper<EnemyComponent> enemyM = ComponentMapper.getFor(EnemyComponent.class);

    private static final Family ENEMY = Family.all(EnemyComponent.class).exclude(EnemyComponent.class).get();

    public static void damage(Entity target, float damage) {
        if(!ENEMY.matches(target))
            throw new IllegalArgumentException("Cannot damage a non-enemy entity.");

        EnemyComponent enemy = enemyM.get(target);

        enemy.health -= damage;
        if(enemy.health <= 0) {
            EnemyDisposeComponent dispose = new EnemyDisposeComponent();
            dispose.state = EnemyDisposeComponent.KILL;
            target.add(dispose);
        }
    }
}