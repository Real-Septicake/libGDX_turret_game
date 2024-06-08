package com.mygdx.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.TurretGame;
import com.mygdx.game.components.EnemyComponent;
import com.mygdx.game.components.EnemyDisposeComponent;
import com.mygdx.game.components.TransformComponent;

public class EnemySystem extends IteratingSystem {
    private static final ComponentMapper<TransformComponent> transM = ComponentMapper.getFor(TransformComponent.class);
    private static final ComponentMapper<EnemyComponent> enemyM = ComponentMapper.getFor(EnemyComponent.class);

    public EnemySystem() {
        super(Family.all(EnemyComponent.class).exclude(EnemyDisposeComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        EnemyComponent e = enemyM.get(entity);

        Vector2 out = new Vector2();
        TurretGame.spline.derivativeAt(out, e.current);
        e.current += (deltaTime * e.speed / TurretGame.spline.spanCount) / out.len();

        if(e.current > 1) {
            EnemyDisposeComponent dispose = new EnemyDisposeComponent();
            dispose.state = EnemyDisposeComponent.PATH;
            entity.add(dispose);
            return;
        }
        float place = e.current * TurretGame.resolution;
        Vector2 first = TurretGame.points[(int)place];
        Vector2 second;
        if(((int)place + 1) < TurretGame.resolution)
            second = TurretGame.points[(int)place + 1];
        else {
            EnemyDisposeComponent dispose = new EnemyDisposeComponent();
            dispose.state = EnemyDisposeComponent.PATH;
            entity.add(dispose);
            return;
        }
        float t = place - (int)place;
        TransformComponent trans = transM.get(entity);
        trans.pos.set(first.x + (second.x - first.x) * t, first.y + (second.y - first.y) * t, 0);
    }
}
