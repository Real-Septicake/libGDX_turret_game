package com.mygdx.game.rounds;

import com.mygdx.game.World;
import java.util.*;

public class Round {
    private final List<Spawn> spawns = new ArrayList<>();
    private final List<Spawn> queue = new ArrayList<>();

    public Round(Spawn init) {
        spawns.add(init);
    }

    public void update(float delta, World world) {
        Iterator<Spawn> itr = spawns.iterator();
        Spawn spawn;
        while (itr.hasNext()) {
            spawn = itr.next();
            Spawn.ProcessResult result = spawn.process(world, delta);
            if(spawn.type == Spawn.Type.INSTANT && result.started)
                queue.addAll(spawn.next);
            if(spawn.type == Spawn.Type.DELAY && result.delayOver)
                queue.addAll(spawn.next);
            if(spawn.type == Spawn.Type.FINISH && result.finished)
                queue.addAll(spawn.next);

            if(spawn.finished && spawn.delayOver)
                itr.remove();
        }
        spawns.addAll(queue);
        queue.clear();
    }

    /**
     * @return Whether all the spawns associated with this round have completed
     */
    public boolean finished() {
        return spawns.isEmpty();
    }
}
