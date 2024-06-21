package com.mygdx.game.rounds;

import com.mygdx.game.World;

import java.util.ArrayList;
import java.util.List;

public class Round {
    private final List<Spawn> spawns = new ArrayList<>();

    public Round(Spawn init) {
        spawns.add(init);
    }

    public void update(float delta, World world) {
        for(Spawn spawn : spawns) {
            spawn.process(world, delta);
        }
    }
}
