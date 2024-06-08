package com.mygdx.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.components.EnemyTextureComponent;
import com.mygdx.game.components.TransformComponent;


public class EnemyRenderingSystem extends IteratingSystem {
    private final Array<Entity> queue = new Array<>();

    private final SpriteBatch batch;

    private final ComponentMapper<EnemyTextureComponent> enemyTextureM = ComponentMapper.getFor(EnemyTextureComponent.class);
    private final ComponentMapper<TransformComponent> transM = ComponentMapper.getFor(TransformComponent.class);

    public EnemyRenderingSystem(SpriteBatch batch) {
        super(Family.all(EnemyTextureComponent.class, TransformComponent.class).get());

        this.batch = batch;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        batch.begin();

        for(Entity e : queue) {
            EnemyTextureComponent texture = enemyTextureM.get(e);
            if(texture == null)
                continue;
            TransformComponent trans = transM.get(e);

            batch.draw(texture.texture,
                    trans.pos.x - texture.originX,
                    trans.pos.y - texture.originY);
        }

        batch.end();
        queue.clear();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        queue.add(entity);
    }
}
