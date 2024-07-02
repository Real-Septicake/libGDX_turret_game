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
    /**
     * The spawns to start after this
     */
    public final List<Spawn> next;
    /**
     * The spawn that started this instance
     */
    public Spawn prev;

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

    /**
     * If this instance has spawned all of its enemies
     */
    public boolean finished = false;
    /**
     * If this instance's delay is over
     */
    public boolean delayOver = false;
    /**
     * The number of enemies this instance has spawned
     */
    private int spawned = 0;
    /**
     * The amount of time this spawn has been active
     */
    private float timer = 0;

    private Spawn(Type type, float delay, List<Spawn> next, EnemyRoot enemy, int count, float spacing) {
        this.type = type;
        this.delay = delay;
        this.next = next;
        this.enemy = enemy;
        this.count = count;
        this.spacing = spacing;
        for(Spawn n : next) {
            n.prev = this;
        }
    }

    public void add(Spawn s) {
        s.prev = this;
        next.add(s);
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
        public boolean started = false;
        public boolean delayOver = false;
        public boolean finished = false;

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
        if(!finished && timer - spawned * spacing >= spacing) {
            world.createEnemy(enemy);
            spawned++;
        }
        if(count == spawned) {
            result.finished = true;
            finished = true;
        }
        return result;
    }

    @Override
    public String toString() {
        return "Spawn{" +
                "type=" + type.name() +
                ", delay=" + delay +
                ", enemy=" + enemy +
                ", count=" + count +
                ", spacing=" + spacing +
                '}';
    }
}
