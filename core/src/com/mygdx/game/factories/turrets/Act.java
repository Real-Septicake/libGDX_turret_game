package com.mygdx.game.factories.turrets;

import com.badlogic.ashley.core.Entity;

@FunctionalInterface
public interface Act {
    void act(Entity turret, Entity target, float delta);
}
