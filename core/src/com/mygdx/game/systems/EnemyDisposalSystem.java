package com.mygdx.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.mygdx.game.World;
import com.mygdx.game.components.EnemyComponent;
import com.mygdx.game.components.EnemyDisposeComponent;

public class EnemyDisposalSystem extends IteratingSystem {
    Engine engine;
    private final World world;

    private static final ComponentMapper<EnemyComponent> enemyM = ComponentMapper.getFor(EnemyComponent.class);
    private static final ComponentMapper<EnemyDisposeComponent> disposeM = ComponentMapper.getFor(EnemyDisposeComponent.class);

    public EnemyDisposalSystem(World world) {
        super(Family.all(EnemyComponent.class, EnemyDisposeComponent.class).get());
        this.world = world;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);

        this.engine = engine;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        EnemyDisposeComponent dispose = disposeM.get(entity);
        EnemyComponent enemy = enemyM.get(entity);

        if(dispose.state == EnemyDisposeComponent.PATH) {
            World.lives -= enemy.maxHealth;
            engine.removeEntity(entity);
        } else if(dispose.state == EnemyDisposeComponent.KILL) {
            World.money += enemy.value;
            enemy.listener.onDeath(world, enemy.current);
            engine.removeEntity(entity);
        }
    }
}
