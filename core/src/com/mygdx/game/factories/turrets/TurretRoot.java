package com.mygdx.game.factories.turrets;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.components.*;

public abstract class TurretRoot {
    protected static final ComponentMapper<StateComponent> stateM = ComponentMapper.getFor(StateComponent.class);

    protected static Entity create(Act act, TextureRegion initGun, Texture base,
                                   float originX, float originY, float x, float y,
                                   float centerX, float centerY, float range, float damage) {
        Entity e = new Entity();

        TurretTextureComponent tex = new TurretTextureComponent();
        tex.turretGun = initGun;
        tex.turretBase = base;
        tex.originX = originX;
        tex.originY = originY;
        tex.centerX = centerX;
        tex.centerY = centerY;
        e.add(tex);

        TransformComponent trans = new TransformComponent();
        trans.pos.x = x;
        trans.pos.y = y;
        e.add(trans);

        RectBoundsComponent bounds = new RectBoundsComponent();
        bounds.bounds.x = x;
        bounds.bounds.y = y;
        bounds.bounds.width = base.getWidth();
        bounds.bounds.height = base.getHeight();
        e.add(bounds);

        TurretComponent turret = new TurretComponent();
        turret.range = range;
        turret.damage = damage;
        turret.act = act;
        e.add(turret);

        e.add(new BuyingComponent());
        e.add(new StateComponent());

        return e;
    }
}
