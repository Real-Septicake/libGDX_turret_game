package com.mygdx.game.rounds;

import com.mygdx.game.World;
import com.mygdx.game.entities.enemies.EnemyRoot;

import java.util.List;

public class Spawn {
    /**
     * What condition must be met for the next spawns to start
     */
    public enum Type {
        /** Next spawns start immediately */
        INSTANT,
        /** Next spawns start after this one has finished */
        FINISH,
        /** Next spawns start after a delay */
        DELAY
    }
    /**
     * This spawn's type
     */
    public final Type type;
    /**
     *  Time until next spawn is started
     */
    public final float delay;
    public final List<Spawn> next;

    /**
     * Type of enemy created by this spawn
     */
    public final EnemyRoot enemy;
    /**
     * Number of enemies created by this spawn
     */
    public final int count;
    /**
     * Time between spawning enemies
     */
    public final float spacing;

    private boolean started = false;
    private boolean delayOver = false;
    private int spawned = 0;
    private float timer = 0f;

    private Spawn(Type type, float delay, List<Spawn> next, EnemyRoot enemy, int count, float spacing) {
        this.type = type;
        this.delay = delay;
        this.next = next;
        this.enemy = enemy;
        this.count = count;
        this.spacing = spacing;
    }

    public static Spawn instant(EnemyRoot enemy, int count, float spacing, List<Spawn> next) {
        assert enemy != null;
        assert count > 0;
        assert spacing > 0;
        return new Spawn(Type.INSTANT, 0, next, enemy, count, spacing);
    }

    public static Spawn finish(EnemyRoot enemy, int count, float spacing, List<Spawn> next) {
        assert enemy != null;
        assert count > 0;
        assert spacing > 0;
        return new Spawn(Type.FINISH, 0, next, enemy, count, spacing);
    }

    public static Spawn delay(EnemyRoot enemy, int count, float spacing, float delay, List<Spawn> next) {
        assert enemy != null;
        assert count > 0;
        assert spacing > 0;
        assert delay > 0;
        return new Spawn(Type.DELAY, delay, next, enemy, count, spacing);
    }

    public static class ProcessResult {
        public boolean started;
        public boolean delayOver;
        public boolean finished;

        protected ProcessResult() {}
    }

    public ProcessResult process(World world, float delta) {
        ProcessResult result = new ProcessResult();
        if(timer == 0) result.started = true;
        timer += delta;
        if(timer >= delay && !delayOver) {
            result.delayOver = true;
            delayOver = true;
        }
        if(timer - spawned * spacing >= spacing) {
            world.createEnemy(enemy);
            spawned++;
        }
        if(count == spawned) result.finished = true;
        return result;
    }
}
