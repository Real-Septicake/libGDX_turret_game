package com.mygdx.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.TurretGame;
import com.mygdx.game.components.*;

public class TurretSystem extends IteratingSystem {
    Engine engine;
    Camera cam;

    private static final ComponentMapper<TransformComponent> transM = ComponentMapper.getFor(TransformComponent.class);

    private static final ComponentMapper<TurretTextureComponent> turretTexM = ComponentMapper.getFor(TurretTextureComponent.class);
    private static final ComponentMapper<TurretComponent> turretM = ComponentMapper.getFor(TurretComponent.class);
    private static final ComponentMapper<BuyingComponent> buyingM = ComponentMapper.getFor(BuyingComponent.class);
    private static final ComponentMapper<RectBoundsComponent> boundsM = ComponentMapper.getFor(RectBoundsComponent.class);

    private static final ComponentMapper<EnemyComponent> enemyM = ComponentMapper.getFor(EnemyComponent.class);

    private static final Family turretFamily = Family.all(TurretComponent.class).exclude(BuyingComponent.class).get();
    private static final Family enemyFamily = Family.all(EnemyComponent.class).exclude(EnemyDisposeComponent.class).get();

    static Vector2 close = new Vector2();
    static Vector3 touch = new Vector3();

    public TurretSystem(Camera camera) {
        super(Family.all(TurretComponent.class).get());

        cam = camera;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);

        this.engine = engine;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if(isBuying(entity)) {
            cam.unproject(touch.set(Gdx.input.getX(), Gdx.input.getY(), 0));
            TurretTextureComponent tex = turretTexM.get(entity);
            setLocation(entity, touch.x - tex.centerX, touch.y - tex.centerY);
        } else {
            Entity target = target(entity);
            TurretComponent turret = turretM.get(entity);
            turret.act.act(entity, target, deltaTime);
        }
    }

    public static void setLocation(Entity entity, float x, float y) {
        TransformComponent trans = transM.get(entity);

        trans.pos.x = x;
        trans.pos.y = y;

        RectBoundsComponent b = boundsM.get(entity);
        b.bounds.x = x;
        b.bounds.y = y;
    }

    public Entity target(Entity entity) {
        TransformComponent pos = transM.get(entity);
        TurretComponent turret = turretM.get(entity);
        float max = turret.range * turret.range;

        ImmutableArray<Entity> enemies = engine.getEntitiesFor(enemyFamily);
        Entity target = null;
        EnemyComponent targetInfo = null;
        float targetDist = 0;

        for(Entity e : enemies) {
            TransformComponent trans = transM.get(e);
            float dist = pos.pos.dst2(trans.pos);

            if(dist > max)
                continue;

            switch(turret.targeting){
                case TurretComponent.FIRST -> {
                    if (target == null){
                        target = e;
                        targetInfo = enemyM.get(e);
                    } else if(targetInfo.current < enemyM.get(e).current) {
                        target = e;
                        targetInfo = enemyM.get(e);
                    }
                }
                case TurretComponent.LAST -> {
                    if (target == null) {
                        target = e;
                        targetInfo = enemyM.get(e);
                    } else if (targetInfo.current > enemyM.get(e).current) {
                        target = e;
                        targetInfo = enemyM.get(e);
                    }
                }
                case TurretComponent.CLOSE -> {
                    if (target == null) {
                        target = e;
                        targetInfo = enemyM.get(e);
                        targetDist = pos.pos.dst2(trans.pos);
                    } else if (targetDist > pos.pos.dst2(trans.pos)) {
                        target = e;
                        targetDist = pos.pos.dst2(trans.pos);
                    }
                }
                case TurretComponent.FAR -> {
                    if (target == null) {
                        target = e;
                        targetInfo = enemyM.get(e);
                        targetDist = pos.pos.dst2(trans.pos);
                    } else if (targetDist < pos.pos.dst2(trans.pos)) {
                        target = e;
                        targetDist = pos.pos.dst2(trans.pos);
                    }
                }
            }
        }

        return target;
    }

    private boolean isBuying(Entity entity) {
        BuyingComponent buy = buyingM.get(entity);

        // If the component doesn't exist then it's not being bought
        // Otherwise check if it can currently be placed
        if(buy != null) {
            buy.canPlace = canPlace(entity);
            return true;
        }
        return false;
    }

    public boolean canPlace(Entity entity) {
        //Check if outside world
        RectBoundsComponent bound = boundsM.get(entity);
        if(!TurretGame.WORLD_BOUNDS.contains(bound.bounds))
            return false;

        // Check if overlapping any turrets
        ImmutableArray<Entity> turrets = engine.getEntitiesFor(turretFamily);
        for(Entity e : turrets) {
            if(boundsM.get(e).bounds.overlaps(bound.bounds))
                return false;
        }

        // Check if too close to path
        TransformComponent trans = transM.get(entity);
        TurretTextureComponent tex = turretTexM.get(entity);
        close.x = trans.pos.x + tex.originX;
        close.y = trans.pos.y + tex.originY;
        int max = (int) (TurretGame.spline.approximate(close) * TurretGame.resolution);
        if(max == 0)
            return false;
        for(int i = 0; i < TurretGame.points.length; i++) {
            if(overlap(TurretGame.points[i], 50, bound.bounds, close.x, close.y))
                return false;
        }

        return true;
    }

    // Algorithm taken from https://stackoverflow.com/a/402010
    private static boolean overlap(Vector2 point, float radius, Rectangle bounds, float x, float y) {
        float circDistX = Math.abs(point.x - x);
        float circDistY = Math.abs(point.y - y);
        if(circDistX > bounds.width/2 + radius || circDistY > bounds.height/2 + radius)
            return false;
        if(circDistX <= bounds.width/2 || circDistY <= bounds.height/2)
            return true;

        float circDistSq = (circDistX - bounds.width/2) * (circDistX - bounds.width/2) +
                (circDistY - bounds.height/2) * (circDistY - bounds.height/2);

        return circDistSq <= radius * radius;
    }
}
