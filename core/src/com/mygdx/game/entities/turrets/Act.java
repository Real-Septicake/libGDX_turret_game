package com.mygdx.game.entities.turrets;

import com.badlogic.ashley.core.Entity;

@FunctionalInterface
public interface Act {
    void act(Entity turret, Entity target, float delta);
}
