package com.mygdx.game.factories.enemies;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.components.EnemyComponent;
import com.mygdx.game.components.EnemyTextureComponent;
import com.mygdx.game.components.TransformComponent;

public class EnemyRoot {

    protected static Entity create(Texture texture, float originX, float originY, float speed, int maxHealth, int value) {
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
