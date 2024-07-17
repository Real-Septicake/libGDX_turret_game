package com.mygdx.game.entities.enemies;

import com.mygdx.game.World;
import com.mygdx.game.components.EnemyComponent;

public interface EnemyListener {
    void onDamage(float damage);
    void onDeath(World world, float progress);
}
