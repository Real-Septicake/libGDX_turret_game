package com.mygdx.game.entities.enemies;

import com.mygdx.game.World;

public interface EnemyListener {
    void onDamage(float damage);
    void onDeath(World world, float progress);
}
